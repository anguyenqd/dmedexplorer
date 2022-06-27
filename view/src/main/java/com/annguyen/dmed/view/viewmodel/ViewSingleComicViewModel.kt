package com.annguyen.dmed.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.statemachine.ViewSingleComicEvent
import com.annguyen.dmed.domain.statemachine.ViewSingleComicState
import com.annguyen.dmed.domain.statemachine.ViewSingleComicStateMachine
import com.annguyen.dmed.domain.usecases.RefreshSingleComicUseCase
import com.annguyen.dmed.domain.usecases.ViewSingleComicUseCase
import com.tinder.StateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ViewSingleComicViewModel @Inject constructor(
    private val viewSingleComicUseCase: ViewSingleComicUseCase,
    private val refreshSingleComicUseCase: RefreshSingleComicUseCase,
    private val viewSingleComicStateMachine: ViewSingleComicStateMachine
) : ViewModel() {

    private val requestComicTrigger: MutableStateFlow<Int?> = MutableStateFlow(null)

    val singleComicFlow: Flow<Comic> = requestComicTrigger.flatMapLatest {
        if (it == null) return@flatMapLatest emptyFlow()
        viewSingleComicUseCase.invoke(it)
    }

    val stateTransitionFlow:
            Flow<StateMachine.Transition.Valid<ViewSingleComicState, ViewSingleComicEvent, Any>> =
        viewSingleComicStateMachine.transitionFlow
            .filterNotNull()

    suspend fun loadComicById(comicId: Int) = withContext(Dispatchers.IO) {
        viewSingleComicStateMachine.stateMachine.transition(ViewSingleComicEvent.Refresh)
        requestComicTrigger.value = comicId
        refreshComicData(comicId)
    }

    fun retry(comicId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            viewSingleComicStateMachine.stateMachine.transition(ViewSingleComicEvent.Retry)
            refreshComicData(comicId)
        }
    }

    private suspend fun refreshComicData(comicId: Int) = withContext(Dispatchers.IO) {
        val result = refreshSingleComicUseCase.invoke(comicId)
        if (result.isSuccess) {
            viewSingleComicStateMachine.stateMachine.transition(ViewSingleComicEvent.GetData)
        } else {
            viewSingleComicStateMachine.stateMachine.transition(ViewSingleComicEvent.GetError)
        }
    }
}