package com.example.pickfresh.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import retrofit2.http.OPTIONS


class Onewordchange : androidx.lifecycle.ViewModel() {
    private var string = MutableLiveData<ArrayList<String>>()
    private var message = MutableLiveData<String>()
    lateinit var translation: TranslatorOptions

    fun option(Translation: TranslatorOptions, dummy: ArrayList<String>) {
        translation = Translation
        val newarray = ArrayList<String>()
        val download = DownloadConditions.Builder()
            .build()
        val translation = com.google.mlkit.nl.translate.Translation.getClient(Translation)
        translation.downloadModelIfNeeded(download)
            .addOnSuccessListener {

                dummy.forEach { the ->
                    translation.translate(the).addOnSuccessListener {
                        newarray.add(it)
                        string.value = newarray
                    }
                }
            }
    }

    fun observer(): LiveData<ArrayList<String>> {
        return string
    }

    fun string(string: String) {
        Translation.getClient(translation)
            .translate(string)
            .addOnSuccessListener {
                if (it != null) {
                    message.value = it
                }
            }
            .addOnFailureListener {
                message.value = it.message
            }
    }

    fun toast() = message


}