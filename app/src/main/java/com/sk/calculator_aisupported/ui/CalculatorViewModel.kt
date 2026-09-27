package com.sk.calculator_aisupported.ui

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sk.calculator_aisupported.data.HistoryItem
import com.sk.calculator_aisupported.util.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale
import java.util.Random

private val Context.dataStore by preferencesDataStore(name = "calculator_preferences")

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    enum class CalculatorMode {
        BASIC, SCIENTIFIC
    }

    private val context = application.applicationContext
    private val HISTORY_KEY = stringPreferencesKey("history_tape_json")
    private val DARK_THEME_KEY = booleanPreferencesKey("is_dark_theme")
    private val HAPTIC_KEY = booleanPreferencesKey("is_haptic_enabled")
    private val SOUND_KEY = booleanPreferencesKey("is_sound_enabled")

    private val _formula = MutableStateFlow("")
    val formula: StateFlow<String> = _formula.asStateFlow()

    private val _livePreview = MutableStateFlow("0")
    val livePreview: StateFlow<String> = _livePreview.asStateFlow()

    private val _calculatorMode = MutableStateFlow(CalculatorMode.BASIC)
    val calculatorMode: StateFlow<CalculatorMode> = _calculatorMode.asStateFlow()

    private val _isRadMode = MutableStateFlow(false) // Default Degrees mode
    val isRadMode: StateFlow<Boolean> = _isRadMode.asStateFlow()

    private val _is2ndActive = MutableStateFlow(false)
    val is2ndActive: StateFlow<Boolean> = _is2ndActive.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private var toneGenerator: ToneGenerator? = null
    private var memoryValue: Double = 0.0

    private val _historyTape = MutableStateFlow<List<HistoryItem>>(emptyList())
    val historyTape: StateFlow<List<HistoryItem>> = _historyTape.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val preferences = context.dataStore.data.first()
                val jsonString = preferences[HISTORY_KEY]
                if (!jsonString.isNullOrEmpty()) {
                    _historyTape.value = Json.decodeFromString(jsonString)
                }
                _isDarkTheme.value = preferences[DARK_THEME_KEY] ?: true
                _isHapticEnabled.value = preferences[HAPTIC_KEY] ?: true
                _isSoundEnabled.value = preferences[SOUND_KEY] ?: true
            } catch (_: Exception) {
                _historyTape.value = emptyList()
            }
        }
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    private fun saveHistoryToDisk(updatedList: List<HistoryItem>) {
        viewModelScope.launch {
            try {
                val jsonString = Json.encodeToString(updatedList)
                context.dataStore.edit { preferences ->
                    preferences[HISTORY_KEY] = jsonString
                }
            } catch (_: Exception) {}
        }
    }

    fun setCalculatorMode(mode: CalculatorMode) {
        _calculatorMode.value = mode
    }

    fun toggleRadDeg() {
        _isRadMode.value = !_isRadMode.value
        updatePreview()
    }

    fun toggle2nd() {
        _is2ndActive.value = !_is2ndActive.value
    }

    fun toggleTheme() {
        val next = !_isDarkTheme.value
        _isDarkTheme.value = next
        viewModelScope.launch {
            try {
                context.dataStore.edit { preferences ->
                    preferences[DARK_THEME_KEY] = next
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleHaptic() {
        val next = !_isHapticEnabled.value
        _isHapticEnabled.value = next
        viewModelScope.launch {
            try {
                context.dataStore.edit { preferences ->
                    preferences[HAPTIC_KEY] = next
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleSound() {
        val next = !_isSoundEnabled.value
        _isSoundEnabled.value = next
        viewModelScope.launch {
            try {
                context.dataStore.edit { preferences ->
                    preferences[SOUND_KEY] = next
                }
            } catch (_: Exception) {}
        }
    }

    fun playClickTune(view: View) {
        if (!_isSoundEnabled.value) return
        try {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        } catch (_: Exception) {}
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 15)
        } catch (_: Exception) {}
    }

    fun triggerHapticFeedback(view: View) {
        playClickTune(view)
        if (!_isHapticEnabled.value) return
        try {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        } catch (_: Exception) {}
    }

    fun onDigitPressed(char: String) {
        _formula.value += char
        updatePreview()
    }

    fun onClear() {
        _formula.value = ""
        _livePreview.value = "0"
    }

    fun onToggleSign() {
        val current = _formula.value
        if (current.isEmpty() || current == "0") {
            _formula.value = "-"
            updatePreview()
            return
        }
        if (current == "-") {
            _formula.value = ""
            updatePreview()
            return
        }
        if (current.startsWith("-") && !current.substring(1).any { it in "+-×÷" }) {
            _formula.value = current.substring(1)
        } else if (!current.any { it in "+-×÷" }) {
            _formula.value = "-$current"
        } else {
            if (current.startsWith("(-") && current.endsWith(")")) {
                _formula.value = current.substring(2, current.length - 1)
            } else {
                _formula.value = "(-$current)"
            }
        }
        updatePreview()
    }

    fun onPercentage() {
        val current = _formula.value
        if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
            _formula.value = "$current%"
            updatePreview()
        }
    }

    fun onBackspace() {
        if (_formula.value.isNotEmpty()) {
            _formula.value = _formula.value.dropLast(1)
            updatePreview()
        }
    }

    fun onEvaluate() {
        val currentExpression = _formula.value
        val finalAns = ExpressionEvaluator.evaluate(currentExpression, _isRadMode.value)
        if (finalAns.isNotEmpty() && finalAns != "Error" && finalAns != currentExpression) {
            _formula.value = finalAns
            _livePreview.value = ""

            val updatedList = listOf(HistoryItem(formulaExpression = currentExpression, solvedResult = finalAns)) + _historyTape.value
            _historyTape.value = updatedList
            saveHistoryToDisk(updatedList)
        }
    }

    fun onMemoryClear() {
        memoryValue = 0.0
    }

    fun onMemoryAdd() {
        val eval = ExpressionEvaluator.evaluate(_formula.value, _isRadMode.value).toDoubleOrNull() ?: 0.0
        memoryValue += eval
    }

    fun onMemorySubtract() {
        val eval = ExpressionEvaluator.evaluate(_formula.value, _isRadMode.value).toDoubleOrNull() ?: 0.0
        memoryValue -= eval
    }

    fun onMemoryRecall() {
        val valStr = if (memoryValue % 1.0 == 0.0) memoryValue.toLong().toString() else memoryValue.toString()
        _formula.value += valStr
        updatePreview()
    }

    fun onReciprocal() {
        val current = _formula.value
        if (current.isNotEmpty()) {
            _formula.value = "1/($current)"
        } else {
            _formula.value = "1/("
        }
        updatePreview()
    }

    fun onSquare() {
        val current = _formula.value
        if (current.isNotEmpty()) {
            _formula.value = "($current)^2"
        } else {
            _formula.value = "0^2"
        }
        updatePreview()
    }

    fun onCube() {
        val current = _formula.value
        if (current.isNotEmpty()) {
            _formula.value = "($current)^3"
        } else {
            _formula.value = "0^3"
        }
        updatePreview()
    }

    fun onFactorial() {
        val current = _formula.value
        if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')' || current.last() == 'π' || current.last() == 'e')) {
            _formula.value = "$current!"
            updatePreview()
        }
    }

    fun onRandom() {
        val randVal = String.format(Locale.US, "%.4f", Random().nextDouble())
        _formula.value += randVal
        updatePreview()
    }

    fun clearHistoryTape() {
        _historyTape.value = emptyList()
        saveHistoryToDisk(emptyList())
    }

    fun onSelectHistoryItem(item: HistoryItem) {
        _formula.value = item.solvedResult
        updatePreview()
    }

    private fun updatePreview() {
        _livePreview.value = ExpressionEvaluator.evaluate(_formula.value, _isRadMode.value)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}