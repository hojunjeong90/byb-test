package com.example.htbeyond.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.auth.cognito.options.AuthFlowType
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.example.htbeyond.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Amplify.Auth.fetchAuthSession({
            Log.i("AmplifyQuickstart", "Auth session = $it")
        }, {
            Log.e("AmplifyQuickstart", "Failed to fetch auth session", it)
        })

        Amplify.Auth.getCurrentUser({
            Log.i("AmplifyQuickstart", "Auth User = $it")
        },{
            Log.e("AmplifyQuickstart", "Failed to getCurrentUser", it)
        })
        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
            when (event.name) {
                InitializationStatus.SUCCEEDED.toString() ->
                    Log.i("AuthQuickstart", "Auth successfully initialized")
                InitializationStatus.FAILED.toString() ->
                    Log.i("AuthQuickstart", "Auth failed to succeed")
                else -> when (AuthChannelEventName.valueOf(event.name)) {
                    AuthChannelEventName.SIGNED_IN ->
                        Log.i("AuthQuickstart", "Auth just became signed in")
                    AuthChannelEventName.SIGNED_OUT ->
                        Log.i("AuthQuickstart", "Auth just became signed out")
                    AuthChannelEventName.SESSION_EXPIRED ->
                        Log.i("AuthQuickstart", "Auth session just expired")
                    AuthChannelEventName.USER_DELETED ->
                        Log.i("AuthQuickstart", "User has been deleted")
                    else ->
                        Log.w("AuthQuickstart", "Unhandled Auth Event: ${event.name}")
                }
            }
        }

        findViewById<AppCompatButton>(R.id.loginButton).setOnClickListener {
            runSignInProcess()
        }
    }

    fun runSignInProcess(){
        try {
            Amplify.Auth.confirmSignUp(
                "saycreed1114@naver.com",
                "535543",
                { result ->
                    Log.i("AuthQuickstart", "Confirm signUp result completed: ${result.isSignUpComplete}")
                }
            ) { error ->
                Log.e("AuthQuickstart", "An error occurred while confirming sign up: $error")
            }
        } catch (error: Exception) {
            Log.e("AuthQuickstart", "unexpected error: $error")
        }

        val options = AWSCognitoAuthSignInOptions.builder().authFlowType(AuthFlowType.USER_SRP_AUTH).build()
        try {
            Amplify.Auth.signIn(
                "saycreed1114@naver.com",
                "Password123",
                options,
                { result ->
                    val nextStep  = result.nextStep
                    when(nextStep.signInStep){
                        AuthSignInStep.CONFIRM_SIGN_IN_WITH_SMS_MFA_CODE -> {
                            Log.i("AuthQuickstart", "SMS code sent to ${nextStep.codeDeliveryDetails?.destination}")
                            Log.i("AuthQuickstart", "Additional Info ${nextStep.additionalInfo}")
                            // Prompt the user to enter the SMS MFA code they received
                            // Then invoke `confirmSignIn` api with the code
                        }
                        AuthSignInStep.CONFIRM_SIGN_IN_WITH_CUSTOM_CHALLENGE -> {
                            Log.i("AuthQuickstart","Custom challenge, additional info: ${nextStep.additionalInfo}")
                            // Prompt the user to enter custom challenge answer
                            // Then invoke `confirmSignIn` api with the answer
                        }
                        AuthSignInStep.CONFIRM_SIGN_IN_WITH_NEW_PASSWORD -> {
                            Log.i("AuthQuickstart", "Sign in with new password, additional info: ${nextStep.additionalInfo}")
                            // Prompt the user to enter a new password
                            // Then invoke `confirmSignIn` api with new password
                        }
                        AuthSignInStep.RESET_PASSWORD -> {
                            Log.i("AuthQuickstart", "Reset password, additional info: ${nextStep.additionalInfo}")
                            // User needs to reset their password.
                            // Invoke `resetPassword` api to start the reset password
                            // flow, and once reset password flow completes, invoke
                            // `signIn` api to trigger signIn flow again.
                        }
                        AuthSignInStep.CONFIRM_SIGN_UP -> {
                            Log.i("AuthQuickstart", "Confirm signup, additional info: ${nextStep.additionalInfo}")
                            // User was not confirmed during the signup process.
                            // Invoke `confirmSignUp` api to confirm the user if
                            // they have the confirmation code. If they do not have the
                            // confirmation code, invoke `resendSignUpCode` to send the
                            // code again.
                            // After the user is confirmed, invoke the `signIn` api again.
                        }
                        AuthSignInStep.DONE -> {
                            Log.i("AuthQuickstart", "SignIn complete")
                            // User has successfully signed in to the app
                        }
                    }

                }
            ) { error ->
                Log.e("AuthQuickstart", "SignIn failed: $error")
            }
        } catch (error: Exception) {
            Log.e("AuthQuickstart", "Unexpected error occurred: $error")
        }
    }
}