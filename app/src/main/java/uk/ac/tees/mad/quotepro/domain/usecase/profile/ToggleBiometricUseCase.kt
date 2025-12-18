package uk.ac.tees.mad.quotepro.domain.usecase.profile

import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Inject

class ToggleBiometricUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.updateBiometricEnabled(enabled)
}