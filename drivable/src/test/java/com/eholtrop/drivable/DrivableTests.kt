package com.eholtrop.drivable

import com.avianapps.drivable.Drivable
import com.avianapps.drivable.drive
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import junit.framework.Assert.assertTrue
import org.junit.Test

class DrivableTests {

    @Test
    fun drivableOutput_SingleDriverWithNoEvents_EmptyOutput() {
        Drivable<Unit>()
            .driveWith(Observable.never())
            .test()
            .assertEmpty()
    }

    @Test
    fun drivableOutput_SingleDriverWithOneEvent_SingleOutput() {
        Drivable<Unit>()
            .driveWith(Observable.just(Unit))
            .test()
            .assertValue(Unit)
    }


    @Test
    fun drivableOutput_SingleDriverWithManyEvents_ManyOutputs() {
        Drivable<Unit>()
            .driveWith(Observable.just(Unit, Unit, Unit))
            .test()
            .assertValues(Unit, Unit, Unit)
    }

    @Test
    fun testSingleDriver_NoSubscription_DriverHasNoObservers() {
        val drivable = Drivable<Unit>()
        val driver = BehaviorSubject.create<Unit>()

        driver.drive(drivable)

        assertTrue(!driver.hasObservers())
    }

    @Test
    fun testSingleDriver_SingleSubscription_DriverHasObserver() {
        val drivable = Drivable<Unit>()
        val driver = BehaviorSubject.create<Unit>()

        driver.drive(drivable)
            .subscribe()

        assertTrue(driver.hasObservers())
    }

    @Test
    fun testSingleDriver_SingleSubscription_SubscriptionDisposed_DriverHasNoObservers() {
        val drivable = Drivable<Unit>()
        val driver = BehaviorSubject.create<Unit>()

        driver.drive(drivable)
            .subscribe()
            .dispose()

        assertTrue(!driver.hasObservers())
    }

    @Test
    fun testTwoDrivers_firstDriverNoObservers_secondDriverHasObservers() {
        val driver1 = BehaviorSubject.create<String>()
        val driver2 = BehaviorSubject.create<String>()

        val drivable = Drivable<String>()

        driver1.drive(drivable)
        driver2.drive(drivable)

        assertTrue(
            "Assert subject1 does not have observers",
            !driver1.hasObservers()
        )

        assertTrue(
            "Assert subject2 has observers",
            !driver2.hasObservers()
        )
    }

    @Test
    fun singleDriver_MultipleSubscriptions_SingleEvent_AllSubscriptionsEmitEvent() {
        val driver1 = BehaviorSubject.createDefault(Unit)

        val drivable = Drivable<Unit>()

        driver1.drive(drivable)

        drivable.test().assertValue(Unit)
        drivable.test().assertValue(Unit)
    }

    @Test
    fun singleDriver_MultipleSubscriptions_MultipleEvents_AllSubscriptionsEmitEvents() {
        val driver1 = ReplaySubject.create<Unit>()
        driver1.onNext(Unit)
        driver1.onNext(Unit)

        val drivable = Drivable<Unit>()

        driver1.drive(drivable)

        drivable.test().assertValues(Unit, Unit)
        drivable.test().assertValues(Unit, Unit)
    }

    @Test
    fun driveAfterMultipleSubscriptions_NoCrash() {
        val driver = PublishSubject.create<Unit>()

        val drivable = Drivable<Unit>()

        drivable.subscribe()
        drivable.subscribe()
        drivable.subscribe()

        driver.drive(drivable)
    }

    @Test
    fun driveBeforeAndAfterMultipleSubscriptions_NoCrash() {
        val driver = PublishSubject.create<Unit>()

        val drivable = Drivable<Unit>()

        driver.drive(drivable)

        drivable.subscribe()

        driver.drive(drivable)

        assertTrue(driver.hasObservers())
    }

}