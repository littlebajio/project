package com.example.moodie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val items: List<Any>,
    private val onMovieClick: ((Movie) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_CATEGORY = 1
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.movieTitle)
        val image: ImageView = itemView.findViewById(R.id.movieImage)
        val desc: TextView = itemView.findViewById(R.id.movieDescription)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.categoryTitle)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Movie -> TYPE_MOVIE
            else -> TYPE_CATEGORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MOVIE -> MovieViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie, parent, false)
            )
            else -> CategoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieViewHolder -> {
                val movie = items[position] as Movie
                holder.title.text = movie.title
                holder.image.setImageResource(movie.imageResId)
                holder.desc.text = movie.description
                holder.itemView.setOnClickListener { onMovieClick?.invoke(movie) }
            }
            is CategoryViewHolder -> {
                val category = items[position] as String
                holder.title.text = category
                holder.itemView.setOnClickListener {
                    // Обработка нажатия на категорию
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}