package com.eholtrop.drivable

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable


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

class Drivable<T> : Observable<T>() {
    private var driver: Observable<T>? = null
    private val observers = mutableListOf<Observer<in T>>()
    private val sourceDisposables = CompositeDisposable()

    /**
     * Drive the drivable with a given observable driver. if observers exist create the necessary
     * observer/driver subscription
     *
     * If the drivable is already being driven (driver is not null). clean up existing subscriptions and rebind
     */
    @Synchronized
    internal fun driveWith(driver: Observable<T>): Observable<T> {
        sourceDisposables.clear()
        this.driver = driver
        observers.forEach { obs ->
            bind(obs, driver)
        }
        return driver
    }

    /**
     * on subscribe add observer to observer list. if driver is non-null bind observer/driver subscription
     */
    override fun subscribeActual(observer: Observer<in T>) {
        observers.add(observer)
        driver?.let {
            bind(observer, it)
        }
    }

    /**
     * Bind the observer and driver together. pass all onNext/onError/onComplete events to the observer
     * bind this subscription to the observer onSubscribe lifecycle
     * add subscription to the disposable list incase early cleanup is required
     */
    private fun bind(observer: Observer<in T>, driver: Observable<T>) {
        val disposable = driver
            .doOnDispose { observers.remove(observer) }
            .subscribe({ observer.onNext(it) }, {
                observer.onError(it)
                observers.remove(observer)
            }, {
                observer.onComplete()
                observers.remove(observer)

            })
        observer.onSubscribe(disposable)
        sourceDisposables.add(disposable)
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