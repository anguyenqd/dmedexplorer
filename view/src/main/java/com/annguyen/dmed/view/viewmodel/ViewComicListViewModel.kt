package com.annguyen.dmed.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.annguyen.dmed.domain.statemachine.*
import com.annguyen.dmed.domain.usecases.ViewComicListUseCase
import com.tinder.StateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class ViewComicListViewModel @Inject constructor(
    viewComicListUseCase: ViewComicListUseCase,
    private val viewComicListStateMachine: ViewComicListStateMachine
) : ViewModel() {
    val comicListPagingData = viewComicListUseCase.invoke().cachedIn(viewModelScope)

    fun onRefresh() {
        viewComicListStateMachine.stateMachine.transition(ViewComicListEvent.Refresh)
    }

    fun onRefreshFinished() {
        viewComicListStateMachine.stateMachine.transition(ViewComicListEvent.GetData)
    }

    fun onRefreshError() {
        viewComicListStateMachine.stateMachine.transition(ViewComicListEvent.GetError)
    }

    val stateTransitionFlow:
            Flow<StateMachine.Transition.Valid<ViewComicListState, ViewComicListEvent, Any>> =
        viewComicListStateMachine
            .transitionFlow
            .filterNotNull()
}