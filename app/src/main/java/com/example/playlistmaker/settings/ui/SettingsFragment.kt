package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.settingsActivityToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state ->
            binding.smThemeSwitcher.isChecked = state.isDarkTheme
        }

        binding.smThemeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitched(isChecked)
        }

        viewModel.observeShareIntent().observe(viewLifecycleOwner) { intent ->
            startActivity(Intent.createChooser(intent, getString(R.string.share_url)))
        }

        viewModel.observeContactIntent().observe(viewLifecycleOwner) { intent ->
            startActivity(intent)
        }

        viewModel.observeOpenLink().observe(viewLifecycleOwner) { uri ->
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.shareListOption.setOnClickListener {
            viewModel.onShareClicked(getString(R.string.share_url))
        }

        binding.contactSupportListOption.setOnClickListener {
            viewModel.onContactClicked(
                email = getString(R.string.my_email),
                subject = getString(R.string.message_subject),
                message = getString(R.string.message)
            )
        }

        binding.termsOfUseListOption.setOnClickListener {
            viewModel.onTermsClicked(getString(R.string.terms_of_use_url))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
