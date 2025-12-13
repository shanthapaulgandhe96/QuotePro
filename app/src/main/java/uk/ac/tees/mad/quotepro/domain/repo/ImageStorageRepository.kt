package uk.ac.tees.mad.quotepro.domain.repo

import android.net.Uri

/**
 * Repository interface for image storage operations
 * Implementation can be Firebase Storage, Cloudinary, or any other service
 */
interface ImageStorageRepository {
    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String>
    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}