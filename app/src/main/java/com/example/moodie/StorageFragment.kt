package com.example.moodie

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StorageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_storage, container, false)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val mainActivity = activity as? MainActivity
        if (mainActivity != null) {
            adapter = MovieAdapter(mainActivity.favorites, mainActivity)
            recyclerView.adapter = adapter
        } else {
            Log.e("StorageFragment", "Activity is not MainActivity")
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList() {
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    class MovieAdapter(
        private val movies: MutableList<Movie>,
        private val mainActivity: MainActivity?
    ) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

        inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.movieTitle)
            private val imageView: ImageView = itemView.findViewById(R.id.movieImage)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.movieDescription)
            private val removeButton: Button = itemView.findViewById(R.id.removeFavoriteButton)

            fun bind(movie: Movie) {
                titleTextView.text = movie.title
                imageView.setImageResource(movie.imageResId)
                descriptionTextView.text = movie.description

                removeButton.visibility = View.VISIBLE
                removeButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val movieToRemove = movies[position]
                        mainActivity?.removeFromFavorites(movieToRemove)
                    }
                }

                itemView.setOnClickListener {
                    mainActivity?.showMovie(movie)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie, parent, false)
            return MovieViewHolder(view)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            holder.bind(movies[position])
        }

        override fun getItemCount(): Int = movies.size
    }
}
