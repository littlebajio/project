package com.example.moodie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MovieFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_movie, container, false)

        val movie = arguments?.getParcelable<Movie>(ARG_MOVIE)

        val titleTextView = view.findViewById<TextView>(R.id.movieTitle)
        val moodTextView = view.findViewById<TextView>(R.id.movieMood)
        val descriptionTextView = view.findViewById<TextView>(R.id.movieDescription)
        val imageView = view.findViewById<ImageView>(R.id.movieImage)
        val favoriteButton = view.findViewById<Button>(R.id.favoriteButton)

        movie?.let { currentMovie ->
            titleTextView.text = currentMovie.title
            moodTextView.text = "Настроение: ${currentMovie.mood}"
            descriptionTextView.text = currentMovie.description
            imageView.setImageResource(currentMovie.imageResId)

            favoriteButton.setOnClickListener {
                (activity as? MainActivity)?.addToFavorites(currentMovie)
            }
        } ?: run {
            titleTextView.text = "Фильм не найден"
            moodTextView.text = ""
            descriptionTextView.text = ""
            favoriteButton.visibility = View.GONE
        }

        return view
    }

    companion object {
        private const val ARG_MOVIE = "movie"

        fun newInstance(movie: Movie) = MovieFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_MOVIE, movie)
            }
        }
    }
}
