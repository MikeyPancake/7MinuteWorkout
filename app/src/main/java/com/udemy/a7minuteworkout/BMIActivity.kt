package com.udemy.a7minuteworkout

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.udemy.a7minuteworkout.databinding.ActivityBmiBinding
import com.udemy.a7minuteworkout.databinding.ActivityMainBinding
import com.udemy.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var binding: ActivityBmiBinding? = null
    private var currentVisibleView : String = US_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBmiBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        //Create Tool Bar
        // then set support action bar and get toolBar Exerciser using the binding variable
        setSupportActionBar(binding?.toolBarBmiActivity)

        // Add back button to tool bar as long as tool bar is not null
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "BMI Calculator"
        }

        // Action for when user presses back button
        binding?.toolBarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        // Sets US Unit View as default
        makeVisibleUSUnitsView()

        // On check changed listener that changes view based on if US or Metric Units are checked
        binding?.rgUnits?.setOnCheckedChangeListener{ _, checkedID: Int ->

            if (checkedID == R.id.rbUSUnits){
                makeVisibleUSUnitsView()
            }
            else{
                makeVisibleMetricUnitsView()
            }
        }

        // On click listener that conducts calculation once user inputs all inputs and clicks calculate
        binding?.btnCalculate?.setOnClickListener {
            calculateBMI()
        }
    }

    /**
     * Function that conducts calculation based on US or Metric Unit View
     */
    private fun calculateBMI(){
        // If US Units is the current view then use the US Unit calculation function
        if (currentVisibleView == US_UNITS_VIEW){
            displayBMIResultUS()
        }
        else{
            displayBMIResultMetric()
        }
    }

    /**
     *  Function that switches view to US Units when a user clicks US UNITS
     */
    private fun makeVisibleUSUnitsView(){

        currentVisibleView = US_UNITS_VIEW
        // US Units are visible
        binding?.tilUnitHeight?.visibility = View.VISIBLE
        binding?.tilUnitWeight?.visibility = View.VISIBLE
        // Metric Units are invisible
        binding?.tilUnitHeightMetric?.visibility = View.GONE
        binding?.tilUnitWeightMetric?.visibility = View.GONE
        // Clears text field when switching between US and Metric
        binding?.etUnitHeight?.text!!.clear()
        binding?.etUnitWeight?.text!!.clear()
        // Sets BMI results to invisible when switching to a different unit
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    /**
     *  Function that switches view to Metric Units when a user clicks Metric UNITS
     */
    private fun makeVisibleMetricUnitsView(){

        currentVisibleView = METRIC_UNITS_VIEW
        // METRIC Units are visible
        binding?.tilUnitHeightMetric?.visibility = View.VISIBLE
        binding?.tilUnitWeightMetric?.visibility = View.VISIBLE
        // US Units are invisible
        binding?.tilUnitHeight?.visibility = View.INVISIBLE
        binding?.tilUnitWeight?.visibility = View.INVISIBLE
        // Clears text field when switching between US and Metric
        binding?.etUnitHeightMetric?.text!!.clear()
        binding?.etUnitWeightMetric?.text!!.clear()
        // Sets BMI results to invisible when switching to a different unit
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    /**
     *  Checks if user has inputted both weight and height values for US Units
     */
    private fun validateUSUnits() : Boolean{

        var isValid = true

        if (binding?.etUnitWeight?.text.toString().isEmpty()){
            isValid = false

        }else if(binding?.etUnitHeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    /**
     *  Function that calculates BMI based on users inputted values (US Standard)
     */
    private fun displayBMIResultUS(){

        /**
         * If validateUnits is true then conduct calculation....
         */
        if (validateUSUnits()){
            // BMI Calculation formula
            val heightValue : Float = binding?.etUnitHeight?.text.toString().toFloat()
            val weightValue: Float = binding?.etUnitWeight?.text.toString().toFloat()
            val bmi: Float = (weightValue * 703) / (heightValue * heightValue)

            // Text View Values
            val bmiLabel : String
            val bmiDescription : String

            // takes bmi number and converts it to a double, the defines the output to 2 numbers that are rounded
            val bmiValue = String.format("%.2f", bmi)

            // BMI results using LBS
            if (bmi.compareTo(18.4f) <= 0){
                bmiLabel = "Underweight"
                bmiDescription = "You should eat and workout more!"
            }
            else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(24.99f) <= 0){
                bmiLabel = "Optimum Range"
                bmiDescription = "You are doing it right! Keep it Up!"
            }
            else if (bmi.compareTo(25.0f) > 0 && bmi.compareTo(29.99f) <= 0){
                bmiLabel = "Overweight"
                bmiDescription = "You need to eat better and workout more!"
            }
            else if (bmi.compareTo(30.0f) > 0 && bmi.compareTo(34.99f) <= 0){
                bmiLabel = "Class I Obesity (Moderately Obese)"
                bmiDescription = "You really need to think about your health!"
            }
            else if (bmi.compareTo(35.0f) > 0 && bmi.compareTo(39.99f) <= 0){
                bmiLabel = "Class II Obesity (Severely Obese)"
                bmiDescription = "You really need to think about your health and act now!"
            }
            else {
                bmiLabel = "Class III Obesity (Very Severely Obese)"
                bmiDescription = "You really need to think about your health and act now!"
            }
            // Makes linear layout visible
            binding?.llDisplayBMIResult?.visibility = View.VISIBLE
            // Sets text value based on results
            binding?.tvBMIValue?.text = bmiValue
            binding?.tvBMIType?.text = bmiLabel
            binding?.tvBMIMessage?.text = bmiDescription

        }else{
            // If missing user inputs, give a toast
            Toast.makeText(
                this@BMIActivity,
                "Please enter values",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     *  Checks if user has inputted both weight and height values for US Units
     */
    private fun validateMetricUnits() : Boolean{

        var isValid = true

        if (binding?.etUnitWeightMetric?.text.toString().isEmpty()){
            isValid = false

        }else if(binding?.etUnitHeightMetric?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }

    /**
     *  Function that calculates BMI based on users inputted values (US Standard)
     */
    private fun displayBMIResultMetric(){

        /**
         * If validateUnits is true then conduct calculation....
         */
        if (validateMetricUnits()){
            // BMI Calculation formula
            val heightValue : Float = binding?.etUnitHeightMetric?.text.toString().toFloat() / 100
            val weightValue: Float = binding?.etUnitWeightMetric?.text.toString().toFloat()
            val bmi: Float = (weightValue / (heightValue * heightValue))

            // Text View Values
            val bmiLabel : String
            val bmiDescription : String

            // takes bmi number and converts it to a double, the defines the output to 2 numbers that are rounded
            val bmiValue = String.format("%.2f", bmi)

            // BMI results using LBS
            if (bmi.compareTo(18.4f) <= 0){
                bmiLabel = "Underweight"
                bmiDescription = "You should eat and workout more!"
            }
            else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(24.99f) <= 0){
                bmiLabel = "Optimum Range"
                bmiDescription = "You are doing it right! Keep it Up!"
            }
            else if (bmi.compareTo(25.0f) > 0 && bmi.compareTo(29.99f) <= 0){
                bmiLabel = "Overweight"
                bmiDescription = "You need to eat better and workout more!"
            }
            else if (bmi.compareTo(30.0f) > 0 && bmi.compareTo(34.99f) <= 0){
                bmiLabel = "Class I Obesity (Moderately Obese)"
                bmiDescription = "You really need to think about your health!"
            }
            else if (bmi.compareTo(35.0f) > 0 && bmi.compareTo(39.99f) <= 0){
                bmiLabel = "Class II Obesity (Severely Obese)"
                bmiDescription = "You really need to think about your health and act now!"
            }
            else {
                bmiLabel = "Class III Obesity (Very Severely Obese)"
                bmiDescription = "You really need to think about your health and act now!"
            }
            // Makes linear layout visible
            binding?.llDisplayBMIResult?.visibility = View.VISIBLE
            // Sets text value based on results
            binding?.tvBMIValue?.text = bmiValue
            binding?.tvBMIType?.text = bmiLabel
            binding?.tvBMIMessage?.text = bmiDescription

        }else{
            // If missing user inputs, give a toast
            Toast.makeText(
                this@BMIActivity,
                "Please enter values",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}