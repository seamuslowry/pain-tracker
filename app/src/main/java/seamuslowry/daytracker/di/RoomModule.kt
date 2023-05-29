package seamuslowry.daytracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import seamuslowry.daytracker.data.repos.ItemConfigurationRepo
import seamuslowry.daytracker.data.repos.ItemRepo
import seamuslowry.daytracker.data.repos.RoomItemConfigurationRepo
import seamuslowry.daytracker.data.repos.RoomItemRepo

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomModule {
    @Binds
    abstract fun bindItemConfigurationRepo(impl: RoomItemConfigurationRepo): ItemConfigurationRepo

    @Binds
    abstract fun bindItemRepo(impl: RoomItemRepo): ItemRepo
}
