package com.dark.neuroverse.neurov.db.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dark.neuroverse.neurov.db.data.ChatMessage

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 20): List<ChatMessage>

    @Query("DELETE FROM chat_history")
    suspend fun clearAll()
}
