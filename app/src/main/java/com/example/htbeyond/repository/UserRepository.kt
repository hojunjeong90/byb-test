package com.example.htbeyond.repository

import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthFetchSessionOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

object UserRepository {


    val session = Amplify.Auth.fetchAuthSession({
        Log.i("AmplifyQuickstart", "Auth session = $it")
    }, {
        Log.e("AmplifyQuickstart", "Failed to fetch auth session", it)
    })

    fun runAuthProcess(){
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), "saycreed1114@naver.com")
            .build()
        val result = Amplify.Auth.signUp(
            "saycreed1114@naver.com",
            "Password123",
            options,
            { Log.i("AuthQuickStart", "Result: $it") },
            {
                Log.e("AuthQuickStart", "Sign up failed", it)
            })

        Amplify.Auth.confirmSignUp(
            "saycreed1114@naver.com", "the code you received via email",
            { result ->
                if (result.isSignUpComplete) {
                    Log.i("AuthQuickstart", "Confirm signUp succeeded")
                } else {
                    Log.i("AuthQuickstart","Confirm sign up not complete")
                }
            },
            { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
        )
    }
}