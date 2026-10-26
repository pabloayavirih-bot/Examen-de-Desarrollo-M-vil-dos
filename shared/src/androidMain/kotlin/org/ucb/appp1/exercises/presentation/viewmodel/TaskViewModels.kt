package org.ucb.appp1.exercises.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.ucb.appp1.exercises.data.repository.FirestoreTaskRepository
import org.ucb.appp1.exercises.domain.model.Task
import org.ucb.appp1.exercises.domain.repository.TaskListener
import org.ucb.appp1.exercises.domain.repository.TaskRepository
import org.ucb.appp1.exercises.domain.usecase.AddTaskUseCase
import org.ucb.appp1.exercises.domain.usecase.DeleteTaskUseCase
import org.ucb.appp1.exercises.domain.usecase.ObserveTasksUseCase
import org.ucb.appp1.exercises.domain.usecase.ToggleTaskUseCase
import org.ucb.appp1.exercises.presentation.notification.AppNotifications

data class TasksState(
    val draft: String = "",
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
    val loading: Boolean = true
)

private class TaskUseCases(repository: TaskRepository = FirestoreTaskRepository()) {
    val observe = ObserveTasksUseCase(repository)
    val add = AddTaskUseCase(repository)
    val toggle = ToggleTaskUseCase(repository)
    val delete = DeleteTaskUseCase(repository)
}

class TasksViewModel(application: Application) : AndroidViewModel(application) {
    private val useCases = TaskUseCases()
    private var listener: TaskListener? = null
    private val _state = MutableStateFlow(TasksState())
    val state = _state.asStateFlow()

    init {
        listener = useCases.observe(
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
        useCases.add(title, ::showError)
    }

    fun toggle(task: Task) = useCases.toggle(task, ::showError)
    fun delete(task: Task) = useCases.delete(task, ::showError)
    private fun showError(message: String) = _state.update { it.copy(error = message) }
    override fun onCleared() {
        listener?.cancel()
    }
}

class TaskMonitorViewModel(application: Application) : AndroidViewModel(application) {
    private val useCases = TaskUseCases()
    private val preferences = application.getSharedPreferences("task_preferences", Application.MODE_PRIVATE)
    private var listener: TaskListener? = null
    private var initialSnapshotReceived = false
    private val _state = MutableStateFlow(TasksState())
    val state = _state.asStateFlow()

    init {
        AppNotifications.createChannel(application)
        listener = useCases.observe(
            onChange = { tasks, added ->
                if (initialSnapshotReceived && !isMuted()) {
                    added.forEach { task ->
                        AppNotifications.show(application, task.id.hashCode(), "Nueva tarea", task.title)
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
    override fun onCleared() {
        listener?.cancel()
    }
}
