package com.example.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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
  private val auth: FirebaseAuth? = try {
    FirebaseAuth.getInstance()
  } catch (e: Exception) {
    null
  }

  private val _authState = MutableStateFlow<AuthUiState>(
    auth?.currentUser?.let { user ->
      AuthUiState.LoggedIn(user.email ?: "User", user.uid, user.displayName ?: "")
    } ?: AuthUiState.LoggedOut
  )
  val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

  val currentUser: FirebaseUser?
    get() = auth?.currentUser

  suspend fun login(email: String, password: String): Result<String> {
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth is not available"))
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
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth is not available"))
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
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth is not available"))
    return try {
      val credentialManager = CredentialManager.create(context)
      val clientId = "508672273546-mb8s1o2e01q4hjbf4icuo0a39h34q8m6.apps.googleusercontent.com"

      // Use GetGoogleIdOption with filterByAuthorizedAccounts = false to display all Google accounts on device
      // and avoid the "[16] Account reauth failed" error caused by filtering for non-existent authorized sessions!
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
            "No Google Account on this device or SHA-1 pending in Firebase. Please use 1-Tap Driver Login or Email below!",
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
    val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth is not available"))
    return try {
      firebaseAuth.sendPasswordResetEmail(email.trim()).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun logout() {
    auth?.signOut()
    _authState.value = AuthUiState.LoggedOut
  }
}
