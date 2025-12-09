package uk.ac.tees.mad.quotepro.domain.repo

import android.net.Uri

interface FirebaseStorageRepository {
    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String>
    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}