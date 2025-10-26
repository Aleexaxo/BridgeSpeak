package com.example.bridgespeak

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.bridgespeak.ui.theme.BridgeSpeakTheme
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var tflite: Interpreter
    private val executor = Executors.newSingleThreadExecutor()

    private val classLabels = arrayOf(
        "A","B","C","D","E","F","G","H","I","J","K","L","M",
        "N","O","P","Q","R","S","T","U","V","W","X","Y","Z","DEL","SPACE","NOTHING"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeSpeakTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SignUpScreen()
                }
            }
        }

        // Load TFLite model
        tflite = Interpreter(loadModelFile())

        // Request camera permission
        val cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            BridgeSpeakTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BridgeSpeakUI(tflite, classLabels)
                }
            }
        }
    }

    // Load model from assets
    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("asl_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}

@Composable
fun BridgeSpeakUI(tflite: Interpreter, classLabels: Array<String>) {
    val context = LocalContext.current
    var predictedLetter by remember { mutableStateOf("...") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analyzer ->
                        analyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            // Use PreviewView bitmap for simplicity (works API 24+)
                            val bitmap: Bitmap? = previewView.bitmap
                            if (bitmap != null) {
                                val input = preprocessBitmap(bitmap)
                                val output = Array(1) { FloatArray(classLabels.size) }
                                tflite.run(input, output)
                                val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
                                predictedLetter = classLabels[maxIndex]
                            }
                            imageProxy.close()
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        context as ComponentActivity,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        })

        // Predicted letter overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Predicted Letter:", style = MaterialTheme.typography.headlineSmall)
            Text(text = predictedLetter, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

// Preprocess Bitmap for TFLite input
fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
    val inputSize = 64
    val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    val byteBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
    byteBuffer.order(ByteOrder.nativeOrder())

    for (y in 0 until inputSize) {
        for (x in 0 until inputSize) {
            val pixel = resized.getPixel(x, y)
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 255f))
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 255f))
            byteBuffer.putFloat(((pixel and 0xFF) / 255f))
        }
    }
    return byteBuffer
}

