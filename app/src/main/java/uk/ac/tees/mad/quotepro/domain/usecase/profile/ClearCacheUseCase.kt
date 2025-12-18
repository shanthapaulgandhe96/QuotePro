package uk.ac.tees.mad.quotepro.domain.usecase.profile

import uk.ac.tees.mad.quotepro.domain.repo.UserPreferencesRepository
import uk.ac.tees.mad.quotepro.utils.CacheManager
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(
    private val cacheManager: CacheManager,
    private val prefsRepo: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        cacheManager.clearAllCache()
        // Optionally preserve some preferences or clear them too depending on requirement
        // prefsRepo.clearPreferences()
    }
}