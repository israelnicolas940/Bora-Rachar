package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtJogadores: EditText
    private lateinit var tvDynamicText: TextView
    private var ttsSucess: Boolean = false;
    private var isUpdating: Boolean = false
    private lateinit var defaultTextValue: String
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        defaultTextValue = getString(R.string.default_value)

        edtConta = findViewById<EditText>(R.id.edtConta)
        edtConta.addTextChangedListener(this)

        edtJogadores = findViewById<EditText>(R.id.edtJogadores)
        edtJogadores.addTextChangedListener(this)

        tvDynamicText = findViewById<TextView>(R.id.tvDynamicText)
        updateTextView(defaultTextValue)
        // Initialize TTS engine
        tts = TextToSpeech(this, this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
       //Log.d("PDM24","Antes de mudar")

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //Log.d("PDM24","Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        isUpdating = true
        val inputConta = edtConta.text.toString()
        val inputJogadores = edtJogadores.text.toString()
        if (inputConta.isNotEmpty() && inputJogadores.isNotEmpty()) {
            val valorConta = inputConta.toDoubleOrNull()
            val valorJogadores = inputJogadores.toDoubleOrNull()

            if (valorConta != null && valorJogadores != null) {
                if (valorJogadores != 0.0) {
                    val value = valorConta / valorJogadores
                    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
                    numberFormat.minimumFractionDigits = 2
                    numberFormat.maximumFractionDigits = 2
                    val formattedValue = numberFormat.format(value)
                    updateTextView(formattedValue)
                } else {
                    updateTextView(defaultTextValue)
                }
            } else {
                updateTextView(defaultTextValue)
            }
        } else {
            updateTextView(defaultTextValue)
        }
        isUpdating = false
    }


    fun clickFalar(v: View) {
        if (tts.isSpeaking) {
            tts.stop()
        }
        if (ttsSucess) {
            Log.d("PDM23", tts.language.toString())
            tts.speak(tvDynamicText.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }

    }

    fun clickShare(v: View) {

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, tvDynamicText.text.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun updateTextView(text: String) {
        tvDynamicText.text = text
    }

    override fun onDestroy() {
            // Release TTS engine resources
            tts.stop()
            tts.shutdown()
            super.onDestroy()
        }

    override fun onInit(status: Int) {
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
                tts.language = Locale.getDefault()
                ttsSucess=true
                Log.d("PDM23","Sucesso na Inicialização")
            } else {
                // TTS engine failed to initialize
                Log.e("PDM23", "Failed to initialize TTS engine.")
                ttsSucess=false
            }
        }


}

