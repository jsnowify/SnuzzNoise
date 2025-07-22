package com.snowi.snuzz.di

import com.google.firebase.firestore.FirebaseFirestore
import com.snowi.snuzz.data.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        firestore: FirebaseFirestore
    ): UserPreferencesRepository {
        return UserPreferencesRepository(firestore)
    }
}