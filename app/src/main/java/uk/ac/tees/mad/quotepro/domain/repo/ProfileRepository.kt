package uk.ac.tees.mad.quotepro.domain.repo

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings

interface ProfileRepository {
    fun getUserProfile(): Flow<Result<ProfileSettings>>
    suspend fun updateUserProfile(profile: ProfileSettings): Result<Unit>
    suspend fun uploadProfileImage(imageUri: String): Result<String>
}