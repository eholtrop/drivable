package com.eholtrop.sample

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avianapps.drivable.drive
import com.eholtrop.sample.databinding.ActivityMainBinding
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class LoginActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    val viewModel = LoginViewModel()

    val disposeBag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.email.textChanges()
            .drive(viewModel.input.email)

        binding.password.textChanges()
            .drive(viewModel.input.password)

        binding.login.clicks()
            .drive(viewModel.input.loginClicked)
    }

    override fun onStart() {
        super.onStart()

        disposeBag.add(
            viewModel.emailError
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.email.error = it }
        )

        disposeBag.add(
            viewModel.passwordError
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { binding.password.error = it }
        )

        disposeBag.add(
            viewModel.loginSuccessful
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show() }
        )
    }

    override fun onStop() {
        super.onStop()
        disposeBag.clear()
    }
}