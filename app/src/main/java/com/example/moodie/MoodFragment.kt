package com.example.moodie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MoodFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.moodRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        recyclerView.adapter = object : RecyclerView.Adapter<MoodViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                MoodViewHolder(layoutInflater.inflate(R.layout.item_mood, parent, false))

            override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
                holder.bind(MovieData.moods[position])
                holder.itemView.setOnClickListener {
                    MovieData.getRandomByMood(MovieData.moods[position])?.let { movie ->
                        (activity as? MainActivity)?.showMovie(movie)
                    }
                }
            }

            override fun getItemCount() = MovieData.moods.size
        }

        return view
    }

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(mood: String) {
            itemView.findViewById<TextView>(R.id.moodText).text = mood
        }
    }
}