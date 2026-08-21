package com.desynatuser.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.desynatuser.R
import com.desynatuser.databinding.BottomSheetVoiceSearchBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VoiceSearchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetVoiceSearchBinding? = null
    private val binding get() = _binding!!

    private var speechRecognizer: SpeechRecognizer? = null
    private var onResult: ((String) -> Unit)? = null
    private var pulseAnimatorSet: AnimatorSet? = null

    companion object {
        fun newInstance(onResult: (String) -> Unit): VoiceSearchBottomSheet {
            val fragment = VoiceSearchBottomSheet()
            fragment.onResult = onResult
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Using standard bottom sheet style. Rounded corners are handled by the layout background.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetVoiceSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startListening()
        startPulseAnimation()
    }

    private fun startPulseAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.viewPulse, View.SCALE_X, 1.0f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(binding.viewPulse, View.SCALE_Y, 1.0f, 1.2f)
        val alpha = ObjectAnimator.ofFloat(binding.viewPulse, View.ALPHA, 0.5f, 0.2f)

        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.REVERSE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE
        alpha.repeatCount = ObjectAnimator.INFINITE
        alpha.repeatMode = ObjectAnimator.REVERSE

        pulseAnimatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun stopPulseAnimation() {
        pulseAnimatorSet?.cancel()
        binding.viewPulse.visibility = View.GONE
    }

    private fun startListening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                if (_binding == null) return
                binding.tvStatus.text = getString(R.string.listening)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                if (_binding == null) return
                stopPulseAnimation()
            }

            override fun onError(error: Int) {
                if (_binding == null) return
                stopPulseAnimation()
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> getString(R.string.no_match_found)
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> getString(R.string.timeout)
                    else -> getString(R.string.error_occurred)
                }
                binding.tvStatus.text = message
                view?.postDelayed({ dismiss() }, 1000)
            }

            override fun onResults(results: Bundle?) {
                if (_binding == null) return
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val result = matches[0]
                    showResult(result)
                } else {
                    dismiss()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (_binding == null) return
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    binding.tvStatus.text = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    private fun showResult(result: String) {
        binding.tvStatus.text = result
        binding.ivMic.visibility = View.GONE
        binding.ivSearch.visibility = View.VISIBLE
        stopPulseAnimation()
        
        binding.root.postDelayed({
            if (isAdded) {
                onResult?.invoke(result)
                dismiss()
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer?.destroy()
        pulseAnimatorSet?.cancel()
        _binding = null
    }
}