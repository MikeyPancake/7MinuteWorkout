package com.udemy.a7minuteworkout

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.udemy.a7minuteworkout.databinding.ActivityExerciseBinding
import com.udemy.a7minuteworkout.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {

    // Object for initializing binding
    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializes binding
        // inflate the layout
        binding = ActivityFinishBinding.inflate(layoutInflater)

        // Content view is then set using binding
        // pass in binding?.root in the content view
        setContentView(binding?.root)

        //Create Tool Bar
        // then set support action bar and get toolBar Exerciser using the binding variable
        setSupportActionBar(binding?.toolBarExercise)

        // Add back button to tool bar as long as tool bar is not null
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolBarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener{
            // Closes current activity and takes user back to previous activity
            finish()
        }
        // Set up Database DAO
        val historyDao = (application as WorkoutApp).db.historyDao()
        // Calls dao
        addDateToDatabase(historyDao)
    }

    private fun addDateToDatabase(historyDao: HistoryDao){

        val c = Calendar.getInstance()
        val dateTime = c.time
        Log.e("Date: ", "" + dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e("Formatted Date : ", "" + date)

        lifecycleScope.launch {
            // Uses coroutine function "insert" to add date to database
            historyDao.insert(HistoryEntity(date))
            Log.e(
                "Date: ",
                "Added..."
            )

        }

    }




    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}