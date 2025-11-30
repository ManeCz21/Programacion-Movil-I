package com.example.proyectofinalweb.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectofinalweb.model.Attachment
import com.example.proyectofinalweb.model.Note
import com.example.proyectofinalweb.model.Task

@Database(entities = [Note::class, Task::class, Attachment::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, NoteDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the new table with the correct schema (without the old 'reminder' column)
                db.execSQL("""
                    CREATE TABLE tasks_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        attachments TEXT NOT NULL,
                        reminders TEXT NOT NULL DEFAULT '[]'
                    )
                """)

                // Copy the data from the old table to the new table
                db.execSQL("""
                    INSERT INTO tasks_new (id, title, description, date, time, isCompleted, attachments, reminders)
                    SELECT id, title, description, date, time, isCompleted, attachments, reminders FROM tasks
                """)

                // Drop the old table
                db.execSQL("DROP TABLE tasks")

                // Rename the new table to the original name
                db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }
    }
}
