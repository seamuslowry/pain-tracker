package seamuslowry.paintracker.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import seamuslowry.paintracker.data.PainTrackerDatabase
import seamuslowry.paintracker.data.daos.TrackedDayDao
import seamuslowry.paintracker.data.daos.TrackedItemDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Provides
    @Singleton
    fun providePainTrackerDb(
        @ApplicationContext context: Context,
    ): PainTrackerDatabase = Room
        .databaseBuilder(context, PainTrackerDatabase::class.java, "pain_tracker_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideTrackedDayDao(
        db: PainTrackerDatabase,
    ): TrackedDayDao = db.trackedDayDao()

    @Provides
    @Singleton
    fun provideTrackedItemDao(
        db: PainTrackerDatabase,
    ): TrackedItemDao = db.trackedItemDao()
}
