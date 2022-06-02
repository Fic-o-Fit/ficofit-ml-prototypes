package com.example.fic_o_fit.models

import android.content.res.AssetManager
import com.example.fic_o_fit.data.Pose
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class CalisthenicsClassifier(assetManager: AssetManager, calisthenicType: String) {
    private var INTERPRETER: Interpreter
    private var threshold: Float = 0.9f
    private var INPUT: IntArray
    private var OUTPUT: IntArray
    private var MODEL_TYPE: String

    init {
        val tfliteOptions = Interpreter.Options()
        tfliteOptions.setNumThreads(5)
        //tfliteOptions.setUseNNAPI(true)
        MODEL_TYPE = calisthenicType
        var modelPath = ""
        if(MODEL_TYPE == "pushup"){
            modelPath = "pushup-model-31may22.tflite"
        }else if(MODEL_TYPE == "situp"){
            modelPath = "situp-model-31may22.tflite"
        }else{
            modelPath = "pushup-model-31may22.tflite"
        }
        INTERPRETER = Interpreter(loadModelFile(assetManager, modelPath), tfliteOptions)
        INPUT = INTERPRETER.getInputTensor(0).shape()
        OUTPUT = INTERPRETER.getOutputTensor(0).shape()
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun poseIsCorrect(pose: Pose): Boolean {
        val inputVector = poseToArray(pose)
        val outputTensor = FloatArray(OUTPUT[1])
        INTERPRETER.run(arrayOf(inputVector), arrayOf(outputTensor))

        if(MODEL_TYPE == "pushup"){
            return (outputTensor[1] >= threshold) || (outputTensor[2] >= threshold)
        }else if(MODEL_TYPE == "situp"){
            return (outputTensor[1] >= threshold)
        }else{
            return (outputTensor[1] >= threshold) || (outputTensor[2] >= threshold)
        }

    }

    private fun poseToArray(pose: Pose) : FloatArray{
        var result = FloatArray(INPUT[1])   //51
        for (keypoint in pose.keypoints) {
            var position = keypoint.bodyPart.position * 3
            if (keypoint.score > 0.2f) {
                result[position] = keypoint.coordinate.y   //the y coordinate is the first element
                result[position+1] = keypoint.coordinate.x
                result[position+2] = keypoint.score
            }else{
                result[position] = 0.0f
                result[position+1] = 0.0f
                result[position+2] = 0.0f
            }
        }
        return result
    }

    fun close() {
        INTERPRETER.close()
    }
}