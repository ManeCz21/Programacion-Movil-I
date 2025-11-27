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

@Database(entities = [Note::class, Task::class, Attachment::class], version = 5, exportSchema = false)
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
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminder TEXT NOT NULL DEFAULT 'Ninguno'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the new table with the correct schema
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
                    INSERT INTO tasks_new (id, title, description, date, time, isCompleted, attachments)
                    SELECT id, title, description, date, time, isCompleted, attachments FROM tasks
                """)

                // Drop the old table
                db.execSQL("DROP TABLE tasks")

                // Rename the new table to the original name
                db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }
    }
}
