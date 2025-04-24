package com.example.moodie

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    val favorites = mutableListOf<Movie>()
    private val gson = Gson()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted.")
                startMovieService()
            } else {
                Log.w("MainActivity", "Notification permission denied.")
                Toast.makeText(this, "Разрешение на уведомления не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("MoodiePrefs", 0)
        loadFavorites()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_movies -> showRandomMovie()
                R.id.nav_mood -> showMoods()
                R.id.nav_storage -> showFavoritesFragment()
            }
            true
        }

        askNotificationPermissionAndStartService()
        handleIntent(intent)

        if (savedInstanceState == null && intent.getIntExtra(MovieService.MOVIE_ID_EXTRA, -1) == -1) {
            showRandomMovie()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val movieId = intent.getIntExtra(MovieService.MOVIE_ID_EXTRA, -1)
        if (movieId != -1) {
            Log.d("MainActivity", "Launched from notification with movie ID: $movieId")
            val movieToShow = MovieData.allMovies.find { it.id == movieId }
            if (movieToShow != null) {
                showMovie(movieToShow)
            } else {
                Log.w("MainActivity", "Movie with ID $movieId not found.")
                if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
                    showRandomMovie()
                }
            }
            intent.removeExtra(MovieService.MOVIE_ID_EXTRA)
        }
    }

    private fun askNotificationPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Notification permission already granted.")
                    startMovieService()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.i("MainActivity", "Showing rationale for notification permission.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.d("MainActivity", "Requesting notification permission.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            startMovieService()
        }
    }

    private fun startMovieService() {
        Log.d("MainActivity", "Starting MovieService.")
        val serviceIntent = Intent(this, MovieService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    fun showMovie(movie: Movie) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MovieFragment.newInstance(movie))
            .addToBackStack(null)
            .commit()
    }

    fun addToFavorites(movie: Movie) {
        if (favorites.none { it.id == movie.id }) {
            favorites.add(movie)
            saveFavorites()
            Toast.makeText(this, "${movie.title} добавлен в избранное", Toast.LENGTH_SHORT).show()
            updateFavoritesFragment()
        } else {
            Toast.makeText(this, "${movie.title} уже в избранном", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFromFavorites(movie: Movie) {
        val removed = favorites.removeAll { it.id == movie.id }
        if (removed) {
            saveFavorites()
            Toast.makeText(this, "${movie.title} удален из избранного", Toast.LENGTH_SHORT).show()
            updateFavoritesFragment()
        }
    }

    private fun showRandomMovie() {
        MovieData.allMovies.randomOrNull()?.let { showMovie(it) }
            ?: Log.w("MainActivity", "Movie list is empty, cannot show random movie.")
    }

    private fun showMoods() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MoodFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showFavoritesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StorageFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun updateFavoritesFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is StorageFragment) {
            fragment.updateList()
        }
    }

    private fun saveFavorites() {
        prefs.edit().putString("favorites", gson.toJson(favorites)).apply()
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorites", null)
        if (json != null) {
            try {
                val items = gson.fromJson(json, Array<Movie>::class.java)
                favorites.clear()
                favorites.addAll(items)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading favorites from JSON", e)
            }
        }
    }
}
