package com.udemy.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.udemy.a7minuteworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /*
    -View binding is a feature that makes it easier to write code that interacts with views.
    Once view binding is enabled in a module, it generates a binding class for each XML layout
    file present in that module. An instance of a binding class contains direct references to all
    views that have an ID in the corresponding layout.
    -In most cases, view binding replaces findViewById.
    - It first must be set in the app gradle
    - variables can implement binding globally instead of have to be set globally and in methods or on create
    - Always use the onDestroy method to set the binding to null once activity is complete to avoid memory leakage
    - Binding ensures that only variables from the corresponding xml are accessed in the activity
     */
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set binding prior to setting content view
        // Use Activity Main, inflate it and set it to the binding object
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Content view is then set using binding
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener{
            val intent = Intent(this@MainActivity, ExerciseActivity::class.java)
            startActivity(intent)
        }

        binding?.flBMI?.setOnClickListener{
            val intent = Intent(this@MainActivity, BMIActivity::class.java)
            startActivity(intent)
        }

        binding?.flHistory?.setOnClickListener{
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}