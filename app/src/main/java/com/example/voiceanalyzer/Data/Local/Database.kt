package com.example.voiceanalyzer.Data.Local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverters::class)
abstract class Appdatabase : RoomDatabase(){

    abstract fun historydao(): DAO_database

    companion object {
        @Volatile
        private var INSTANCE : Appdatabase? = null

        fun get_database(context: Context): Appdatabase{

            return INSTANCE ?: synchronized(this){
                val instans = Room.databaseBuilder(
                    context.applicationContext,
                    Appdatabase::class.java,
                    "voice_analyzer_db"
                ).build()
                INSTANCE = instans
                instans
            }
        }
        }


}


