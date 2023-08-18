package com.udemy.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy.a7minuteworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding : ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Create Tool Bar
        // then set support action bar and get toolBar Exerciser using the binding variable
        setSupportActionBar(binding?.toolBarHistoryActivity)


        // Add back button to tool bar as long as tool bar is not null
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History"
        }

        // Action for when user presses back button
        binding?.toolBarHistoryActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set up Database DAO
        val historyDao = (application as WorkoutApp).db.historyDao()
        getAllCompletedDates(historyDao)
    }

    private fun getAllCompletedDates(historyDao: HistoryDao){

        // Use coroutine to retrieve data
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect{getAllCompletedDatesList ->
                if (getAllCompletedDatesList.isNotEmpty()){
                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.INVISIBLE

                    // Setup Recycler view
                    binding?.rvHistory?.layoutManager = LinearLayoutManager(
                        this@HistoryActivity)

                    val dates = ArrayList<String>()
                    for (date in getAllCompletedDatesList){
                        dates.add(date.date)
                    }
                    val historyAdapter = HistoryAdapter(dates)
                    // Assign history adapter to RV adapter property
                    binding?.rvHistory?.adapter = historyAdapter

                }
                else{
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}