package uk.ac.tees.mad.quotepro.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uk.ac.tees.mad.quotepro.domain.usecase.reminder.SyncRemindersUseCase

@HiltWorker
class ReminderSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRemindersUseCase: SyncRemindersUseCase,
    private val auth: FirebaseAuth
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                syncRemindersUseCase(userId).getOrThrow()
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "reminder_sync_work"
    }
}