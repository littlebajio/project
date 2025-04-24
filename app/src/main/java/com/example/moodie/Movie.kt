package com.example.moodie
import com.example.moodie.R
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val mood: String,
    val description: String,
    val genre: String,
    val imageResId: Int
) : Parcelable

object MovieData {
    val genres = listOf("Комедия", "Драма", "Боевик", "Фантастика")
    val moods = listOf("Веселое", "Грустное", "Романтическое", "Энергичное")

    val allMovies = listOf(
        Movie(1, "Маска", "Веселое", "Классическая комедия", "Комедия", R.drawable.maska),
        Movie(2, "Титаник", "Грустное", "История любви", "Драма", R.drawable.tit),
        Movie(3, "Форсаж", "Энергичное", "Гонки и экшен", "Боевик", R.drawable.forsazh),
        Movie(4, "Назад в будущее", "Веселое", "Путешествия во времени", "Фантастика", R.drawable.btf),
        Movie(5, "Дневник памяти", "Романтическое", "Трогательная история любви", "Драма", R.drawable.diary)
    )

    fun getRandomByMood(mood: String): Movie? {
        val filtered = allMovies.filter { it.mood == mood }
        return filtered.randomOrNull()
    }
}
