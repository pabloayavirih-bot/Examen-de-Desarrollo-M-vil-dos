package org.ucb.appp1.exercises.data.repository

import org.ucb.appp1.exercises.data.datasource.FirestoreTaskDataSource
import org.ucb.appp1.exercises.domain.model.Task
import org.ucb.appp1.exercises.domain.model.TaskSnapshot
import org.ucb.appp1.exercises.domain.repository.TaskListener
import org.ucb.appp1.exercises.domain.repository.TaskRepository

class FirestoreTaskRepository(
    private val dataSource: FirestoreTaskDataSource = FirestoreTaskDataSource()
) : TaskRepository {
    override fun observe(onChange: (TaskSnapshot) -> Unit, onError: (String) -> Unit): TaskListener {
        val listener = dataSource.observe(onChange, onError)
        return TaskListener { listener.remove() }
    }

    override fun add(title: String, onError: (String) -> Unit) = dataSource.add(title, onError)
    override fun toggle(task: Task, onError: (String) -> Unit) = dataSource.toggle(task, onError)
    override fun delete(task: Task, onError: (String) -> Unit) = dataSource.delete(task, onError)
}
