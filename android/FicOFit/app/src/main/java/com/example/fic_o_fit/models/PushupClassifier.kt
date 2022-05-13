package com.example.fic_o_fit.models

import android.content.res.AssetManager
import com.example.fic_o_fit.data.Classification
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class PushupClassifier(assetManager: AssetManager) {
    private var INTERPRETER: Interpreter

    init {
        val tfliteOptions = Interpreter.Options()
        tfliteOptions.setNumThreads(5)
        //tfliteOptions.setUseNNAPI(true)
        INTERPRETER = Interpreter(loadModelFile(assetManager, "pushup_classifier.tflite"),tfliteOptions)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyPose(inputArr: FloatArray): Classification {
        val shape = intArrayOf(1,51)
        var input = TensorBuffer.createFixedSize(shape, DataType.FLOAT32)
        input.loadArray(inputArr, shape)
        //var result = Array(1) { ByteArray(2) }
        val outputShape: IntArray = INTERPRETER.getOutputTensor(0).shape()
        val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
        INTERPRETER.run(input.buffer, outputTensor.buffer.rewind())
        val output = outputTensor.floatArray
        return getSortedResult(output)
    }

    private fun getSortedResult(labelProbArray: FloatArray): Classification {
        lateinit var result: Classification
        if (labelProbArray[0] > labelProbArray[1]){
            val label = 0
            result = Classification("" + label, "Push Up Down", labelProbArray[0].toFloat())
        }else if (labelProbArray[0] < labelProbArray[1]){
            val label = 1
            result = Classification("" + label, "Push Up Up", labelProbArray[1].toFloat())
        }else{
            result = Classification()
        }
        return result
    }

    fun close() {
        INTERPRETER.close()
    }
}