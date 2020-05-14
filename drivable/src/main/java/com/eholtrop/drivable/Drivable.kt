package com.avianapps.drivable

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import java.util.*


class Drivable<T> : Observable<T>() {
    private var source: Observable<T>? = null
    private val observers = mutableListOf<Observer<in T>>()
    private val sourceDisposables = CompositeDisposable()

    @Synchronized
    internal fun driveWith(source: Observable<T>): Observable<T> {
        sourceDisposables.clear()
        this.source = source
        observers.forEach { obs ->
            bind(obs, source)
        }
        return source
    }

    override fun subscribeActual(observer: Observer<in T>) {
        observers.add(observer)
        source?.let {
            bind(observer, it)
        }
    }

    private fun bind(observer: Observer<in T>, source: Observable<T>) {
        val disposable = source.subscribe({ observer.onNext(it) }, { observer.onError(it) }, { observer.onComplete() })
        observer.onSubscribe(disposable)
        sourceDisposables.add(disposable)
    }
}

fun <T> Observable<T>.drive(drivable: Drivable<T>): Observable<T> {
    return drivable.driveWith(this)
}