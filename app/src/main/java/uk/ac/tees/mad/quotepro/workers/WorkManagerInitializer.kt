package uk.ac.tees.mad.quotepro.workers

import android.content.Context
import androidx.work.*
import uk.ac.tees.mad.quotepro.data.workers.QuoteSyncWorker
import java.util.concurrent.TimeUnit
import uk.ac.tees.mad.quotepro.workers.ReminderSyncWorker

object WorkManagerInitializer {

    fun initialize(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<QuoteSyncWorker>(
            15, TimeUnit.MINUTES // Sync every 15 minutes
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            QuoteSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        val reminderSyncRequest = PeriodicWorkRequestBuilder<ReminderSyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderSyncRequest
        )
    }

    fun syncNow(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<QuoteSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "immediate_sync",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }
}