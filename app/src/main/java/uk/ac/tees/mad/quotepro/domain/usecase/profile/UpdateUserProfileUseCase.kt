package uk.ac.tees.mad.quotepro.domain.usecase.profile

import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings
import uk.ac.tees.mad.quotepro.domain.repo.ProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: ProfileSettings): Result<Unit> =
        repository.updateUserProfile(profile)
}