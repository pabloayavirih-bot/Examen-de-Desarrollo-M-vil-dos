package org.ucb.appp1.exercises.domain.usecase

import org.ucb.appp1.exercises.domain.model.Task
import org.ucb.appp1.exercises.domain.repository.TaskListener
import org.ucb.appp1.exercises.domain.repository.TaskRepository

class ObserveTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(onChange: (List<Task>, List<Task>) -> Unit, onError: (String) -> Unit): TaskListener =
        repository.observe({ snapshot -> onChange(snapshot.tasks, snapshot.addedTasks) }, onError)
}

class AddTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(title: String, onError: (String) -> Unit) = repository.add(title, onError)
}

class ToggleTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task, onError: (String) -> Unit) = repository.toggle(task, onError)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task, onError: (String) -> Unit) = repository.delete(task, onError)
}
