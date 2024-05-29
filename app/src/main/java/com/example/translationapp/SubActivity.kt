package com.example.translationapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.translationapp.ui.theme.TranslationAppTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class SubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslationAppTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val context = LocalContext.current
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            var outputText by remember {
                mutableStateOf("")
            }
            val pickMedia =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
                    imageUri = uri
                    val image: InputImage
                    try {
                        image = InputImage.fromFilePath(context, imageUri!!)
                        val result = recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                // Task completed successfully
                                outputText = visionText.text
                            }
                            .addOnFailureListener { e ->
                                // Task failed with an exception
                                // ...
                            }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }


            Button(onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

            }) {
                Text(text = "인식")
            }
            Image(
                painter = rememberAsyncImagePainter(model = imageUri), contentDescription = null,
                modifier = Modifier.size(300.dp)
            )
            if (outputText.isNotEmpty()) {
                Text(text = outputText)
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun MainScreenPreview() {
        TranslationAppTheme {
            MainScreen()
        }
    }
}
