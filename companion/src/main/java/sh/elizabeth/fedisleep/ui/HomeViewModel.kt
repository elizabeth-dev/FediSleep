package sh.elizabeth.fedisleep.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sh.elizabeth.fedisleep.data.repository.AuthRepository
import sh.elizabeth.fedisleep.data.repository.InternalDataRepository
import javax.inject.Inject
import kotlin.text.get

sealed interface HomeUiState {
    val asleepName: String
    val awakeName: String

    data class Loading(
        override val asleepName: String, override val awakeName: String
    ) : HomeUiState

    data class NotLoggedIn(
        override val asleepName: String, override val awakeName: String
    ) : HomeUiState

    data class LoggedIn(
        override val asleepName: String,
        override val awakeName: String,
        val loggedInProfiles: List<String>,
        val activeAccount: String
    ) : HomeUiState
}

fun toUiState(
    asleepName: String,
    awakeName: String,
    activeAccount: String? = null,
    loggedInProfiles: List<String>? = null,
    isAuthLoading: Boolean = true,
): HomeUiState = if (isAuthLoading) {
    HomeUiState.Loading(asleepName, awakeName)
} else if (!loggedInProfiles.isNullOrEmpty() && !activeAccount.isNullOrBlank()) {
    HomeUiState.LoggedIn(asleepName, awakeName, loggedInProfiles, activeAccount)
} else {
    HomeUiState.NotLoggedIn(asleepName, awakeName)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val internalDataRepository: InternalDataRepository
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = authRepository.internalData.map { internalData ->
        toUiState(
            activeAccount = internalData.activeAccount,
            loggedInProfiles = internalData.accountTokens.keys.toList(),
            isAuthLoading = false,
            asleepName = internalData.accountSettings[internalData.activeAccount]?.asleepName ?: "",
            awakeName = internalData.accountSettings[internalData.activeAccount]?.awakeName ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState.Loading("", ""))

    fun onLogin(instance: String) {
        viewModelScope.launch {
            authRepository.prepareOAuth(instance)
            authRepository.finishOAuth("O77mn4FogUVz89E4r8wMU4rw2dNhkMIa")
        }
    }

    fun setNames(activeAccount: String, asleepName: String, awakeName: String) {
        viewModelScope.launch {
            internalDataRepository.setNames(activeAccount, asleepName, awakeName)
        }
    }
}
