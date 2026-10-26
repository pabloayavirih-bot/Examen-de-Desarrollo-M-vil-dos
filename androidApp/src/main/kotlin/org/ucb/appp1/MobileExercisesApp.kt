package org.ucb.appp1

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.ucb.appp1.notifications.AppNotifications
import org.ucb.appp1.tasks.TaskItem
import org.ucb.appp1.tasks.TaskMonitorViewModel
import org.ucb.appp1.tasks.TasksState
import org.ucb.appp1.tasks.TasksViewModel

private enum class Screen { HOME, CRUD, LOCAL_NOTIFICATION, MONITOR }

@Composable
fun MobileExercisesApp(openTasksInitially: Boolean = false) {
    var screen by remember(openTasksInitially) {
        mutableStateOf(if (openTasksInitially) Screen.CRUD else Screen.HOME)
    }
    when (screen) {
        Screen.HOME -> HomeScreen(onOpen = { screen = it })
        Screen.CRUD -> CrudScreen(onBack = { screen = Screen.HOME })
        Screen.LOCAL_NOTIFICATION -> LocalNotificationScreen(onBack = { screen = Screen.HOME })
        Screen.MONITOR -> MonitorScreen(onBack = { screen = Screen.HOME })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Page(title: String, onBack: (() -> Unit)? = null, content: @Composable () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = { if (onBack != null) TextButton(onClick = onBack) { Text("Volver") } }
        )
    }) { padding -> Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), content = { content() }) }
}

@Composable
private fun HomeScreen(onOpen: (Screen) -> Unit) = Page("Ejercicios de Móviles") {
    Text("Selecciona un ejercicio independiente.", style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(16.dp))
    ExerciseButton("E25-3 · CRUD de tareas en Firestore") { onOpen(Screen.CRUD) }
    ExerciseButton("E25-5 · Notificación local") { onOpen(Screen.LOCAL_NOTIFICATION) }
    ExerciseButton("E50-5 · Listener y avisos de tareas") { onOpen(Screen.MONITOR) }
}

@Composable
private fun ExerciseButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) { Text(text) }
}

@Composable
private fun CrudScreen(onBack: () -> Unit, viewModel: TasksViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    Page("E25-3 · CRUD Firestore", onBack) {
        TaskEditor(state.draft, viewModel::updateDraft, viewModel::addTask)
        TaskList(state, viewModel::toggle, viewModel::delete)
    }
}

@Composable
private fun TaskEditor(value: String, onChange: (String) -> Unit, onAdd: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text("Título de la tarea") },
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Título de nueva tarea" },
        singleLine = true
    )
    Button(onClick = onAdd, modifier = Modifier.padding(top = 8.dp)) { Text("Agregar tarea") }
}

@Composable
private fun TaskList(
    state: TasksState,
    onToggle: (TaskItem) -> Unit,
    onDelete: (TaskItem) -> Unit,
    showActions: Boolean = true
) {
    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
    if (state.loading) Text("Cargando tareas…", modifier = Modifier.padding(top = 16.dp))
    LazyColumn(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.tasks, key = { it.id }) { task ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.completed, onCheckedChange = if (showActions) ({ onToggle(task) }) else null)
                    Text(
                        task.title,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (task.completed) TextDecoration.LineThrough else null
                    )
                    if (showActions) TextButton(onClick = { onDelete(task) }) { Text("Eliminar") }
                }
            }
        }
    }
}

@Composable
private fun LocalNotificationScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Page("E25-5 · Notificación local", onBack) {
        Text("Crea un canal de notificaciones y muestra un mensaje local.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            AppNotifications.createChannel(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            else AppNotifications.show(context, 25_5, "Notificación local", "Hola desde la app")
        }) { Text("Permitir notificaciones") }
        Button(onClick = {
            AppNotifications.createChannel(context)
            AppNotifications.show(context, 25_5, "Notificación local", "Hola desde la app")
        }, modifier = Modifier.padding(top = 8.dp)) { Text("Mostrar “Hola desde la app”") }
    }
}

@Composable
private fun MonitorScreen(onBack: () -> Unit, viewModel: TaskMonitorViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var muted by remember { mutableStateOf(viewModel.isMuted()) }
    Page("E50-5 · Listener y notificaciones", onBack) {
        Text("Este listener detecta tareas añadidas desde cualquier cliente de Firestore.")
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
            Text("Silenciar notificaciones", modifier = Modifier.weight(1f))
            Switch(checked = muted, onCheckedChange = { muted = it; viewModel.setMuted(it) })
        }
        Text(if (muted) "Las notificaciones están silenciadas." else "Las nuevas tareas mostrarán una notificación.")
        TaskList(state, onToggle = {}, onDelete = {}, showActions = false)
    }
}
