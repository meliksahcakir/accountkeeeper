package com.meliksahcakir.accountkeeper.login

import android.content.Intent
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.meliksahcakir.accountkeeper.utils.Result
import kotlinx.coroutines.tasks.await

class LoginRepository {

    companion object {
        @Volatile
        private var INSTANCE: LoginRepository? = null
        private var firebaseAuth: FirebaseAuth? = null

        fun getInstance(): LoginRepository {
            synchronized(this) {
                return INSTANCE ?: LoginRepository().also {
                    INSTANCE = it
                    firebaseAuth = FirebaseAuth.getInstance()
                }
            }
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            firebaseAuth?.signInWithEmailAndPassword(email, password)?.await()
            retrieveUserFromFirebaseAuth(firebaseAuth)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun login(data: Intent): Result<FirebaseUser> {
        return try {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                firebaseAuth?.signInWithCredential(credential)?.await()
            }
            retrieveUserFromFirebaseAuth(firebaseAuth)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            firebaseAuth?.createUserWithEmailAndPassword(email, password)?.await()
            retrieveUserFromFirebaseAuth(firebaseAuth)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getUser(): FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            firebaseAuth?.useAppLanguage()
            firebaseAuth?.sendPasswordResetEmail(email)?.await()
            return Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun retrieveUserFromFirebaseAuth(firebaseAuth: FirebaseAuth?): Result<FirebaseUser> {
        return if (firebaseAuth != null) {
            val user = firebaseAuth.currentUser
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("User null"))
            }
        } else {
            Result.Error(Exception("Auth null"))
        }
    }
}