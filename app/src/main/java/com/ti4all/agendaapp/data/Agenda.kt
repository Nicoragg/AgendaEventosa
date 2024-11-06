package com.ti4all.agendaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agenda")
data class Agenda(
    @PrimaryKey(autoGenerate = true) val id: Int = 0
    ,val nome: String
    ,val telefone : String
)
