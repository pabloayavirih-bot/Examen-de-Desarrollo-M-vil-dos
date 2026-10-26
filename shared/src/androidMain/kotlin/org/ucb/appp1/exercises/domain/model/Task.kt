package org.ucb.appp1.exercises.domain.model

data class Task(
    val id: String,
    val title: String,
    val completed: Boolean,
    val timestampMillis: Long?
)

data class TaskSnapshot(
    val tasks: List<Task>,
    val addedTasks: List<Task>
)
