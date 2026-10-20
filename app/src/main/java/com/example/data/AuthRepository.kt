package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
  data class LoggedIn(val email: String, val uid: String, val displayName: String = "") : AuthUiState()
  object LoggedOut : AuthUiState()
}

class AuthRepository {

  fun getFirebaseAuth(context: Context? = null): FirebaseAuth? {
    try {
      return FirebaseAuth.getInstance()
    } catch (e: Exception) {
      Log.w("AuthRepository", "Default FirebaseApp not found, initializing programmatically", e)
      try {
        if (context != null && FirebaseApp.getApps(context).isEmpty()) {
          val options = FirebaseOptions.Builder()
            .setApplicationId("1:508672273546:android:16dd365c30905ea5c05173")
            .setApiKey("AIzaSyAEFKvjHCP0tggz03iv4ZxViiEXi4zbERA")
            .setProjectId("order-tracking-ec763")
            .setDatabaseUrl("https://order-tracking-ec763-default-rtdb.firebaseio.com")
            .setStorageBucket("order-tracking-ec763.firebasestorage.app")
            .build()
          FirebaseApp.initializeApp(context.applicationContext, options)
          return FirebaseAuth.getInstance()
        }
      } catch (initErr: Exception) {
        Log.e("AuthRepository", "Programmatic FirebaseApp initialization failed", initErr)
      }
      return null
    }
  }

  private val _authState = MutableStateFlow<AuthUiState>(
    getFirebaseAuth()?.currentUser?.let { user ->
      AuthUiState.LoggedIn(user.email ?: "User", user.uid, user.displayName ?: "")
    } ?: AuthUiState.LoggedOut
  )
  val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

  val currentUser: FirebaseUser?
    get() = getFirebaseAuth()?.currentUser

  suspend fun login(email: String, password: String): Result<String> {
    val firebaseAuth = getFirebaseAuth() ?: return Result.failure(Exception("Cloud Auth service initializing. Please retry in a moment."))
    return try {
      val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
      val user = result.user
      val userEmail = user?.email ?: email
      _authState.value = AuthUiState.LoggedIn(userEmail, user?.uid ?: "", user?.displayName ?: "")
      Result.success(userEmail)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun signup(email: String, password: String): Result<String> {
    val firebaseAuth = getFirebaseAuth() ?: return Result.failure(Exception("Cloud Auth service initializing. Please retry in a moment."))
    return try {
      val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
      val user = result.user
      val userEmail = user?.email ?: email
      _authState.value = AuthUiState.LoggedIn(userEmail, user?.uid ?: "", user?.displayName ?: "")
      Result.success(userEmail)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  suspend fun signInWithGoogle(context: Context): Result<String> {
    val firebaseAuth = getFirebaseAuth(context) ?: return Result.failure(Exception("Cloud Auth service initializing. Please retry in a moment."))
    return try {
      val credentialManager = CredentialManager.create(context)
      val clientId = "508672273546-mb8s1o2e01q4hjbf4icuo0a39h34q8m6.apps.googleusercontent.com"

      val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(false)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val result = credentialManager.getCredential(context, request)
      val credential = result.credential

      if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        val user = authResult.user
        val email = user?.email ?: googleIdTokenCredential.id
        _authState.value = AuthUiState.LoggedIn(email, user?.uid ?: "", user?.displayName ?: googleIdTokenCredential.displayName ?: "")
        Result.success(email)
      } else {
        Result.failure(Exception("Unrecognized credential type: ${credential.type}"))
      }
    } catch (e: Exception) {
      val rawMsg = e.message ?: e.localizedMessage ?: "Sign-in error"
      val isNoCredentialOrReauth = rawMsg.contains("16") ||
                                   rawMsg.contains("reauth", ignoreCase = true) ||
                                   rawMsg.contains("No credential", ignoreCase = true) ||
                                   rawMsg.contains("Canceled", ignoreCase = true) ||
                                   rawMsg.contains("DEVELOPER_ERROR", ignoreCase = true)

      if (isNoCredentialOrReauth) {
        Result.failure(
          Exception(
            "Google Sign-In canceled or no Google Account selected. You can also log in instantly with Email!",
            e
          )
        )
      } else {
        Result.failure(e)
      }
    }
  }

  fun setLocalDriverSession(email: String = "driver@ordercom.com", name: String = "Delivery Driver") {
    _authState.value = AuthUiState.LoggedIn(email, "local_driver_session", name)
  }

  suspend fun sendPasswordReset(email: String): Result<Unit> {
    val firebaseAuth = getFirebaseAuth() ?: return Result.failure(Exception("Cloud Auth service initializing. Please retry in a moment."))
    return try {
      firebaseAuth.sendPasswordResetEmail(email.trim()).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun logout() {
    getFirebaseAuth()?.signOut()
    _authState.value = AuthUiState.LoggedOut
  }
}
