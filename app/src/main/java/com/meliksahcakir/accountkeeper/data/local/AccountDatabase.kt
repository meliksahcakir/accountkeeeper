package com.meliksahcakir.accountkeeper.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.meliksahcakir.accountkeeper.data.Account


/**
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Account::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {
    abstract val accountDao: AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccountDatabase? = null

        fun getInstance(context: Context): AccountDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AccountDatabase::class.java,
                    "accountDatabase"
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }
}