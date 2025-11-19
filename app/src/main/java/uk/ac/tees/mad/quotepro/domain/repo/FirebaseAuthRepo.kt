package uk.ac.tees.mad.quotepro.domain.repo

import com.google.firebase.auth.FirebaseUser
import uk.ac.tees.mad.quotepro.domain.model.UserModel

interface FirebaseAuthRepo {
    suspend fun signUp(name: String, email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    suspend fun getUserProfile(uid: String): Result<UserModel>
}