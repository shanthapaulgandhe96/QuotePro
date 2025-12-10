package uk.ac.tees.mad.quotepro.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SyncOfflineQuotesUseCase

@HiltWorker
class QuoteSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncOfflineQuotesUseCase: SyncOfflineQuotesUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val result = syncOfflineQuotesUseCase()

            if (result.isSuccess) {
                val syncedCount = result.getOrNull() ?: 0
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "quote_sync_work"
    }
}