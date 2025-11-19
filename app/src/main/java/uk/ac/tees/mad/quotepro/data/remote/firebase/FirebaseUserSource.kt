package uk.ac.tees.mad.quotepro.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.quotepro.domain.model.UserModel
import uk.ac.tees.mad.quotepro.utils.Constants
import javax.inject.Inject

class FirebaseUserSource @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {
    suspend fun saveUser(userModel: UserModel): Result<Unit> {
        return try {
            firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(userModel.uid)
                .set(userModel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<UserModel> {
        try {
            val snapshot = firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            val user = snapshot.toObject(UserModel::class.java)
            return if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}