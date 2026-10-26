package org.ucb.appp1

import org.ucb.appp1.designsystem.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.ucb.appp1.starwars.presentation.screen.StarWarsScreen

@Composable
@Preview
fun App() {
    AppTheme {
        StarWarsScreen()
    }
}
