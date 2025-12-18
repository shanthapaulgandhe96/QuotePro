package uk.ac.tees.mad.quotepro.domain.usecase.profile

import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Inject

class UpdateThemeModeUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(isDarkMode: Boolean) = repository.updateThemeMode(isDarkMode)
}