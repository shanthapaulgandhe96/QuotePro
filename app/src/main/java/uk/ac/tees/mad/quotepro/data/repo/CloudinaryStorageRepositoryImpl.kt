package uk.ac.tees.mad.quotepro.data.repo

import android.net.Uri
import uk.ac.tees.mad.quotepro.data.remote.cloudinary.CloudinarySource
import uk.ac.tees.mad.quotepro.domain.repo.ImageStorageRepository
import javax.inject.Inject

class CloudinaryStorageRepositoryImpl @Inject constructor(
    private val cloudinarySource: CloudinarySource
) : ImageStorageRepository {

    override suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return cloudinarySource.uploadLogo(userId, imageUri)
    }

    override suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return cloudinarySource.uploadSignature(userId, imageUri)
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return cloudinarySource.deleteImage(imageUrl)
    }
}