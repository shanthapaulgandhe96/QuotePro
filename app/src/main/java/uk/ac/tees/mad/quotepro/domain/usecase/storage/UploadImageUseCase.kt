package uk.ac.tees.mad.quotepro.domain.usecase.storage

import android.net.Uri
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseStorageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: FirebaseStorageRepository
) {
    suspend fun uploadLogo(userId: String, imageUri: Uri): Result<String> {
        return repository.uploadLogo(userId, imageUri)
    }

    suspend fun uploadSignature(userId: String, imageUri: Uri): Result<String> {
        return repository.uploadSignature(userId, imageUri)
    }
}