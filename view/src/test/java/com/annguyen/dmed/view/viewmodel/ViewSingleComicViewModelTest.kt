package com.annguyen.dmed.view.viewmodel

import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.statemachine.ViewSingleComicState
import com.annguyen.dmed.domain.statemachine.ViewSingleComicStateMachine
import com.annguyen.dmed.domain.usecases.RefreshSingleComicUseCase
import com.annguyen.dmed.domain.usecases.ViewSingleComicUseCase
import com.annguyen.dmed.view.fake.FakeRepository
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewSingleComicViewModelTest {

    // Faking the repository implementation
    private val repository = FakeRepository().also {
        it.setComicToBeReturnedForRefreshing(
            Comic(
                id = FIRST_COMIC_ID,
                title = "new title",
                description = null,
                imageUrl = "",
                imageUrlExtension = ""
            )
        )
    }

    private val viewSingleComicStateMachine = ViewSingleComicStateMachine()
    private val viewSingleComicUseCase = ViewSingleComicUseCase(repository)
    private val refreshSingleComicUseCase = RefreshSingleComicUseCase(repository)

    private val viewModel = ViewSingleComicViewModel(
        viewSingleComicStateMachine = viewSingleComicStateMachine,
        viewSingleComicUseCase = viewSingleComicUseCase,
        refreshSingleComicUseCase = refreshSingleComicUseCase
    )

    @Test
    fun requestComicDataWithComicId() = runTest {
        val comicId = 1
        viewModel.loadComicById(comicId)
        val comic = viewModel.singleComicFlow.first()
        assertNotNull(comic)
        assert(comic.id == comicId)
    }

    @Test
    fun requestComicDataThenRefreshWithSameComicId() = runTest {
        val comicId = 1
        val values = mutableListOf<Comic>()
        // Eagerly collect data into the values
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.singleComicFlow.toList(values)
        }

        // Start loading comic with the input comic id
        viewModel.loadComicById(comicId)

        // At this point, data is all refreshed
        assert(viewSingleComicStateMachine.stateMachine.state is ViewSingleComicState.DataLoaded)

        // The first emitted comic is from local data
        val firstEmittedComic = values.first()
        assertNotNull(firstEmittedComic)
        assert(firstEmittedComic.id == comicId)

        // the 2nd emitted comic is refreshed data from network
        val refreshedComic = values[1]
        assertNotNull(refreshedComic)
        assert(firstEmittedComic.id == refreshedComic.id)
        assert(refreshedComic.title == "new title")

        collectJob.cancel()
    }

    companion object {
        private const val FIRST_COMIC_ID = 1
    }
}