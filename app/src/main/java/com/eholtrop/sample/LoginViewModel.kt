package com.eholtrop.sample

import com.eholtrop.drivable.Drivable
import io.reactivex.rxjava3.kotlin.withLatestFrom

class LoginViewModel {

    val input = Input()

    inner class Input {
        val email = Drivable<CharSequence>()
        val password = Drivable<CharSequence>()
        val loginClicked = Drivable<Unit>()
    }

    val emailError = input.email.map { if (it.isNullOrBlank()) "Enter an email" else "" }

    val passwordError =
        input.password.map { if (it.isNullOrBlank()) "Enter a valid password" else "" }

    val loginSuccessful = input.loginClicked
        .withLatestFrom(
            input.email,
            input.password
        ) { _, email, password -> (email.isNotBlank() || password.isNotBlank()) }
        .filter { it }

}