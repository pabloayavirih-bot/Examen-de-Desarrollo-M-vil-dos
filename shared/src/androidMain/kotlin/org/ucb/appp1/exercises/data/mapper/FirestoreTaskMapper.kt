package org.ucb.appp1.exercises.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import org.ucb.appp1.exercises.domain.model.Task

fun DocumentSnapshot.toTask(): Task = Task(
    id = id,
    title = getString("titulo").orEmpty(),
    completed = getBoolean("completada") ?: false,
    timestampMillis = getTimestamp("timestamp")?.toDate()?.time
)
