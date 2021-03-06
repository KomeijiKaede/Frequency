package net.teamfruit.frequency.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import java.util.*

@Database(entities = [DBEntity::class], version = 2)
abstract class Base: RoomDatabase() {
    abstract fun dbdao(): DataAccessObjects
    companion object {
        fun create(title: String, videoID: String, thumbnail: String) = DBEntity(Random().nextInt(), title, videoID, thumbnail)
        private var INSTANCE: Base? = null
        fun getDataBase(context: Context): Base {
            if(INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context, Base::class.java, "info.db").allowMainThreadQueries().build()
            return INSTANCE as Base
        }
    }
}