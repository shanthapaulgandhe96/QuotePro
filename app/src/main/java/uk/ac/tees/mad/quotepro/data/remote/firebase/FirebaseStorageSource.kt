package uk.ac.tees.mad.quotepro.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageSource @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {
    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "logos/${userId}_${System.currentTimeMillis()}.jpg"
            val storageRef = firebaseStorage.reference.child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "signatures/${userId}_${System.currentTimeMillis()}.jpg"
            val storageRef = firebaseStorage.reference.child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}