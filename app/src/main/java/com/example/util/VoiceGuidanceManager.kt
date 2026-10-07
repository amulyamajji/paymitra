package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class VoiceGuidanceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("VoiceGuidance", "Failed to init TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(0.95f)
        }
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isInitialized || tts == null) return

        try {
            val targetLocale = if (language == AppLanguage.TELUGU) {
                // Check if Telugu locale is supported, otherwise fallback gracefully
                val teLocale = Locale("te", "IN")
                val langAvailable = tts?.isLanguageAvailable(teLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (langAvailable >= TextToSpeech.LANG_AVAILABLE) {
                    teLocale
                } else {
                    Locale("en", "IN")
                }
            } else {
                Locale("en", "IN")
            }

            tts?.language = targetLocale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "paymitra_tts_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("VoiceGuidance", "Error during TTS speak: ${e.message}")
        }
    }

    fun speakPaymentSuccess(amount: Double, payee: String, language: AppLanguage) {
        val message = if (language == AppLanguage.TELUGU) {
            "రూపాయలు ${amount.toInt()} $payee కు విజయవంతంగా చెల్లించబడ్డాయి."
        } else {
            "Payment of rupees ${amount.toInt()} to $payee was successful."
        }
        speak(message, language)
    }

    fun speakBalance(balance: Double, language: AppLanguage) {
        val message = if (language == AppLanguage.TELUGU) {
            "మీ స్టేట్ బ్యాంక్ ఖాతాలో నిల్వ రూపాయలు ${balance.toInt()}."
        } else {
            "Your State Bank account balance is rupees ${balance.toInt()}."
        }
        speak(message, language)
    }

    fun speakSosAlert(language: AppLanguage) {
        val message = if (language == AppLanguage.TELUGU) {
            "అత్యవసర హెచ్చరిక ప్రారంభించబడింది. సహాయం కోసం కాల్ చేయబడుతోంది."
        } else {
            "Emergency SOS alert activated. Emergency services are being alerted."
        }
        speak(message, language)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
