package uk.ac.tees.mad.quotepro.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.ProfileSettings
import uk.ac.tees.mad.quotepro.domain.repo.ProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Result<ProfileSettings>> = repository.getUserProfile()
}