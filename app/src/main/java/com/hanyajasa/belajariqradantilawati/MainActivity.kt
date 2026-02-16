package com.hanyajasa.belajariqradantilawati

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private data class HurufHijaiyah(
        val arabic: String,
        val latin: String,
    )

    private val daftarHuruf = listOf(
        HurufHijaiyah("ا", "Alif"),
        HurufHijaiyah("ب", "Ba"),
        HurufHijaiyah("ت", "Ta"),
        HurufHijaiyah("ث", "Tsa"),
        HurufHijaiyah("ج", "Jim"),
        HurufHijaiyah("ح", "Ha"),
        HurufHijaiyah("خ", "Kha"),
        HurufHijaiyah("د", "Dal"),
        HurufHijaiyah("ذ", "Dzal"),
        HurufHijaiyah("ر", "Ra"),
        HurufHijaiyah("ز", "Zai"),
        HurufHijaiyah("س", "Sin"),
        HurufHijaiyah("ش", "Syin"),
        HurufHijaiyah("ص", "Shad"),
        HurufHijaiyah("ض", "Dhad"),
        HurufHijaiyah("ط", "Tha"),
        HurufHijaiyah("ظ", "Zha"),
        HurufHijaiyah("ع", "Ain"),
        HurufHijaiyah("غ", "Ghain"),
        HurufHijaiyah("ف", "Fa"),
        HurufHijaiyah("ق", "Qaf"),
        HurufHijaiyah("ك", "Kaf"),
        HurufHijaiyah("ل", "Lam"),
        HurufHijaiyah("م", "Mim"),
        HurufHijaiyah("ن", "Nun"),
        HurufHijaiyah("و", "Wau"),
        HurufHijaiyah("ه", "Ha"),
        HurufHijaiyah("لا", "Lam Alif"),
        HurufHijaiyah("ء", "Hamzah"),
        HurufHijaiyah("ي", "Ya"),
    )

    private lateinit var tvHuruf: TextView
    private lateinit var tvLatin: TextView
    private lateinit var tvCounter: TextView
    private lateinit var btnPrev: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var btnListen: MaterialButton

    private var currentIndex = 0
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvHuruf = findViewById(R.id.tvHuruf)
        tvLatin = findViewById(R.id.tvLatin)
        tvCounter = findViewById(R.id.tvCounter)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnListen = findViewById(R.id.btnListen)

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                renderHuruf()
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex < daftarHuruf.lastIndex) {
                currentIndex++
                renderHuruf()
            }
        }

        btnListen.setOnClickListener {
            speakCurrentHuruf()
        }

        tts = TextToSpeech(this, this)
        renderHuruf()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsLangResult = tts?.setLanguage(Locale("ar"))
            isTtsReady = ttsLangResult != TextToSpeech.LANG_MISSING_DATA &&
                ttsLangResult != TextToSpeech.LANG_NOT_SUPPORTED
            btnListen.isEnabled = isTtsReady
        } else {
            isTtsReady = false
            btnListen.isEnabled = false
        }
    }

    private fun speakCurrentHuruf() {
        if (!isTtsReady) return
        val huruf = daftarHuruf[currentIndex]
        tts?.speak(huruf.arabic, TextToSpeech.QUEUE_FLUSH, null, "huruf_${currentIndex}")
    }

    private fun renderHuruf() {
        val huruf = daftarHuruf[currentIndex]
        tvHuruf.text = huruf.arabic
        tvLatin.text = huruf.latin
        tvCounter.text = getString(R.string.label_huruf_ke, currentIndex + 1, daftarHuruf.size)

        btnPrev.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < daftarHuruf.lastIndex
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
