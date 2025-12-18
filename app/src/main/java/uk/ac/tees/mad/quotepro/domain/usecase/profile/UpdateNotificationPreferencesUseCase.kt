package uk.ac.tees.mad.quotepro.domain.usecase.profile

import uk.ac.tees.mad.quotepro.domain.model.NotificationPreference
import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import javax.inject.Inject

class UpdateNotificationPreferencesUseCase @Inject constructor(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(prefs: NotificationPreference) = repository.updateNotificationPreferences(prefs)
}