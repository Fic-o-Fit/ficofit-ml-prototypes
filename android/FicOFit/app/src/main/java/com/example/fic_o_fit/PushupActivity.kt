package com.example.fic_o_fit

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.fic_o_fit.camera.CameraSource
import com.example.fic_o_fit.data.BodyPart
import com.example.fic_o_fit.data.Pose
import com.example.fic_o_fit.models.PoseEstimator
import com.example.fic_o_fit.models.CalisthenicsClassifier
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.speech.tts.TextToSpeech

class PushupActivity : AppCompatActivity() {

    private var history = mutableListOf<Int>()
    private var num_frames_requirement: Int = 5
    private var pushup_down_done: Boolean = false
    private var pushup_count: Int = 0
    private var currentFps: Int = 20  // default to 20

    private lateinit var surfaceView: SurfaceView
    private lateinit var tvPushupCount: TextView
    private lateinit var tvFPS: TextView
    private var cameraSource: CameraSource? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            }else {
                ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                    .show(supportFragmentManager, "dialog")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pushup)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        surfaceView = findViewById(R.id.surfaceView)
        tvPushupCount = findViewById(R.id.tvPushupCount)
        tvFPS = findViewById(R.id.tvFPS)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        history = mutableListOf<Int>()
        num_frames_requirement = 5
        pushup_down_done = false
        pushup_count = 0
        currentFps = 20
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isIncreasing(arr: List<Int>): Boolean{
        var true_count = 0
        val threshold = 0.8
        for(i in 1 until arr.size){
            if(arr[i] < arr[i-1]){  // movenet y coordinate is reversed (?), the higher point will have smaller y value
                true_count += 1
            }
        }
        val true_percentage = true_count.toFloat() / (arr.size-1).toFloat()
        return true_percentage >= threshold
    }

    fun isDecreasing(arr: List<Int>): Boolean{
        var true_count = 0
        val threshold = 0.8
        for(i in 1 until arr.size){
            if(arr[i] > arr[i-1]){
                true_count += 1
            }
        }
        val true_percentage = true_count.toFloat() / (arr.size-1).toFloat()
        return true_percentage >= threshold
    }

    fun bodyIsVisible(pose: Pose): Boolean{
        val important_bodyparts = intArrayOf(5, 7, 9, 11, 13)
        for(i in important_bodyparts){
            if((pose.keypoints[i].score < 0.2f) && (pose.keypoints[i+1].score < 0.2f)){
                return false
            }
        }
        return true
    }

    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            currentFps = fps
                            tvFPS.text = getString(R.string.FPS_status, fps.toString())
                        }

                        override fun onDetectedInfo(
                            poseIsCorrect: Boolean,
                            pose: Pose
                        ) {
                            tvPushupCount.text = getString(R.string.pushup_count, pushup_count.toString())
                            // jika bener pushup maka...
                            if(poseIsCorrect && bodyIsVisible(pose)){
                                println("body is visible!! AND POSE IS CORRECT!!!")
                                var shoulder_y = 0
                                if(pose.keypoints[BodyPart.NOSE.position].coordinate.x > pose.keypoints[BodyPart.RIGHT_KNEE.position].coordinate.x){
                                    shoulder_y = (pose.keypoints[BodyPart.RIGHT_SHOULDER.position].coordinate.y).toInt()
                                }else{
                                    shoulder_y = (pose.keypoints[BodyPart.LEFT_SHOULDER.position].coordinate.y).toInt()
                                }
                                if((history.size == 0) || (shoulder_y != history.takeLast(1)[0])){
                                    num_frames_requirement = currentFps/3
                                    history.add(shoulder_y)
                                    history = history.takeLast(num_frames_requirement).toMutableList()
                                    if(history.size >= num_frames_requirement){
                                        if(isDecreasing(history)){
                                            pushup_down_done = true
                                        }else if(isIncreasing(history)){
                                            if(pushup_down_done){
                                                pushup_count += 1
                                                pushup_down_done = false
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }).apply {
                        prepareCamera()
                    }
                cameraSource?.setClassifier(CalisthenicsClassifier(this.assets, "pushup"))
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun createPoseEstimator() {
        val poseEstimator = PoseEstimator(this.assets)

        cameraSource?.setEstimator(poseEstimator)
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }
}