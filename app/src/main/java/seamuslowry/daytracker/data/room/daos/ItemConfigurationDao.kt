package seamuslowry.daytracker.data.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import seamuslowry.daytracker.models.ItemConfiguration

@Dao
interface ItemConfigurationDao {
    @Query("select count(*) from item_configuration")
    fun getTotal(): Long

    @Query("select * from item_configuration")
    fun getAll(): Flow<List<ItemConfiguration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(itemConfiguration: ItemConfiguration): Long

    @Delete
    suspend fun delete(itemConfiguration: ItemConfiguration)
}
