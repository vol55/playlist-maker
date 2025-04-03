package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SearchActivity : AppCompatActivity() {
    private lateinit var searchFieldValue: String
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val searchInput = findViewById<EditText>(R.id.input_field)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFieldValue = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        if (savedInstanceState != null) {
            searchFieldValue = savedInstanceState.getString(SEARCH_FIELD_VALUE, "")
            searchInput.setText(searchFieldValue)
        }

        searchInput.addTextChangedListener(textWatcher)

        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_VALUE, searchFieldValue)
    }

    companion object {
        private const val SEARCH_FIELD_VALUE = "SEARCH_STRING"
    }
}
