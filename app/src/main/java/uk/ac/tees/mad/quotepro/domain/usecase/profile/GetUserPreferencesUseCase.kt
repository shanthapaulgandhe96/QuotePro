package uk.ac.tees.mad.quotepro.domain.usecase.profile

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.quotepro.domain.model.UserPreferences
import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<UserPreferences> = repository.userPreferences
}