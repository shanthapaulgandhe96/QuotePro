package uk.ac.tees.mad.quotepro.data.repo

import android.net.Uri
import uk.ac.tees.mad.quotepro.data.remote.firebase.FirebaseStorageSource
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseStorageRepository
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val storageSource: FirebaseStorageSource
) : FirebaseStorageRepository {

    override suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return storageSource.uploadLogo(userId, imageUri)
    }

    override suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return storageSource.uploadSignature(userId, imageUri)
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return storageSource.deleteImage(imageUrl)
    }
}