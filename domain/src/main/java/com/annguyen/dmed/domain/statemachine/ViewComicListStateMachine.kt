package com.annguyen.dmed.domain.statemachine

import com.tinder.StateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import logcat.logcat
import javax.inject.Inject

sealed class ViewComicListState {
    object Idle : ViewComicListState()
    object Refresh : ViewComicListState()
    object Error : ViewComicListState()
    object Empty : ViewComicListState()
    object DataLoaded : ViewComicListState()
    object LoadingMore : ViewComicListState()
    object LoadMoreError : ViewComicListState()
    object LoadMoreEmpty : ViewComicListState()
}

sealed class ViewComicListEvent {
    object Refresh : ViewComicListEvent()
    object Retry : ViewComicListEvent()
    object GetError : ViewComicListEvent()
    object GetEmpty : ViewComicListEvent()
    object GetData : ViewComicListEvent()
    object LoadMore : ViewComicListEvent()
}

class ViewComicListStateMachine @Inject constructor() {
    val stateMachine =
        StateMachine.create<ViewComicListState, ViewComicListEvent, Any> {
            initialState(ViewComicListState.Idle)
            state<ViewComicListState.Idle> {
                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }
            }

            state<ViewComicListState.Refresh> {
                on<ViewComicListEvent.GetError> {
                    transitionTo(ViewComicListState.Error)
                }

                on<ViewComicListEvent.GetEmpty> {
                    transitionTo(ViewComicListState.Empty)
                }

                on<ViewComicListEvent.GetData> {
                    transitionTo(ViewComicListState.DataLoaded)
                }
            }

            state<ViewComicListState.Error> {
                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }
            }

            state<ViewComicListState.Empty> {
                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }
            }

            state<ViewComicListState.DataLoaded> {
                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }

                on<ViewComicListEvent.LoadMore> {
                    transitionTo(ViewComicListState.LoadingMore)
                }
            }

            state<ViewComicListState.LoadingMore> {
                on<ViewComicListEvent.GetData> {
                    transitionTo(ViewComicListState.DataLoaded)
                }

                on<ViewComicListEvent.GetError> {
                    transitionTo(ViewComicListState.LoadMoreError)
                }

                on<ViewComicListEvent.GetEmpty> {
                    transitionTo(ViewComicListState.LoadMoreEmpty)
                }
            }

            state<ViewComicListState.LoadMoreEmpty> {
                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }
            }

            state<ViewComicListState.LoadMoreError> {
                on<ViewComicListEvent.Retry> {
                    transitionTo(ViewComicListState.LoadingMore)
                }

                on<ViewComicListEvent.Refresh> {
                    transitionTo(ViewComicListState.Refresh)
                }
            }

            onTransition {
                val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
                logcat { "Single Comic state machine $validTransition" }
                _transitioningFlow.value = validTransition
            }
        }

    private val _transitioningFlow =
        MutableStateFlow<StateMachine.Transition.Valid<ViewComicListState, ViewComicListEvent, Any>?>(
            null
        )

    val transitionFlow:
            Flow<StateMachine.Transition.Valid<ViewComicListState, ViewComicListEvent, Any>?> =
        _transitioningFlow
}