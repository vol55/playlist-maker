package com.example.playlistmaker.root.ui

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val hiddenBotNavScreens = setOf(
            R.id.playerFragment,
            R.id.addPlaylistFragment,
            R.id.playlistDetailsFragment,
            R.id.editPlaylistFragment,
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.isVisible = destination.id !in hiddenBotNavScreens
            binding.bottomNavigationBorder.isVisible = destination.id !in hiddenBotNavScreens
        }

        val customBackHandledScreens = setOf(
            R.id.playerFragment,
            R.id.playlistDetailsFragment,
        )

        onBackPressedDispatcher.addCallback(this) {
            if (navController.currentDestination?.id in customBackHandledScreens) {
                if (!navController.popBackStack()) finish()
            } else {
                moveTaskToBack(true)
            }
        }
    }
}
