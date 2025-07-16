package com.example.pickfresh.Buyer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pickfresh.Model.Onewordchange
import com.example.pickfresh.databinding.ActivityRequestsFragementBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class RequestsFragement : Fragment() {
    lateinit var onewordchange: Onewordchange
    val string=ArrayList<String>()
    private lateinit var bind:ActivityRequestsFragementBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bind= ActivityRequestsFragementBinding.inflate(inflater)
        onewordchange=ViewModelProvider(requireActivity())[Onewordchange::class.java]
       string.add(bind.pending.text.toString())
       string.add(bind.accept.text.toString())
       string.add(bind.completetxt.text.toString())
        bind.request.setOnClickListener {
            startActivity(Intent(requireContext(),View_Pendings::class.java))
        }
        bind.accepted.setOnClickListener { Intent(requireContext(),View_Pendings::class.java).apply {
            putExtra("type","viewselleraccepted")
            startActivity(this)
        }
        }
        bind.completed.setOnClickListener {
            Intent(requireContext(),View_Pendings::class.java).apply {
                putExtra("type","viewcompleted")
                startActivity(this)
            }
        }
        return bind.root
    }

    override fun onStart() {
        super.onStart()
    data()
        onewordchange.observer().observe(
            requireActivity()
        ){
            if(it.size==string.size){
                bind.pending.text=it[0]
                bind.accept.text=it[1]
                bind.completetxt.text=it[2]
            }
        }
    }

    private fun data() {
       val type=requireActivity().getSharedPreferences("user",AppCompatActivity.MODE_PRIVATE).getString("language","")
        if(type=="Tamil"){
            translate(TranslateLanguage.TAMIL)
        }else if(type=="Kannada"){
            translate(TranslateLanguage.KANNADA)
        }else if(type=="Telugu"){
            translate(TranslateLanguage.TELUGU)
        }else if(type=="Hindi"){
            translate(TranslateLanguage.HINDI)
        }
    }

    private fun translate(language: String) {
val trnaslate=TranslatorOptions.Builder()
    .setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(language)
    .build()

        val download=DownloadConditions.Builder().build()
        Translation.getClient(trnaslate)
            .downloadModelIfNeeded(download)
            .addOnSuccessListener {
                onewordchange.option(trnaslate,string)
            }
    }

}