package com.avianapps.drivable

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.HashMap

/**
 * Drivable
 * --------
 * An observable that can be driven by another observable. You can think of this as a Subject that
 * can be 'onNext' with an observable
 *
 * The driver observable is not subscribed to until the drivable itself is subscribed to. so unless
 * your callsite is also performaing a subscription. there is no disposable cleanup necessary
 *
 * The Drivable can only be driven once. if driven a second time the original subscriptions that
 * are bound to the original driver will be cleaned up. and new subscriptions with the new driver
 * will be created
 */

class IdObserver<T>(
    val id: String = UUID.randomUUID().toString(),
    val observer: Observer<T>
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is IdObserver<*> -> this.id == other.id
            else -> false
        }
    }
}

class Drivable<T> : Observable<T>() {
    private var driver: Observable<T>? = null
    private val sourceDisposables = hashMapOf<Observer<in T>, List<Disposable>>()

    /**
     * Drive the drivable with a given observable driver. if observers exist create the necessary
     * observer/driver subscription
     *
     * If the drivable is already being driven (driver is not null). clean up existing subscriptions and rebind
     */
    @Synchronized
    internal fun driveWith(driver: Observable<T>): Observable<T> {
        this.driver = driver
        sourceDisposables.keys.forEach { obs ->
            bind(obs, driver)
        }
        return driver
    }

    /**
     * on subscribe add observer to observer list. if driver is non-null bind observer/driver subscription
     */
    override fun subscribeActual(observer: Observer<in T>) {
        if(!sourceDisposables.containsKey(observer)) {
            sourceDisposables[observer] = emptyList()
        }
        driver?.let {
            observer.onSubscribe(bind(observer, it))
        }
    }

    /**
     * Bind the observer and driver together. pass all onNext/onError/onComplete events to the observer
     * bind this subscription to the observer onSubscribe lifecycle
     * add subscription to the disposable list incase early cleanup is required
     */
    private fun bind(observer: Observer<in T>, driver: Observable<T>): Disposable {
        val disposable = driver
            .doOnDispose { cleanObserver(observer) }
            .subscribe(
                { observer.onNext(it) }, {
                    observer.onError(it)
                    cleanObserver(observer)
                }, {
                    observer.onComplete()
                    cleanObserver(observer)

                })

        sourceDisposables[observer] =
            (sourceDisposables[observer] ?: emptyList()) + listOf(disposable)
        return disposable
    }

    private fun cleanObserver(observer: Observer<in T>) {
        sourceDisposables[observer]?.filter { !it.isDisposed }?.forEach { it.dispose() }
        sourceDisposables.remove(observer)
    }
}

/**
 * QOL function to allow for easy chaining on Observables
 *
 * ex. Observable.just(Unit).drive(drivable)
 */
fun <T> Observable<T>.drive(drivable: Drivable<T>): Observable<T> {
    return drivable.driveWith(this)
}