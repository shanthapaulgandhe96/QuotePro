package uk.ac.tees.mad.quotepro.data.repo

import com.google.firebase.auth.FirebaseUser
import uk.ac.tees.mad.quotepro.data.remote.firebase.FirebaseAuthSource
import uk.ac.tees.mad.quotepro.data.remote.firebase.FirebaseUserSource
import uk.ac.tees.mad.quotepro.domain.model.UserModel
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import javax.inject.Inject


class FirebaseAuthRepoImpl @Inject constructor(
    private val authSource: FirebaseAuthSource,
    private val userSource: FirebaseUserSource,
) : FirebaseAuthRepo {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Result<Unit> {

        return try {
            val authResult = authSource.signUp(email = email, password = password)
            val firebaseUser = authResult.getOrNull()
                ?: return Result.failure(Exception("User already account created"))

            val userModel = UserModel(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                companyName = "",
                currency = ""
            )
            userSource.saveUser(userModel)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun signIn(
        email: String,
        password: String,
    ): Result<Unit> {
        return authSource.signIn(email, password)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return authSource.resetPassword(email)
    }

    override suspend fun signOut(): Result<Unit> {
        return authSource.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authSource.getCurrentUser()
    }

    override suspend fun getUserProfile(uid: String): Result<UserModel> {
        return userSource.getUser(uid)
    }

}