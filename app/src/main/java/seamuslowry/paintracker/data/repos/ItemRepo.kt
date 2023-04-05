package seamuslowry.paintracker.data.repos

import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.data.room.daos.ItemDao
import seamuslowry.paintracker.models.Item
import javax.inject.Inject

interface ItemRepo {
    fun getAll(): Flow<List<Item>>
    suspend fun save(vararg item: Item)
}

class RoomItemRepo @Inject constructor(private val itemDao: ItemDao) : ItemRepo {
    override fun getAll(): Flow<List<Item>> = itemDao.getAll()
    override suspend fun save(vararg item: Item) = itemDao.upsert(*item)
}
