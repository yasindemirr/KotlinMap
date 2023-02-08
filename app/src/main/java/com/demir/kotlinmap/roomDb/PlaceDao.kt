package com.demir.kotlinmap.roomDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.demir.kotlinmap.model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {
    @Query("SELECT*FROM Place")
    fun getAll(): Flowable<List<Place>>
    @Insert
    fun insert(place:Place): Completable
    @Delete
    fun deleteDb(place: Place): Completable

}