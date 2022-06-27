package com.annguyen.dmed.view.screen

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.annguyen.dmed.domain.statemachine.ViewComicListState
import com.annguyen.dmed.view.R
import com.annguyen.dmed.view.databinding.ViewComicListActivityBinding
import com.annguyen.dmed.view.displayErrorSnackBar
import com.annguyen.dmed.view.displayNormalSnackBar
import com.annguyen.dmed.view.ui.ComicAdapter
import com.annguyen.dmed.view.ui.ComicLoadStateAdapter
import com.annguyen.dmed.view.ui.OnItemClickListener
import com.annguyen.dmed.view.viewmodel.ViewComicListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import logcat.logcat

const val EXTRA_COMIC_ID = "extra.comic.id"

@AndroidEntryPoint
class ViewComicListActivity : AppCompatActivity() {

    private val viewModel: ViewComicListViewModel by viewModels()
    private lateinit var binding: ViewComicListActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewComicListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bindUI()
    }

    private fun ViewComicListActivityBinding.bindUI() {
        val adapter = ComicAdapter(object : OnItemClickListener {
            override fun onItemClicked(id: Int) {
                startActivity(
                    Intent(
                        this@ViewComicListActivity,
                        ViewSingleComicActivity::class.java
                    ).apply {
                        putExtra(EXTRA_COMIC_ID, id)
                    }
                )
            }
        })
        rclView.adapter = adapter.withLoadStateFooter(
            footer = ComicLoadStateAdapter(retry = { adapter.retry() })
        )

        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy { it.mediator }.collect { loadState ->
                logcat { "ComicListFragment loadState ${loadState.mediator?.refresh}" }
                when (loadState.mediator?.refresh) {
                    is LoadState.Loading -> viewModel.onRefresh()
                    is LoadState.NotLoading -> viewModel.onRefreshFinished()
                    is LoadState.Error -> viewModel.onRefreshError()
                    null -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.stateTransitionFlow.collect { validTransition ->
                when {
                    validTransition.fromState is ViewComicListState.Refresh
                            && validTransition.toState is ViewComicListState.Empty -> {
                        coordinator.displayNormalSnackBar(R.string.empty_list_load_more_description)
                        startRefreshAnimation(false)
                    }
                    validTransition.fromState is ViewComicListState.Refresh
                            && validTransition.toState is ViewComicListState.Error -> {
                        coordinator.displayErrorSnackBar(R.string.error_text)
                        startRefreshAnimation(false)
                    }
                    validTransition.fromState is ViewComicListState.Refresh
                            && validTransition.toState is ViewComicListState.DataLoaded -> {
                        startRefreshAnimation(false)
                    }
                    validTransition.fromState is ViewComicListState.LoadingMore
                            && validTransition.toState is ViewComicListState.LoadMoreEmpty -> {
                        // Should be handled by LoadStateFooter adapter. This is just for logging
                        logcat { "Empty on Loading More" }
                    }
                    validTransition.fromState is ViewComicListState.LoadingMore
                            && validTransition.toState is ViewComicListState.LoadMoreError -> {
                        // Should be handled by LoadStateFooter adapter. This is just for logging
                        logcat { "Error on Loading more" }
                    }
                    validTransition.fromState is ViewComicListState.DataLoaded
                            && validTransition.toState is ViewComicListState.LoadingMore -> {
                        // Should be handled by LoadStateFooter adapter. This is just for logging
                        logcat { "Loading more" }
                    }
                    validTransition.toState is ViewComicListState.Refresh -> {
                        startRefreshAnimation(true)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.comicListPagingData.collectLatest(adapter::submitData)
        }

        btnRefresh.setOnClickListener {
            if (it?.animation == null || it.animation.hasEnded()) {
                adapter.refresh()
            }
        }
    }

    private fun ViewComicListActivityBinding.startRefreshAnimation(enable: Boolean) {
        if (enable) {
            btnRefresh.startAnimation(
                AnimationUtils.loadAnimation(
                    this@ViewComicListActivity,
                    R.anim.rotate_clockwise
                )
            )
        } else {
            btnRefresh.clearAnimation()
        }
    }
}