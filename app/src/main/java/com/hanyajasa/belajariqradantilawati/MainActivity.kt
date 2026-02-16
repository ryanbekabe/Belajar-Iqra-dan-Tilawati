package com.hanyajasa.belajariqradantilawati

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.util.Locale
import java.util.TreeMap

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private sealed class ModeBelajar {
        data object Huruf : ModeBelajar()
        data class Iqra(val level: Int) : ModeBelajar()
    }

    private data class HurufHijaiyah(
        val arabic: String,
        val latin: String,
    )

    private val daftarHuruf = listOf(
        HurufHijaiyah("ا", "Alif"), HurufHijaiyah("ب", "Ba"), HurufHijaiyah("ت", "Ta"),
        HurufHijaiyah("ث", "Tsa"), HurufHijaiyah("ج", "Jim"), HurufHijaiyah("ح", "Ha"),
        HurufHijaiyah("خ", "Kha"), HurufHijaiyah("د", "Dal"), HurufHijaiyah("ذ", "Dzal"),
        HurufHijaiyah("ر", "Ra"), HurufHijaiyah("ز", "Zai"), HurufHijaiyah("س", "Sin"),
        HurufHijaiyah("ش", "Syin"), HurufHijaiyah("ص", "Shad"), HurufHijaiyah("ض", "Dhad"),
        HurufHijaiyah("ط", "Tha"), HurufHijaiyah("ظ", "Zha"), HurufHijaiyah("ع", "Ain"),
        HurufHijaiyah("غ", "Ghain"), HurufHijaiyah("ف", "Fa"), HurufHijaiyah("ق", "Qaf"),
        HurufHijaiyah("ك", "Kaf"), HurufHijaiyah("ل", "Lam"), HurufHijaiyah("م", "Mim"),
        HurufHijaiyah("ن", "Nun"), HurufHijaiyah("و", "Wau"), HurufHijaiyah("ه", "Ha"),
        HurufHijaiyah("لا", "Lam Alif"), HurufHijaiyah("ء", "Hamzah"), HurufHijaiyah("ي", "Ya"),
    )

    private lateinit var tvHuruf: TextView
    private lateinit var tvLatin: TextView
    private lateinit var tvCounter: TextView
    private lateinit var layoutHuruf: LinearLayout
    private lateinit var ivIqra: ImageView
    private lateinit var modeContainer: LinearLayout
    private lateinit var btnPrev: MaterialButton
    private lateinit var btnNext: MaterialButton
    private lateinit var btnListen: MaterialButton

    private var currentIndexHuruf = 0
    private val currentIqraPageIndex = mutableMapOf<Int, Int>()
    private var modeAktif: ModeBelajar = ModeBelajar.Huruf

    private val iqraPagesByLevel: Map<Int, List<Int>> by lazy { loadAllIqraPages() }
    private val modeButtons = mutableMapOf<ModeBelajar, MaterialButton>()

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
        layoutHuruf = findViewById(R.id.layoutHuruf)
        ivIqra = findViewById(R.id.ivIqra)
        modeContainer = findViewById(R.id.modeContainer)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnListen = findViewById(R.id.btnListen)

        setupDynamicModeButtons()

        btnPrev.setOnClickListener {
            when (val mode = modeAktif) {
                is ModeBelajar.Huruf -> if (currentIndexHuruf > 0) currentIndexHuruf--
                is ModeBelajar.Iqra -> {
                    val current = currentIqraPageIndex[mode.level] ?: 0
                    if (current > 0) currentIqraPageIndex[mode.level] = current - 1
                }
            }
            renderModeAktif()
        }

        btnNext.setOnClickListener {
            when (val mode = modeAktif) {
                is ModeBelajar.Huruf -> if (currentIndexHuruf < daftarHuruf.lastIndex) currentIndexHuruf++
                is ModeBelajar.Iqra -> {
                    val pages = iqraPagesByLevel[mode.level].orEmpty()
                    val current = currentIqraPageIndex[mode.level] ?: 0
                    if (current < pages.lastIndex) currentIqraPageIndex[mode.level] = current + 1
                }
            }
            renderModeAktif()
        }

        btnListen.setOnClickListener { speakCurrentHuruf() }

        tts = TextToSpeech(this, this)
        renderModeAktif()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsLangResult = tts?.setLanguage(Locale.Builder().setLanguage("ar").build())
            isTtsReady = ttsLangResult != TextToSpeech.LANG_MISSING_DATA &&
                ttsLangResult != TextToSpeech.LANG_NOT_SUPPORTED
        } else {
            isTtsReady = false
        }
        renderModeAktif()
    }

    private fun setupDynamicModeButtons() {
        modeContainer.removeAllViews()
        modeButtons.clear()

        addModeButton(ModeBelajar.Huruf, getString(R.string.mode_huruf))

        iqraPagesByLevel.keys.sorted().forEach { level ->
            currentIqraPageIndex.putIfAbsent(level, 0)
            addModeButton(ModeBelajar.Iqra(level), getString(R.string.mode_iqra_format, level))
        }
    }

    private fun addModeButton(mode: ModeBelajar, label: String) {
        val button = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        params.marginEnd = (8 * resources.displayMetrics.density).toInt()

        button.layoutParams = params
        button.text = label
        button.setOnClickListener {
            modeAktif = mode
            renderModeAktif()
        }

        modeContainer.addView(button)
        modeButtons[mode] = button
    }

    private fun loadAllIqraPages(): Map<Int, List<Int>> {
        val pattern = Regex("^iqra(\\d+)_halaman_(\\d+)$")
        val grouped = TreeMap<Int, MutableList<Pair<Int, Int>>>()

        R.drawable::class.java.fields.forEach { field ->
            val match = pattern.matchEntire(field.name) ?: return@forEach
            val level = match.groupValues[1].toIntOrNull() ?: return@forEach
            val page = match.groupValues[2].toIntOrNull() ?: return@forEach
            val drawableId = field.getInt(null)
            grouped.getOrPut(level) { mutableListOf() }.add(page to drawableId)
        }

        val result = grouped.mapValues { (_, pairs) ->
            pairs.sortedBy { it.first }.map { it.second }
        }.toMutableMap()

        if (result.isEmpty()) {
            result[1] = listOf(R.drawable.iqra1_halaman_1)
        }

        return result
    }

    private fun speakCurrentHuruf() {
        if (!isTtsReady || modeAktif !is ModeBelajar.Huruf) return
        val huruf = daftarHuruf[currentIndexHuruf]
        tts?.speak(huruf.arabic, TextToSpeech.QUEUE_FLUSH, null, "huruf_$currentIndexHuruf")
    }

    private fun renderModeAktif() {
        when (val mode = modeAktif) {
            is ModeBelajar.Huruf -> renderModeHuruf()
            is ModeBelajar.Iqra -> {
                val pages = iqraPagesByLevel[mode.level].orEmpty()
                val current = (currentIqraPageIndex[mode.level] ?: 0).coerceIn(0, pages.lastIndex.coerceAtLeast(0))
                currentIqraPageIndex[mode.level] = current
                renderModeIqra(pages, current)
            }
        }

        modeButtons.forEach { (mode, button) ->
            button.isEnabled = mode != modeAktif
        }
    }

    private fun renderModeHuruf() {
        val huruf = daftarHuruf[currentIndexHuruf]
        layoutHuruf.visibility = View.VISIBLE
        ivIqra.visibility = View.GONE
        btnListen.visibility = View.VISIBLE
        btnListen.isEnabled = isTtsReady

        tvHuruf.text = huruf.arabic
        tvLatin.text = huruf.latin
        tvCounter.text = getString(R.string.label_huruf_ke, currentIndexHuruf + 1, daftarHuruf.size)
        btnPrev.isEnabled = currentIndexHuruf > 0
        btnNext.isEnabled = currentIndexHuruf < daftarHuruf.lastIndex
    }

    private fun renderModeIqra(pages: List<Int>, currentIndex: Int) {
        layoutHuruf.visibility = View.GONE
        ivIqra.visibility = View.VISIBLE
        btnListen.visibility = View.GONE

        val safePages = if (pages.isNotEmpty()) pages else listOf(R.drawable.iqra1_halaman_1)
        val safeIndex = currentIndex.coerceIn(0, safePages.lastIndex)

        ivIqra.setImageResource(safePages[safeIndex])
        tvCounter.text = getString(R.string.label_halaman_ke, safeIndex + 1, safePages.size)
        btnPrev.isEnabled = safeIndex > 0
        btnNext.isEnabled = safeIndex < safePages.lastIndex
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
