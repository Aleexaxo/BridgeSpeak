package com.example.bridgespeak

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageTranslationScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var detectedSign by remember { mutableStateOf<String?>(null) }
    var confidence by remember { mutableStateOf<Float?>(null) }

    // History list (in-memory). Move to ViewModel/Room if you want persistence.
    val historyList = remember { mutableStateListOf<HistoryItem>() }

    // 🔊 Text-to-Speech setup
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val MODEL_INPUT_SIZE = 64 // matches your CNN input size

    // 📂 Gallery launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let { u ->
            scope.launch {
                // load bitmap on IO
                val bitmap = withContext(Dispatchers.IO) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, u)
                }

                // run inference
                val (sign, conf) = withContext(Dispatchers.Default) {
                    runModelOnImage(context, bitmap)
                }

                // update UI
                detectedSign = sign
                confidence = conf

                // add to history (newest first)
                val currentDateTime = Calendar.getInstance()
                val date = android.text.format.DateFormat.format("MMMM dd, yyyy", currentDateTime).toString()
                val time = android.text.format.DateFormat.format("hh:mm a", currentDateTime).toString()

                historyList.add(
                    0,
                    HistoryItem(
                        translation = "Letter $sign",
                        date = date,
                        time = time
                    )
                )
            }
        }
    }

    // 📷 Camera launcher - uses TakePicturePreview which returns a Bitmap thumbnail
    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // convert the Bitmap to a Uri so Compose can display it via rememberAsyncImagePainter
            val inserted: String? = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                it,
                "captured_image",
                null
            )
            val tempUri: Uri? = if (inserted != null) Uri.parse(inserted) else null

            if (tempUri != null) {
                imageUri = tempUri
            }

            // run inference
            scope.launch {
                val (sign, conf) = withContext(Dispatchers.Default) {
                    runModelOnImage(context, it)
                }
                detectedSign = sign
                confidence = conf

                // add to history
                val currentDateTime = Calendar.getInstance()
                val date = android.text.format.DateFormat.format("MMMM dd, yyyy", currentDateTime).toString()
                val time = android.text.format.DateFormat.format("hh:mm a", currentDateTime).toString()

                historyList.add(
                    0,
                    HistoryItem(
                        translation = "Letter $sign",
                        date = date,
                        time = time
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Image Translation",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFEFF3F9)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 🖼️ Image preview
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(220.dp)
                    .border(BorderStroke(2.dp, Color(0xFF003366)), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Uploaded image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_upload),
                            contentDescription = null,
                            tint = Color(0xFF003366),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Upload or capture an image of sign language.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🧠 Translation result
            if (detectedSign != null && confidence != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Translation Result", fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Detected sign: Letter \"$detectedSign\"")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (confidence ?: 0f) / 100f,
                            color = Color(0xFF003366),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Confidence: ${confidence?.toInt() ?: 0}%")
                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔊 Play Audio button
                        Button(
                            onClick = {
                                tts?.speak(
                                    "The letter is $detectedSign",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "bridge_speak_utterance"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_audio),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Play Audio", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 📷 Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { takePhotoLauncher.launch(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo", color = Color.White)
                }

                OutlinedButton(
                    onClick = { pickImageLauncher.launch("image/*") },
                    border = BorderStroke(1.dp, Color(0xFF003366)),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload),
                        contentDescription = null,
                        tint = Color(0xFF003366),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Image", color = Color(0xFF003366))
                }
            }
        }
    }
}

/** 🔍 Run model.tflite on bitmap and return (label, confidence) */
private fun runModelOnImage(context: Context, bitmap: Bitmap): Pair<String, Float> {
    val inputSize = 64 // model input (from Colab)
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

    // Load model (memory-mapped)
    val modelBuffer = loadModelFile(context) // ensure this helper exists and returns MappedByteBuffer
    val interpreter = Interpreter(modelBuffer)

    // Prepare input buffer (FLOAT32 RGB normalized 0..1)
    val numChannels = 3
    val inputBuffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * numChannels * 4)
    inputBuffer.order(ByteOrder.nativeOrder())
    inputBuffer.rewind()

    val intValues = IntArray(inputSize * inputSize)
    resizedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
    for (pixel in intValues) {
        val r = ((pixel shr 16) and 0xFF) / 255.0f
        val g = ((pixel shr 8) and 0xFF) / 255.0f
        val b = (pixel and 0xFF) / 255.0f
        inputBuffer.putFloat(r)
        inputBuffer.putFloat(g)
        inputBuffer.putFloat(b)
    }
    inputBuffer.rewind()

    // Prepare output buffer for 29 classes (matches your model)
    val numClasses = 29
    val output = Array(1) { FloatArray(numClasses) }

    // Run inference
    interpreter.run(inputBuffer, output)
    interpreter.close()

    // Labels mapping (A-Z + extras)
    val labels = listOf(
        "A","B","C","D","E","F","G","H","I","J","K","L","M",
        "N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
        "Nothing","Space","Delete"
    )

    // Pick best class
    val probs = output[0]
    val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: -1
    val confidence = if (maxIndex >= 0) probs[maxIndex] * 100f else 0f
    val label = if (maxIndex >= 0 && maxIndex < labels.size) labels[maxIndex] else "Unknown"

    return Pair(label, confidence)
}

/** ✅ Helper function: properly load model.tflite from assets using FileChannel */
private fun loadModelFile(context: Context): ByteBuffer {
    val fileDescriptor = context.assets.openFd("model.tflite")
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength

    Log.d("ModelCheck", "Loading model.tflite (length=$declaredLength bytes)")

    val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    inputStream.close()
    fileDescriptor.close()
    return modelBuffer
}
