package com.annguyen.dmed.domain.statemachine

import com.tinder.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import logcat.logcat
import javax.inject.Inject

sealed class ViewSingleComicState {
    object Idle : ViewSingleComicState()
    object Refreshing : ViewSingleComicState()
    object DataLoaded : ViewSingleComicState()
    object Error : ViewSingleComicState()
}

sealed class ViewSingleComicEvent {
    object Refresh : ViewSingleComicEvent()
    object GetError : ViewSingleComicEvent()
    object GetData : ViewSingleComicEvent()
    object Retry : ViewSingleComicEvent()
}

class ViewSingleComicStateMachine @Inject constructor() {

    val stateMachine = StateMachine.create<ViewSingleComicState, ViewSingleComicEvent, Any> {
        initialState(ViewSingleComicState.Idle)
        state<ViewSingleComicState.Idle> {
            on<ViewSingleComicEvent.Refresh> {
                transitionTo(ViewSingleComicState.Refreshing)
            }
        }

        state<ViewSingleComicState.Refreshing> {
            on<ViewSingleComicEvent.GetData> {
                transitionTo(ViewSingleComicState.DataLoaded)
            }

            on<ViewSingleComicEvent.GetError> {
                transitionTo(ViewSingleComicState.Error)
            }
        }

        state<ViewSingleComicState.Error> {
            on<ViewSingleComicEvent.Retry> {
                transitionTo(ViewSingleComicState.Refreshing)
            }
        }

        state<ViewSingleComicState.DataLoaded> {  }

        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            logcat { "Single Comic state machine $validTransition" }
            _transitioningFlow.value = validTransition
        }
    }

    private val _transitioningFlow =
        MutableStateFlow<StateMachine.Transition.Valid<ViewSingleComicState, ViewSingleComicEvent, Any>?>(
            null
        )

    val transitionFlow:
            Flow<StateMachine.Transition.Valid<ViewSingleComicState, ViewSingleComicEvent, Any>?> =
        _transitioningFlow
}