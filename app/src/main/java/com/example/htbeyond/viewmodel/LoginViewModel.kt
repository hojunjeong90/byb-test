package com.example.htbeyond.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class LoginViewModel(application: Application) : AndroidViewModel(application) {


    fun signUp() {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), "my@email.com")
            .build()
        Amplify.Auth.signUp("username", "Password123", options,
            { Log.i("AuthQuickStart", "Sign up succeeded: $it") },
            { Log.e("AuthQuickStart", "Sign up failed", it) }
        )
    }

    fun confirmSignUp() {
        Amplify.Auth.confirmSignUp(
            "username", "the code you received via email",
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart", "Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }

    fun signIn() {
        Amplify.Auth.signIn("username", "password",
            { result ->
                if (result.isSignedIn) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to sign in", it) }
        )
    }

    fun socialSignIn() {
        Amplify.Auth.signInWithSocialWebUI(
            AuthProvider.facebook(),
            getApplication(),
            { Log.i("AuthQuickstart", "Sign in OK: $it") },
            { Log.e("AuthQuickstart", "Sign in failed", it) }
        )
    }
}