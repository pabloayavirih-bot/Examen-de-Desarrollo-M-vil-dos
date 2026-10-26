package org.ucb.appp1.exercises.domain.repository

import org.ucb.appp1.exercises.domain.model.Task
import org.ucb.appp1.exercises.domain.model.TaskSnapshot

fun interface TaskListener {
    fun cancel()
}

interface TaskRepository {
    fun observe(onChange: (TaskSnapshot) -> Unit, onError: (String) -> Unit): TaskListener
    fun add(title: String, onError: (String) -> Unit)
    fun toggle(task: Task, onError: (String) -> Unit)
    fun delete(task: Task, onError: (String) -> Unit)
}
