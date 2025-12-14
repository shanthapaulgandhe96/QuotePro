package uk.ac.tees.mad.quotepro.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.data.remote.cloudinary.CloudinarySource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudinaryModule {

    @Provides
    @Singleton
    fun provideCloudinarySource(
        @ApplicationContext context: Context
    ): CloudinarySource {
        return CloudinarySource(context)
    }
}