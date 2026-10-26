package org.ucb.appp1.exercises.data.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import org.ucb.appp1.exercises.data.mapper.toTask
import org.ucb.appp1.exercises.domain.model.Task
import org.ucb.appp1.exercises.domain.model.TaskSnapshot

class FirestoreTaskDataSource {
    private val tasks = FirebaseFirestore.getInstance().collection("tasks")

    fun observe(onChange: (TaskSnapshot) -> Unit, onError: (String) -> Unit): ListenerRegistration =
        tasks.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error.localizedMessage ?: "No se pudo consultar Firestore.")
                return@addSnapshotListener
            }
            val added = snapshot?.documentChanges
                ?.filter { it.type == DocumentChange.Type.ADDED }
                ?.map { it.document.toTask() }
                .orEmpty()
            onChange(TaskSnapshot(snapshot?.documents.orEmpty().map { it.toTask() }, added))
        }

    fun add(title: String, onError: (String) -> Unit) {
        tasks.add(mapOf("titulo" to title, "completada" to false, "timestamp" to Timestamp.now()))
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo agregar la tarea.") }
    }

    fun toggle(task: Task, onError: (String) -> Unit) {
        tasks.document(task.id).update("completada", !task.completed)
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo actualizar la tarea.") }
    }

    fun delete(task: Task, onError: (String) -> Unit) {
        tasks.document(task.id).delete()
            .addOnFailureListener { onError(it.localizedMessage ?: "No se pudo eliminar la tarea.") }
    }
}
