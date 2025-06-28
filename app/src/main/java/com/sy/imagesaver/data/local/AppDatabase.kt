package com.sy.imagesaver.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.sy.imagesaver.data.local.dao.MediaDao
import com.sy.imagesaver.data.local.entity.MediaEntity
import com.sy.imagesaver.data.local.converter.MediaTypeConverter

@Database(
    entities = [MediaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MediaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun mediaDao(): MediaDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "image-saver-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

