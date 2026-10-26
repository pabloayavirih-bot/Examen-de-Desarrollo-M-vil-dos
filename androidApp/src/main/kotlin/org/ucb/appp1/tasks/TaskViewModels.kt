package org.ucb.appp1.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.ucb.appp1.notifications.AppNotifications

data class TasksState(
    val draft: String = "",
    val tasks: List<TaskItem> = emptyList(),
    val error: String? = null,
    val loading: Boolean = true
)

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository()
    private var listener: ListenerRegistration? = null
    private val _state = MutableStateFlow(TasksState())
    val state = _state.asStateFlow()

    init { start() }

    private fun start() {
        listener = repository.observe(
            onChange = { tasks, _ -> _state.update { it.copy(tasks = tasks, loading = false, error = null) } },
            onError = { message -> _state.update { it.copy(loading = false, error = message) } }
        )
    }

    fun updateDraft(value: String) = _state.update { it.copy(draft = value) }

    fun addTask() {
        val title = state.value.draft.trim()
        if (title.isBlank()) {
            _state.update { it.copy(error = "Escribe un título para la tarea.") }
            return
        }
        _state.update { it.copy(draft = "", error = null) }
        repository.add(title, ::showError)
    }

    fun toggle(task: TaskItem) = repository.toggle(task, ::showError)
    fun delete(task: TaskItem) = repository.delete(task, ::showError)
    private fun showError(message: String) = _state.update { it.copy(error = message) }
    override fun onCleared() { listener?.remove() }
}

class TaskMonitorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository()
    private val preferences = application.getSharedPreferences("task_preferences", Application.MODE_PRIVATE)
    private var listener: ListenerRegistration? = null
    private var initialSnapshotReceived = false
    private val _state = MutableStateFlow(TasksState())
    val state = _state.asStateFlow()
    private val app = application

    init {
        AppNotifications.createChannel(application)
        listener = repository.observe(
            onChange = { tasks, added ->
                if (initialSnapshotReceived && !isMuted()) {
                    added.forEach { task ->
                        AppNotifications.show(app, task.id.hashCode(), "Nueva tarea", task.title)
                    }
                }
                initialSnapshotReceived = true
                _state.update { it.copy(tasks = tasks, loading = false, error = null) }
            },
            onError = { message -> _state.update { it.copy(loading = false, error = message) } }
        )
    }

    fun isMuted(): Boolean = preferences.getBoolean("notifications_muted", false)
    fun setMuted(muted: Boolean) = preferences.edit().putBoolean("notifications_muted", muted).apply()
    override fun onCleared() { listener?.remove() }
}
