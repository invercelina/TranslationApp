package com.example.translationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translationapp.ui.theme.TranslationAppTheme
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var text by remember { mutableStateOf("") }
            var textLength by remember { mutableStateOf(0) }
            Column {
                Spacer(modifier = Modifier.height(100.dp))
                Box(modifier = Modifier.border(1.dp, Color.Green, RoundedCornerShape(15.dp))) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(
                                text = "번역할 내용을 입력하세요",
                                color = Color.LightGray
                            )
                        },
                        placeholder = { Text(text = "입력") },
                        modifier = Modifier.size(300.dp, 270.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.size(300.dp)
                    ) {
                        Spacer(modifier = Modifier.height(268.dp))
                        Divider(color = Color.LightGray)
                        Text(
                            text = "글자 수 : $textLength",
                            modifier = Modifier.padding(top = 1.dp, end = 10.dp)
                        )
                    }

                }
                Row(Modifier.width(300.dp)) {
                    Row(horizontalArrangement = Arrangement.Start) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_volume_up_24),
                            contentDescription = "speaker",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(30.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.baseline_content_copy_24),
                            contentDescription = "copy",
                            modifier = Modifier
                                .padding(top = 8.dp, start = 10.dp)
                                .size(30.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.width(300.dp)
                    ) {
                        var isDownloaded by remember { mutableStateOf(false) }
                        val koEnTranslator = remember {
                            val options = TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.KOREAN)
                                .setTargetLanguage(TranslateLanguage.ENGLISH)
                                .build()
                            Translation.getClient(options)
                        }
                        DownloadModel(koEnTranslator, onSuccess = {
                            isDownloaded = true
                        })
                        var translatedOutput by remember { mutableStateOf("") }
                        Button(
                            onClick = {
                                textLength = text.length
                                koEnTranslator.translate(text)
                                    .addOnSuccessListener { translatedText ->
                                        // Translation successful.
                                        translatedOutput = translatedText
                                    }
                                    .addOnFailureListener { exception ->
                                        // Error.
                                        // ...
                                    }
                            },
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .padding(top = 5.dp),
                            enabled = isDownloaded
                        ) {
                            Text(text = "번역하기", fontWeight = FontWeight.Bold)
                        }
                        Text(text = translatedOutput)
                    }
                }
            }
        }
    }
    @Composable
    fun DownloadModel(koEnTranslator: Translator,
                      onSuccess: () -> Unit,
                      ) {
        LaunchedEffect(key1 = koEnTranslator) {
            var conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            koEnTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    onSuccess()
                }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        TranslationAppTheme {
            MainScreen()
        }
    }
}