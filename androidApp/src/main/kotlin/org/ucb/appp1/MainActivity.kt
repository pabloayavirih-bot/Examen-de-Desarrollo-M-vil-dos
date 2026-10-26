package org.ucb.appp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private val openTasksInitially = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        openTasksInitially.value = intent?.getBooleanExtra(EXTRA_OPEN_TASKS, false) == true

        setContent {
            MobileExercisesApp(openTasksInitially = openTasksInitially.value)
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openTasksInitially.value = intent.getBooleanExtra(EXTRA_OPEN_TASKS, false)
    }

    companion object {
        const val EXTRA_OPEN_TASKS = "open_tasks"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MobileExercisesApp()
}
