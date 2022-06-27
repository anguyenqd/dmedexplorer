package com.annguyen.dmed.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.annguyen.dmed.view.R
import com.annguyen.dmed.view.databinding.ComicLoadHeaderFooterViewItemBinding

class ComicLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ComicLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ComicLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ComicLoadStateViewHolder = ComicLoadStateViewHolder(
        binding = ComicLoadHeaderFooterViewItemBinding.bind(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comic_load_header_footer_view_item,
                parent,
                false
            )
        ),
        retry = retry
    )

}

class ComicLoadStateViewHolder(
    private val binding: ComicLoadHeaderFooterViewItemBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        when (loadState) {
            is LoadState.Error -> {
                binding.errorMsg.isVisible = true
                binding.retryButton.isVisible = true
                binding.progressBar.isVisible = false
            }

            is LoadState.Loading -> {
                binding.errorMsg.isVisible = false
                binding.retryButton.isVisible = false
                binding.progressBar.isVisible = true
            }

            is LoadState.NotLoading -> {
                binding.errorMsg.isVisible = false
                binding.retryButton.isVisible = false
                binding.progressBar.isVisible = false
            }
        }
    }
}