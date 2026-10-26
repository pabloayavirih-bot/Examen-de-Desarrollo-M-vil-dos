package org.ucb.appp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import org.ucb.appp1.exercises.presentation.MobileExercisesApp
import org.ucb.appp1.exercises.presentation.notification.NotificationDestination

class MainActivity : ComponentActivity() {
    private val openTasksInitially = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        openTasksInitially.value = intent?.getBooleanExtra(NotificationDestination.EXTRA_OPEN_TASKS, false) == true

        setContent {
            MobileExercisesApp(openTasksInitially = openTasksInitially.value)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTasksInitially.value = intent.getBooleanExtra(NotificationDestination.EXTRA_OPEN_TASKS, false)
    }
}
