package com.ti4all.agendaapp

import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ti4all.agendaapp.data.Agenda
import com.ti4all.agendaapp.data.AgendaViewModel
import com.ti4all.agendaapp.ui.theme.AgendaAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AgendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgendaAppTheme {
                AgendaScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AgendaList(agenda: Agenda, onClick: (Agenda) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp).fillMaxWidth()
            .clickable { onClick(agenda) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Nome: ${agenda.nome}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Telefone: ${agenda.telefone}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(viewModel: AgendaViewModel) {
    val agendaList by viewModel.agendaList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedAgenda by remember { mutableStateOf<Agenda?>(null) }


    LaunchedEffect(Unit) {
        viewModel.listarTodos()  }

    Scaffold(topBar = {TopAppBar(
                title = { Text("Agenda Telefônica") })
            },
        floatingActionButton = {FloatingActionButton(
                                onClick = { showDialog = true
                                selectedAgenda = null}
            ) {Icon(Icons.Filled.Add, contentDescription = "Adicionar contato")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            agendaList.forEach { agenda ->
                AgendaList(agenda = agenda) { selectedAgenda = agenda; showDialog = true } // Passa o contato selecionado
            }
        }
        /*
        if (showDialog) {
            AgendaFormDialog(
                onDismissRequest = { showDialog = false }
            ) { nome, telefone ->
                viewModel.inserir(Agenda(nome = nome, telefone = telefone))
                showDialog = false
            }
        }

         */
        if (showDialog && selectedAgenda != null) {
            AgendaFormDialog(
                agenda = selectedAgenda!!, // Passa o contato selecionado
                isEditMode = true, // Indica que estamos editando
                onDismissRequest = { showDialog = false },
                onAddClick = { newAgenda ->
                    viewModel.inserir(newAgenda)
                    showDialog = false
                },
                onEditClick = { updatedAgenda ->
                    viewModel.atualizar(updatedAgenda) // Atualiza o contato
                    showDialog = false
                },
                onDeleteClick = { id ->
                    viewModel.deletar(id) // Chama a função de deletar
                    showDialog = false
                }
            )
        } else if (showDialog) {
            AgendaFormDialog(
                agenda = Agenda(nome = "", telefone = ""), // Passa um novo objeto vazio para adicionar
                isEditMode = false, // Indica que estamos incluindo
                onDismissRequest = { showDialog = false },
                onAddClick = { newAgenda ->
                    viewModel.inserir(newAgenda) // Adiciona novo contato
                    showDialog = false
                },
                onEditClick = { /* Não faz nada, pois é um novo contato */ },
                onDeleteClick = { /* Não faz nada, pois não há id para novo contato */ }
            )
        }
    }
}

    @Composable
// Alterando a assinatura da função AgendaFormDialog
// para receber o dado complexo agenda : Agenda
    /*
    fun AgendaFormDialog(
        onDismissRequest: () -> Unit,
        onAddClick: (String, String) -> Unit
    ) {
    */
    fun AgendaFormDialog(
        agenda: Agenda, // Novo parâmetro para receber o contato
        isEditMode: Boolean, // Sinaliza modo de operação da função
        onDismissRequest: () -> Unit,
        onAddClick: (Agenda) -> Unit, // Altera para receber um objeto Agenda
        onEditClick: (Agenda) -> Unit,
        onDeleteClick: (Int) -> Unit // Função para excluir o contato
    ) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ajustar a declaração das variáveis
                    // var nome by remember { mutableStateOf("") }
                    // var telefone by remember { mutableStateOf("") }
                    var nome by remember { mutableStateOf(agenda.nome) } // Preenche com o nome do contato
                    var telefone by remember { mutableStateOf(agenda.telefone) } // Preenche com o telefone do contato

                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefone,
                        onValueChange = { telefone = it },
                        label = { Text("Telefone") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (isEditMode) {
                                    onEditClick(agenda.copy(nome = nome, telefone = telefone))
                                } else {
                                    onAddClick(agenda.copy(nome = nome, telefone = telefone))
                                }
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(0.5f)
                        ) {
                            Text(if (isEditMode) "Editar" else "Adicionar")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isEditMode) {
                            Button(
                                onClick = {
                                    onDeleteClick(agenda.id) // Chama a função de deletar
                                    onDismissRequest()
                                },
                            modifier = Modifier.weight(0.5f) // Para ocupar espaço igual
                            ) {
                                Text(" Excluir ")
                            }
                        }

                    }
                }
            }
        }
    }
