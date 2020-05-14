Drivable
========

Gradle
------
[![](https://jitpack.io/v/eholtrop/drivable.svg)](https://jitpack.io/#eholtrop/drivable)

Project build.gradle
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Module build.gradle
```
dependencies {
        implementation 'com.github.eholtrop:drivable:x.y.z'
}
```

Why
---

When using Rx in an Android application with a MVVM pattern. But one of the struggles I came across was the mechanism that was used to pass information from the View to the ViewModel.

I found two strategies that worked well. but they both had their pros and cons

1. Pass view actions via an interface
- while this works well. it requires some sort of "initialization" function on the viewmodel (after the views themselves have been intiialized). meaning things like null pointer exceptions are possible if the viewmodel isnt properly initialized before subscribing to outputs
- a view model should almost be a "state machine" ie. "given these inputs. I will give these outputs". having objects build themselves without dependencies is a great goal to strive for

2. Use subjects internally in the ViewModel to propogate events
- this seems to solve most of the issues with pt 1. but it exposes another extremely dangerous effect. you expose your inputs as subjects. meaning anything can "onNext" your inputs. which removes the beauty of RxJava. wherein you have single inputs which directly map to inputs.

Enter Drivable!

Drivable allows the view to "drive" observables within the viewmodel. without any extra setup needed. This way the ViewModel can set itself up as needed, acting more of a state machine instead of something that requires view initialization to function properly. And the activity can subscribe to outputs without worrying about null pointer

ex.

Activity:

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
                .observeOn(AndroidSchedulers.mainThread()) //required due to bug in RxBinding
                .subscribe { binding.email.error = it }
        )

        disposeBag.add(
            viewModel.passwordError
                .observeOn(AndroidSchedulers.mainThread()) //required due to bug in RxBinding
                .subscribe { binding.password.error = it }
        )

        disposeBag.add(
            viewModel.loginSuccessful
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show() }
        )
    }

ViewModel:

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

This allows for easy binding within your views. **as well as** ensuring taht your viewModel is not exposing any nasty subjects publically.

Drivable also has some hidden benefits.

Drivables are only subscribed to when subscribe is called on the Drivable itself. meaning no unnecessary subscriptions are created when all you are doing is simple binding to an input. your only create subscriptions when you actually use the output values! (which means less disposable management... which we all want)