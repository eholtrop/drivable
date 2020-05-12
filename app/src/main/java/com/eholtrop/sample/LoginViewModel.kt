package com.eholtrop.sample

import android.util.Log
import com.avianapps.drivable.Drivable
import io.reactivex.rxkotlin.withLatestFrom

class LoginViewModel {

    val input = Input()

    inner class Input {
        val email = Drivable<CharSequence>()
        val password = Drivable<CharSequence>()
        val loginClicked = Drivable<Unit>()
    }

    val emailError = input.email.map { if (it.isNullOrBlank()) "Enter an email" else "" }

    val passwordError = input.password.map { if (it.isNullOrBlank()) "Enter a valid password" else "" }

    val loginSuccessful = input.loginClicked
        .doOnNext { Log.d("Debug", "test") }
        .withLatestFrom(input.email, input.password) { _, email, password ->
            return@withLatestFrom (email.isNotBlank() || password.isNotBlank())
        }
        .filter { it }

}