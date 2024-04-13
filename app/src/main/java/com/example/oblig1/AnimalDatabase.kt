package com.example.oblig1

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Animal::class], version = 1)
abstract class AnimalDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao

    companion object {
        @Volatile
        private var INSTANCE: AnimalDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AnimalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnimalDatabase::class.java,
                    "animal_database"
                )
                    .addCallback(AnimalDatabaseCallback(context, scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AnimalDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                if (!isDatabaseInitialized(context)) {
                    INSTANCE?.let { database ->
                        scope.launch(Dispatchers.IO) {
                            populateDatabase(context, database.animalDao())
                            setDatabaseInitialized(context)
                        }
                    }
                }
            }

            private fun isDatabaseInitialized(context: Context): Boolean {
                val sharedPrefs = context.getSharedPreferences("database_prefs", Context.MODE_PRIVATE)
                return sharedPrefs.getBoolean("initialized", false)
            }

            private fun setDatabaseInitialized(context: Context) {
                val sharedPrefs = context.getSharedPreferences("database_prefs", Context.MODE_PRIVATE)
                with(sharedPrefs.edit()) {
                    putBoolean("initialized", true)
                    apply()
                }
            }

            private suspend fun populateDatabase(context: Context, animalDao: AnimalDao) {
                // Populate the database with initial data
                val animals = listOf(
                    Animal(name = "Cat", photoUri = getResourceUri(context, R.drawable.cat)),
                    Animal(name = "Dog", photoUri = getResourceUri(context, R.drawable.dog)),
                    Animal(name = "Opossum", photoUri = getResourceUri(context, R.drawable.opossum))
                )
                for (animal in animals) {
                    animalDao.insertAnimal(animal)
                }
            }

            private fun getResourceUri(context: Context, resId: Int): String {
                return Uri.parse("android.resource://${context.packageName}/$resId").toString()
            }
        }
    }
}