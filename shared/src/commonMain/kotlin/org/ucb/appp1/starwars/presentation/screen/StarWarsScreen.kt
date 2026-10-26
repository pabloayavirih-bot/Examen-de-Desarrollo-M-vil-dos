package org.ucb.appp1.starwars.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import appp1.shared.generated.resources.*
import org.ucb.appp1.starwars.presentation.viewmodel.StarWarsEffect
import org.ucb.appp1.starwars.presentation.viewmodel.StarWarsEvent
import org.ucb.appp1.starwars.presentation.viewmodel.StarWarsViewModel
import org.ucb.appp1.designsystem.PrimaryButton

@Composable
fun StarWarsScreen(viewModel: StarWarsViewModel = koinViewModel()) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is StarWarsEffect.ShowToast -> {
                    // Todo
                }
                StarWarsEffect.NavigateToBack -> {
                    // Todo
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = state.value.characterId,
            onValueChange = {
                viewModel.emitEvent(StarWarsEvent.OnCharacterIdChange(it))
            },
            label = { Text(stringResource(Res.string.character_id)) }
        )
        PrimaryButton(text = stringResource(Res.string.search), onClick = {
            viewModel.emitEvent(StarWarsEvent.OnSubmit)
        })

        if (state.value.isLoading) {
            CircularProgressIndicator()
        }

        state.value.error?.let {
            Text(stringResource(Res.string.error_label, it))
        }

        state.value.name?.let {
            Text(stringResource(Res.string.name_label, it))
        }
        state.value.height?.let {
            Text(stringResource(Res.string.height_label, it))
        }
        state.value.mass?.let {
            Text(stringResource(Res.string.mass_label, it))
        }
        state.value.hairColor?.let {
            Text(stringResource(Res.string.hair_label, it))
        }
        state.value.skinColor?.let {
            Text(stringResource(Res.string.skin_label, it))
        }
        state.value.eyeColor?.let {
            Text(stringResource(Res.string.eyes_label, it))
        }
        state.value.gender?.let {
            Text(stringResource(Res.string.gender_label, it))
        }
    }
}
