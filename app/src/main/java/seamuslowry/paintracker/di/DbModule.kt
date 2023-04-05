package seamuslowry.paintracker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import seamuslowry.paintracker.data.room.PainTrackerDatabase
import seamuslowry.paintracker.data.room.daos.ItemConfigurationDao
import seamuslowry.paintracker.data.room.daos.ItemDao
import seamuslowry.paintracker.data.room.daos.ReportDao
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
    fun provideReportDao(
        db: PainTrackerDatabase,
    ): ReportDao = db.reportDao()

    @Provides
    @Singleton
    fun provideItemDao(
        db: PainTrackerDatabase,
    ): ItemDao = db.itemDao()

    @Provides
    @Singleton
    fun provideItemConfigurationDao(
        db: PainTrackerDatabase,
    ): ItemConfigurationDao = db.itemConfigurationDao()
}
