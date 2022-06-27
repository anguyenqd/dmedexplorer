package com.annguyen.dmed.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.annguyen.dmed.domain.model.Comic
import com.annguyen.dmed.domain.utils.getThumbnailUrl
import com.annguyen.dmed.view.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ComicAdapter(private val onItemClickListener: OnItemClickListener) :
    PagingDataAdapter<Comic, ComicViewHolder>(IMAGES_COMPARATOR) {
    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        holder.bind(
            getItem(position) ?: throw UnsupportedOperationException(
                "Item in ComicAdapter must not be NULL, check the paging setup!"
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder =
        ComicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comic_item, parent, false),
            onItemClickListener
        )

    companion object {
        private val IMAGES_COMPARATOR = object : DiffUtil.ItemCallback<Comic>() {
            override fun areItemsTheSame(oldItem: Comic, newItem: Comic): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Comic, newItem: Comic): Boolean =
                oldItem == newItem
        }
    }
}

class ComicViewHolder(
    private val view: View,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.ViewHolder(view) { // TODO Use view binding??

    private val tvTitle: TextView = view.findViewById(R.id.tv_title)
    private val tvDescription: TextView = view.findViewById(R.id.tv_description)
    private val imageView: ImageView = view.findViewById(R.id.main_image)
    private val shimmer: ShimmerFrameLayout = view.findViewById(R.id.shimmer_imv_main)

    fun bind(comic: Comic) {
        view.setOnClickListener { onItemClickListener.onItemClicked(id = comic.id) }
        tvTitle.text = comic.title
        tvDescription.text = comic.description

        imageView.visibility = View.INVISIBLE
        shimmer.isVisible = true

        Picasso
            .get()
            .load(comic.getThumbnailUrl())
            .error(R.drawable.ic_baseline_no_photography_24)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView.isVisible = true
                    shimmer.stopShimmer()
                    shimmer.isVisible = false
                }

                override fun onError(e: Exception?) {
                    imageView.isVisible = true
                    shimmer.stopShimmer()
                    shimmer.isVisible = false
                    e?.printStackTrace()
                }
            })
    }
}

interface OnItemClickListener {
    fun onItemClicked(id: Int)
}