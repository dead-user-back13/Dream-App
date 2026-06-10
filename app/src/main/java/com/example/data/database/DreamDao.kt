package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY timestamp DESC")
    fun getAllDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams WHERE id = :id LIMIT 1")
    fun getDreamById(id: Long): Flow<DreamEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: DreamEntity): Long

    @Query("UPDATE dreams SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Long, isBookmarked: Boolean)

    @Delete
    suspend fun deleteDream(dream: DreamEntity)

    @Query("DELETE FROM dreams WHERE id = :id")
    suspend fun deleteDreamById(id: Long)

    @Query("DELETE FROM dreams")
    suspend fun clearHistory()
}
