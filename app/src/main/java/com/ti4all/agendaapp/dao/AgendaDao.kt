package com.ti4all.agendaapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ti4all.agendaapp.data.Agenda

@Dao
interface AgendaDao {

    @Insert
    suspend fun inserir(contato: Agenda)

    @Update
    suspend fun atualizar(contato: Agenda) // Método para atualizar contato

    @Query("SELECT * FROM agenda")
    suspend fun listarTodos() : List<Agenda>

    @Query("DELETE FROM agenda WHERE id = :id")
    suspend fun deletear(id: Int)
}