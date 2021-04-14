package com.eholtrop.drivable

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.Subject


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