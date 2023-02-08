package com.demir.kotlinmap.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demir.kotlinmap.model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDataBase:RoomDatabase() {
    abstract fun placeDao():PlaceDao
}