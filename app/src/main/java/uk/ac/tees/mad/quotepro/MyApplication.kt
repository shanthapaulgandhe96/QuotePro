package uk.ac.tees.mad.quotepro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import uk.ac.tees.mad.quotepro.utils.NotificationHelper
import uk.ac.tees.mad.quotepro.workers.WorkManagerInitializer
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()

        // Initialize WorkManager for background sync
        WorkManagerInitializer.initialize(this)

        // Create Channel
        notificationHelper.createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}