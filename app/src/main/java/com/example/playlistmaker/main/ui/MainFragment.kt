package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.library.ui.LibraryFragment
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.settings.ui.SettingsFragment

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SearchFragment(),
                    SearchFragment::class.java.simpleName
                )
                addToBackStack(SearchFragment::class.java.simpleName)
            }
        }

        binding.libraryButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    LibraryFragment(),
                    LibraryFragment::class.java.simpleName
                )
                addToBackStack(LibraryFragment::class.java.simpleName)
            }
        }

        binding.settingsButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.rootFragmentContainerView,
                    SettingsFragment(),
                    SettingsFragment::class.java.simpleName
                )
                addToBackStack(SettingsFragment::class.java.simpleName)
            }
        }
    }
}
