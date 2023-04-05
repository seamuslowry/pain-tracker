package seamuslowry.paintracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import seamuslowry.paintracker.data.repos.ItemConfigurationRepo
import seamuslowry.paintracker.data.repos.ItemRepo
import seamuslowry.paintracker.data.repos.ReportRepo
import seamuslowry.paintracker.data.repos.RoomItemConfigurationRepo
import seamuslowry.paintracker.data.repos.RoomItemRepo
import seamuslowry.paintracker.data.repos.RoomReportRepo

@Module
@InstallIn(ViewModelComponent::class)
abstract class RoomModule {
    @Binds
    abstract fun bindItemConfigurationRepo(impl: RoomItemConfigurationRepo): ItemConfigurationRepo

    @Binds
    abstract fun bindItemRepo(impl: RoomItemRepo): ItemRepo

    @Binds
    abstract fun bindReportRepo(impl: RoomReportRepo): ReportRepo
}
