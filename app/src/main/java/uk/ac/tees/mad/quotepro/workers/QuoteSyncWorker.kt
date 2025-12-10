package uk.ac.tees.mad.quotepro.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uk.ac.tees.mad.quotepro.domain.usecase.quote.SyncOfflineQuotesUseCase

@HiltWorker
class QuoteSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncOfflineQuotesUseCase: SyncOfflineQuotesUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            syncOfflineQuotesUseCase()
                .onSuccess {
                    android.util.Log.d("QuoteSyncWorker", "Successfully synced offline quotes")
                }
                .onFailure { exception ->
                    android.util.Log.e("QuoteSyncWorker", "Failed to sync quotes: ${exception.message}")
                }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("QuoteSyncWorker", "Worker failed: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "quote_sync_work"
    }
}