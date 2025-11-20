package uk.ac.tees.mad.quotepro.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.quotepro.data.repo.FirebaseAuthRepoImpl
import uk.ac.tees.mad.quotepro.domain.repo.FirebaseAuthRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepo(firebaseAuthRepoImpl: FirebaseAuthRepoImpl): FirebaseAuthRepo

}