package com.example.android_labs

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var adapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        adapter = CharacterAdapter()
        initialiseRecyclerView()
        observeCharacterList()

        findViewById<Button>(R.id.btnGetCharacters).setOnClickListener {
            getRandomCharacters()
        }
    }

    private fun initialiseRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeCharacterList() {
        viewModel.characterData.observe(this) { characters ->
            adapter.submitList(characters)
        }
    }

    private fun getRandomCharacters() {
        val randomIds = (1..826).shuffled().take(10)
        viewModel.fetchCharacters(randomIds)
    }
}