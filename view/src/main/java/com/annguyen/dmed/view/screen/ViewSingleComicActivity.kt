package com.annguyen.dmed.view.screen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.annguyen.dmed.domain.statemachine.ViewSingleComicState
import com.annguyen.dmed.domain.utils.getBackgroundUrl
import com.annguyen.dmed.domain.utils.getOriginalUrl
import com.annguyen.dmed.view.R
import com.annguyen.dmed.view.databinding.ActivityViewSingleComicBinding
import com.annguyen.dmed.view.displayErrorSnackBar
import com.annguyen.dmed.view.viewmodel.ViewSingleComicViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.coroutines.launch
import logcat.logcat

const val INVALID_DEFAULT_COMIC_ID = -1

@AndroidEntryPoint
class ViewSingleComicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewSingleComicBinding
    private val viewModel: ViewSingleComicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSingleComicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bindUI(parseComicIdFromExternal())
    }

    private fun parseComicIdFromExternal(): Int {
        return try {
            var comicId = intent.data?.getQueryParameters("query")?.firstOrNull()?.toInt()
                ?: INVALID_DEFAULT_COMIC_ID

            if (comicId == INVALID_DEFAULT_COMIC_ID) {
                comicId = intent.getIntExtra(EXTRA_COMIC_ID, INVALID_DEFAULT_COMIC_ID)
            }

            comicId
        } catch (ex: Exception) {
            ex.printStackTrace()
            INVALID_DEFAULT_COMIC_ID
        }
    }

    private fun ActivityViewSingleComicBinding.bindUI(comicId: Int) {
        if (comicId == INVALID_DEFAULT_COMIC_ID) {
            onBackPressed()
            return
        }

        btnClose.setOnClickListener { onBackPressed() }
        lifecycleScope.launch {
            viewModel.loadComicById(comicId)
        }
        lifecycleScope.launch {
            viewModel.singleComicFlow.collect { comic ->
                logcat { "SingleComic data collected $comic" }
                Picasso
                    .get()
                    .load(comic.getBackgroundUrl())
                    .error(R.drawable.ic_baseline_no_photography_24)
                    .transform(BlurTransformation(applicationContext))
                    .into(imvCover, object : Callback {
                        override fun onSuccess() {
                            imvCover.isVisible = true
                            shimmerImvCover.stopShimmer()
                            shimmerImvCover.isVisible = false
                        }

                        override fun onError(e: Exception?) {
                            imvCover.isVisible = true
                            shimmerImvCover.stopShimmer()
                            shimmerImvCover.isVisible = false
                            e?.printStackTrace()
                        }
                    })

                Picasso
                    .get()
                    .load(comic.getOriginalUrl())
                    .error(R.drawable.ic_baseline_no_photography_24)
                    .into(imvMain, object : Callback {
                        override fun onSuccess() {
                            imvMain.isVisible = true
                            shimmerImvMain.stopShimmer()
                            shimmerImvMain.isVisible = false
                        }

                        override fun onError(e: Exception?) {
                            imvMain.isVisible = true
                            shimmerImvMain.stopShimmer()
                            shimmerImvMain.isVisible = false
                            e?.printStackTrace()
                        }
                    })

                tvTitle.text = comic.title
                tvDescription.text = comic.description
            }
        }

        lifecycleScope.launch {
            viewModel.stateTransitionFlow.collect { transition ->
                logcat { "SingleComic Transition $transition" }
                when (transition.toState) {
                    is ViewSingleComicState.Error -> {
                        // Error UI
                        progressBar.isVisible = false
                        coordinator.displayErrorSnackBar(
                            R.string.error_text,
                            R.string.retry,
                            autoDismiss = false,
                            action = {
                                viewModel.retry(comicId = comicId)
                            }
                        )
                    }
                    is ViewSingleComicState.DataLoaded -> {
                        // Data loaded UI
                        progressBar.isVisible = false
                    }
                    is ViewSingleComicState.Refreshing -> {
                        // Refreshing UI
                        progressBar.isVisible = true
                    }
                    is ViewSingleComicState.Idle -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}