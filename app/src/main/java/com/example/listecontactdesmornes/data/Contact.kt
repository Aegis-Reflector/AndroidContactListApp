package com.example.listecontactdesmornes.data

import android.location.Address
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts" )
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val firstname: String,
    val lastname: String,
    val phonenumber: String,
    val email :String,
    val address: String,
    val age : Int,
    val isFavorite: Boolean
)