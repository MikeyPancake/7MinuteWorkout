package com.udemy.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy.a7minuteworkout.databinding.ActivityExerciseBinding
import com.udemy.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.Locale


class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // Object for initializing binding
    private var binding: ActivityExerciseBinding? = null

    // Rest Timer Objects
    private var restTimer: CountDownTimer? = null
    private var restTimerDuration: Long = 1

    // Variable for timer progress. As initial value the rest progress is set to 0. As we are about to start.
    private var restProgress = 0

    // Exercise timer Objects
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseTimerDuration: Long = 1

    // Exercise timer progress object. As initial value the progress is set to 0.
    private var exerciseProgress = 0

    // Variables for the Exercise Array list that is initialized in the on create method
    // and the current position is set to -1
    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    // Object for Text-to-Speech
    private var tts: TextToSpeech? = null

    // Media Player object
    private var player: MediaPlayer? = null

    // Adapter Object for Recycler View
    private var exerciseAdapter : ExerciseStatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Initializes binding
        // inflate the layout
        binding = ActivityExerciseBinding.inflate(layoutInflater)
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

        // Initializes the exercise list
        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        // Action for when user presses back button
        binding?.toolBarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        //Step 4 - Calling the function to make it visible on screen.)-->
        setupRestView()

        // Has to be called after the exercise list has been initialized
        setupExerciseStatusRecyclerView()
    }

    /**
     * Overrides what happens when back button is pressed.
     * User will be taken to main activity when back button is press
     */
    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed() (this will finish the current activity)
    }

    /**
     * Function is used to launch the custom confirmation dialog.
     * Performing the steps to show the custom dialog for back button confirmation while the exercise is going on.)
     */
    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)

        // create a binding variable
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        customDialog.setContentView(dialogBinding.root)
        // ensure that the user clicks one of the button and that the dialog is
        // not dismissed when surrounding parts of the screen is clicked
        customDialog.setCanceledOnTouchOutside(false)
        // What happens when a user presses yes or no
        dialogBinding.btnYes.setOnClickListener{
            // We need to specify that we are finishing this activity if not the player
            // continues beeping even after the screen is not visible
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener{
            // Closes the custom Dialog
            customDialog.dismiss()
        }
        // Displays custom dialog
        customDialog.show()
    }

    /**
     * Step 3 - Setting up the Get Ready View with 10 seconds of timer
     * Function is used to set the Rest view.
     */
    private fun setupRestView(){

        // Sets Frame layout for rest timer to invisible
        binding?.flRestView?.visibility =  View.VISIBLE
        // Changes text of text view to disappear
        binding?.tvTitle?.visibility = View.VISIBLE
        // Changes exercise name to visible
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        // Makes the exercise frame layout to visible "set to gone" in xml
        binding?.flExerciseView?.visibility = View.INVISIBLE
        // Makes the exercise image visible
        binding?.ivImage?.visibility = View.INVISIBLE
        // Makes the exercise text visible
        binding?.tvNextExercise?.visibility = View.VISIBLE
        // Makes the exercise text visible
        binding?.tvNextExerciseName?.visibility = View.VISIBLE


        /**
         * Here firstly we will check if the timer is running the and it is not null then
         * cancel the running timer and start the new one And set the progress to initial which is 0.
         */
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        // Sets the name of the next exercise to the text view
        binding?.tvNextExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()

        // TODO - figure out why it doesn't say the first exercise
        // Setup TTS and speak exercise name
        speakOut("The Next exercise is... " + exerciseList!![currentExercisePosition + 1].getName())

        setRestProgressBar()
    }

    /**
     * Function is used to set the Exercise view.
     */
    private fun setupExerciseView(){
        // Sets Frame layout for rest timer to invisible
        binding?.flRestView?.visibility =  View.INVISIBLE
        // Changes text of text view to disappear
        binding?.tvTitle?.visibility = View.INVISIBLE
        // Changes exercise name to visible
        binding?.tvExerciseName?.visibility = View.VISIBLE
        // Makes the exercise frame layout to visible "set to gone" in xml
        binding?.flExerciseView?.visibility = View.VISIBLE
        // Makes the exercise image visible
        binding?.ivImage?.visibility = View.VISIBLE
        // Makes the exercise text invisible
        binding?.tvNextExercise?.visibility = View.INVISIBLE
        // Makes the exercise text invisible
        binding?.tvNextExerciseName?.visibility = View.INVISIBLE

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        // Implements Media Player once exercise time starts
        try{
            val soundURI = Uri.parse(
                "android.resource://com.udemy.a7minuteworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }


        // Gets and Sets image and text for view
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        // Calls exercise progress function
        setExerciseProgressBar()
    }

    /**
     * Step 2 - Setting up the 10 seconds timer for rest view and updating it continuously
     * Function is used to set the progress of rest timer using the progress bar
     */
    private fun setRestProgressBar(){
        binding?.pbRestProgressBar?.progress = restProgress

        /**
         * @param millisInFuture The number of millis in the future from the call
         *   to {#start()} until the countdown is done and {#onFinish()}
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {#onTick(long)} callbacks.
         */
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds
        // and the countdown interval is 1 second so it 1000.
        //TODO - Change timer back to 10000 once testing is complete
        restTimer = object: CountDownTimer(restTimerDuration * 10000, 1000){

            override fun onTick(millisUntilStart: Long) {
                // Increases rest progress by 1
                restProgress++
                binding?.pbRestProgressBar?.progress = 10 - restProgress
                binding?.tvRestTimer?.text = (10 - restProgress).toString()

                // Sets the TTS to countdown the time remaining once 3 seconds are left
                val secondsRemaining = millisUntilStart / 1000
                if (secondsRemaining <= 3){
                    speakOut(secondsRemaining.toString())
                }
            }
            // Once timer is done, calls on finish function
            override fun onFinish() {
                // Increments exercise list by 1
                currentExercisePosition++

                // Sets isSelected variable to true of current position
                exerciseList!![currentExercisePosition].setIsSelected(true)
                // Notifies adapter that data has changed
                exerciseAdapter!!.notifyDataSetChanged()
                // Sets up next exercise view
                setupExerciseView()
            }
        }.start()
    }

    /**
     * Function is used to set the progress of the exercise timer using the progress bar
     */
    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        //TODO - Change timer back to 30000 once testing is complete
        exerciseTimer = object: CountDownTimer(exerciseTimerDuration * 30000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvExerciseTimer?.text = (30 - exerciseProgress).toString()

                // Sets the TTS to countdown the time remaining once 10 seconds are left
                val secondsRemaining = millisUntilFinished / 1000
                if (secondsRemaining <= 10){
                    speakOut(secondsRemaining.toString())
                }
            }
            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1){

                    /**
                     * This is used to edit variables for the recycler view
                     */
                    // Sets selected exercise variable to false
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    // Sets completed exercise variable to true
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    // Notifies Adapter that data has been changed
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
                    // Finishes current activity
                    finish()
                    // Takes user to the finish activity when all exercises are done
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onInit(status: Int) {
        // Checks if TTS status was successfully
        if(status == TextToSpeech.SUCCESS){
            // Set US English as tts language
            val result = tts?.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "the Language is not supported!")
            }
            else{
                // TTS engine initialized successfully, setup rest view
                setupRestView()
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    /**
     * Function for TTS
     */
    private fun speakOut(text : String){
        // Uses tts object and speaks it
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    /**
     * Function is used to set up the recycler view to UI and asining the Layout Manager and
     * Adapter Class is attached to it.
     *
     * Step 2: Binding adapter class to recycler view and setting the recycler view layout manager
     * and passing a list to the adapter.
     */
    private fun setupExerciseStatusRecyclerView(){
        // Defining a layout manager for the recycle view
        // Here we have used a LinearLayout Manager with horizontal scroll.
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // !! is used to force onrepit since the list is a nullable
        // As the adapter expects the exercises list and context so initialize it passing it.
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)

        // Adapter class is attached to recycler view
        // Assigns adapter to the exercise status view using binding
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    /**
     * Step 5 - Destroying the timer when closing the activity or app
     * Here in the Destroy function we will reset the rest timer, TTS, Media Player if it is running.
     */
    public override fun onDestroy(){
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        if(player != null){
            player?.stop()
        }
        binding = null
    }
}

