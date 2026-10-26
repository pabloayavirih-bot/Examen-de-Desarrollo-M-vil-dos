package org.ucb.appp1.tasks

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class TaskItem(
    val id: String,
    val title: String,
    val completed: Boolean,
    val timestamp: Timestamp?
)

class TaskRepository {
    private val tasks = FirebaseFirestore.getInstance().collection("tasks")

    fun observe(onChange: (List<TaskItem>, List<TaskItem>) -> Unit, onError: (String) -> Unit): ListenerRegistration =
        tasks.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error.localizedMessage ?: "No se pudo consultar Firestore.")
                return@addSnapshotListener
            }
            val current = snapshot?.documents.orEmpty().map { document ->
                TaskItem(
                    id = document.id,
                    title = document.getString("titulo").orEmpty(),
                    completed = document.getBoolean("completada") ?: false,
                    timestamp = document.getTimestamp("timestamp")
                )
            }
            val added = snapshot?.documentChanges
                ?.filter { it.type == DocumentChange.Type.ADDED }
                ?.map { change ->
                    TaskItem(
                        id = change.document.id,
                        title = change.document.getString("titulo").orEmpty(),
                        completed = change.document.getBoolean("completada") ?: false,
                        timestamp = change.document.getTimestamp("timestamp")
                    )
                }
                .orEmpty()
            onChange(current, added)
        }

    fun add(title: String, onError: (String) -> Unit) {
        tasks.add(mapOf("titulo" to title, "completada" to false, "timestamp" to Timestamp.now()))
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo agregar la tarea.") }
    }

    fun toggle(task: TaskItem, onError: (String) -> Unit) {
        tasks.document(task.id).update("completada", !task.completed)
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo actualizar la tarea.") }
    }

    fun delete(task: TaskItem, onError: (String) -> Unit) {
        tasks.document(task.id).delete()
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo eliminar la tarea.") }
    }
}
