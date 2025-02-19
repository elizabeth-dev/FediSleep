package sh.elizabeth.fedisleep.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.elizabeth.fedisleep.ui.HomeUiState
import sh.elizabeth.fedisleep.ui.HomeViewModel

@Composable
fun Home(homeViewModel: HomeViewModel = hiltViewModel(), innerPadding: PaddingValues) {
    val _uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val awakeNameFocusRequester = remember { FocusRequester() }

    Column(
        Modifier
            .padding(innerPadding)
            .wrapContentSize(Alignment.Center)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val uiState = _uiState) {
            is HomeUiState.Loading -> return
            is HomeUiState.NotLoggedIn -> {
                val (instance, setInstance) = remember { mutableStateOf("") }

                Column {
                    TextField(
                        value = instance,
                        onValueChange = { setInstance(it) },
                        label = { Text("Instance") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go, keyboardType = KeyboardType.Uri
                        ),
                        keyboardActions = KeyboardActions(onGo = {
                            homeViewModel.onLogin(instance)
                        })
                    )
                    Spacer(Modifier.size(16.dp))
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { homeViewModel.onLogin(instance) }) {
                        Text(
                            "Login", style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                return
            }

            is HomeUiState.LoggedIn -> {
                val (asleepName, setAsleepName) = remember { mutableStateOf("") }
                val (awakeName, setAwakeName) = remember { mutableStateOf("") }

                Column {
                    Text("Logged in as ${uiState.activeAccount}")
                    Spacer(Modifier.size(16.dp))

                    TextField(
                        value = asleepName,
                        onValueChange = { setAsleepName(it) },
                        label = { Text("Asleep name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go, keyboardType = KeyboardType.Uri
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            awakeNameFocusRequester.requestFocus()
                        })
                    )
                    Spacer(Modifier.size(16.dp))

                    TextField(
                        value = awakeName,
                        onValueChange = { setAwakeName(it) },
                        label = { Text("Awake name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Go, keyboardType = KeyboardType.Uri
                        ),
                        keyboardActions = KeyboardActions(onGo = {
                            homeViewModel.setNames(uiState.activeAccount, asleepName, awakeName)
                        }),
                        modifier = Modifier.focusRequester(awakeNameFocusRequester)
                    )
                    Spacer(Modifier.size(16.dp))
                    Button(
                        modifier = Modifier.align(Alignment.End), onClick = {
                            homeViewModel.setNames(
                                uiState.activeAccount, asleepName, awakeName
                            )
                        }) {
                        Text(
                            "Login", style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}