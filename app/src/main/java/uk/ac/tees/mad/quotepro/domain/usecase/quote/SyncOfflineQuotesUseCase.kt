package uk.ac.tees.mad.quotepro.domain.usecase.quote

import uk.ac.tees.mad.quotepro.domain.repo.SyncRepository
import javax.inject.Inject

class SyncOfflineQuotesUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            syncRepository.syncOfflineQuotes()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}