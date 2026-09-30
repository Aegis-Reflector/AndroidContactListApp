package com.example.listecontactdesmornes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)
    @Update
    suspend fun update(contact: Contact)
    @Delete
    suspend fun delete(contact: Contact)
    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<Contact>>
    @Query("SELECT * FROM contacts WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): Flow<List<Contact>>
}