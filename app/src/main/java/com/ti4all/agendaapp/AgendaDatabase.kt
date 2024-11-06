package com.ti4all.agendaapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ti4all.agendaapp.dao.AgendaDao
import com.ti4all.agendaapp.data.Agenda

@Database(entities = [Agenda::class], version = 1)
abstract class AgendaDatabase : RoomDatabase() {
    abstract fun agendaDao() : AgendaDao
}
