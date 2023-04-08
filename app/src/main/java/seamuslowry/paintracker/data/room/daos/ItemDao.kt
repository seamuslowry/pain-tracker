package seamuslowry.paintracker.data.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import seamuslowry.paintracker.models.Item
import java.time.LocalDate

@Dao
interface ItemDao {
    @Query("select * from item")
    fun getAll(): Flow<List<Item>>

    @Query("select * from item where date = :date")
    fun get(date: LocalDate): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg item: Item)
}
