package seamuslowry.paintracker.data.repos

import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.data.room.daos.ItemConfigurationDao
import seamuslowry.paintracker.models.ItemConfiguration
import javax.inject.Inject

interface ItemConfigurationRepo {
    fun getAll(): Flow<List<ItemConfiguration>>
    suspend fun save(itemConfiguration: ItemConfiguration): Long
    suspend fun delete(itemConfiguration: ItemConfiguration)
}

class RoomItemConfigurationRepo @Inject constructor(private val itemConfigurationDao: ItemConfigurationDao) : ItemConfigurationRepo {
    override fun getAll(): Flow<List<ItemConfiguration>> = itemConfigurationDao.getAll()
    override suspend fun save(itemConfiguration: ItemConfiguration): Long = itemConfigurationDao.upsert(itemConfiguration)
    override suspend fun delete(itemConfiguration: ItemConfiguration) = itemConfigurationDao.delete(itemConfiguration)
}
