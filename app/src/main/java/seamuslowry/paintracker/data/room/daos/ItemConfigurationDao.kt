package seamuslowry.paintracker.data.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.models.ItemConfiguration

@Dao
interface ItemConfigurationDao {
    @Query("select * from item_configuration")
    fun getAll(): Flow<List<ItemConfiguration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(itemConfiguration: ItemConfiguration): Long

    @Delete
    suspend fun delete(itemConfiguration: ItemConfiguration)
}
