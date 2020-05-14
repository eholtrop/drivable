package com.avianapps.drivable

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject


class DriveSubject<T>(
        internal val subject: Subject<T>
) {
    fun asObservable() = subject.hide()
}

fun <T> Observable<T>.drive(driveSubject: DriveSubject<T>): Disposable {
    return this.subscribe { driveSubject.subject.onNext(it) }
}

fun <T> Single<T>.drive(driveSubject: DriveSubject<T>): Disposable {
    return this.doOnSuccess { driveSubject.subject.onNext(it) }.subscribe()
}

fun <T> Flowable<T>.drive(driveSubject: DriveSubject<T>): Disposable {
    return this.subscribe { driveSubject.subject.onNext(it) }
}

fun <T> Maybe<T>.drive(driveSubject: DriveSubject<T>): Disposable {
    return this.subscribe { driveSubject.subject.onNext(it) }
}