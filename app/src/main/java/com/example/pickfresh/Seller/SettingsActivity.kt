package com.example.pickfresh.Seller

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pickfresh.Buyer.ProflieActivity
import com.example.pickfresh.MainActivity
import com.example.pickfresh.R
import com.example.pickfresh.databinding.ActivitySettingsBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.lang.StringBuilder

class SettingsActivity : AppCompatActivity() {
    private lateinit var bind: ActivitySettingsBinding
    lateinit var tamil: RadioButton
    lateinit var telugu: RadioButton
    lateinit var kannada: RadioButton
    lateinit var english: RadioButton
    lateinit var hindi: RadioButton
    val string = StringBuilder()
    lateinit var progress: Dialog
    var title = ""
    var message = ""
    var yes = ""
    var no = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val dialog = Dialog(this).apply {
            setContentView(R.layout.language)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            tamil = this.findViewById(R.id.tamil)
            telugu = this.findViewById(R.id.telugu)
            kannada = this.findViewById(R.id.kannada)
            english = this.findViewById(R.id.english)
            hindi = this.findViewById(R.id.hindi)
        }
        bind.settings.setOnClickListener {
            startActivity(Intent(this, ProflieActivity::class.java))
        }


        progress = Dialog(this).apply {
            setContentView(R.layout.progressdi)
            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        bind.logout2.setOnClickListener { logdialog() }

        string.append(bind.profiletext.text.toString() + ",")
        string.append(bind.languagetxt.text.toString() + ",")
        string.append(
            bind.logouttxt.text.toString() + ","
                    + "Do you want Logout ?" + "," + " Press Yes to logout or No Cancel !!" + ",Yes" + ",No"
        )

        val shared = getSharedPreferences("user", MODE_PRIVATE)

        val type = shared.getString("language", "")
        if (type == "Tamil") {
            transalte(TranslateLanguage.TAMIL, tamil)
        } else if (type == "Telugu") {
            transalte(TranslateLanguage.TELUGU, telugu)
        } else if (type == "Kannada") {
            transalte(TranslateLanguage.KANNADA, kannada)
        } else if (type == "Hindi") {
            transalte(TranslateLanguage.HINDI, hindi)
        }


        bind.language.setOnClickListener {
            val lan = shared.getString("language", "")!!



            if (lan == "Tamil") {
                tamil.isChecked = true
            } else if (lan == "Telugu") {
                telugu.isChecked = true
            } else if (lan == "Kannada") {
                kannada.isChecked = true
            } else if (lan == "English") {
                english.isChecked = true

            }
            dialog.show()
            tamil.setOnClickListener { function(tamil, dialog) }
            telugu.setOnClickListener { function(telugu, dialog) }
            kannada.setOnClickListener { function(kannada, dialog) }
            english.setOnClickListener { function(english, dialog) }
            hindi.setOnClickListener { function(hindi, dialog) }


        }
    }

    private fun logdialog() {
        val shared = getSharedPreferences("user", MODE_PRIVATE)
        if (shared.getString("language", "") == "English") {
            title = "Do you want Logout ?"
            message = "Press Yes to logout or No Cancel !!"
            yes = "Yes"
            no = "No"
        }

        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(yes) { dialog, _ ->
                dialog.dismiss()
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply()
                finishAffinity()
                startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
            }
            setNegativeButton(no) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun function(rd: RadioButton, dialog: Dialog) {
        if (rd.isChecked) {
            if (rd.text == "Tamil") {
                transalte(TranslateLanguage.TAMIL, rd)
            } else if (rd.text == "Telugu") {
                transalte(TranslateLanguage.TELUGU, rd)
            } else if (rd.text == "Kannada") {
                transalte(TranslateLanguage.KANNADA, rd)
            } else if (rd.text == "English") {
                rd.isChecked = true
                getSharedPreferences("user", MODE_PRIVATE).edit().putString("language", "English")
                    .apply()
                finish()
            } else if (rd.text == "Hindi") {
                transalte(TranslateLanguage.HINDI, rd)
            }
        }
        dialog.dismiss()

    }

    private fun transalte(languages: String, rd: RadioButton) {
        progress.show()
        val option = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(languages)
            .build()
        val downcondition = DownloadConditions.Builder().build()
        val trans = Translation.getClient(option)
        trans.downloadModelIfNeeded(downcondition)
            .addOnSuccessListener {
                var num = 0
                string.split(",").forEach {
                    trans.translate(it)
                        .addOnSuccessListener {
                            if (num == 0) {
                                bind.profiletext.text = it
                            } else if (num == 1) {
                                bind.languagetxt.text = it
                            } else if (num == 2) {
                                bind.logouttxt.text = it
                            } else if (num == 3) {
                                title = it
                            } else if (num == 4) {
                                message = it
                            } else if (num == 5) {
                                yes = it
                            } else if (num == 6) {
                                no = it
                            }
                            num++

                        }.addOnFailureListener {

                        }
                }

                rd.isChecked = true
                getSharedPreferences("user", MODE_PRIVATE).edit()
                    .putString("language", rd.text.toString())
                    .apply()
                progress.dismiss()
            }.addOnFailureListener {
                rd.isChecked = false
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                progress.dismiss()
            }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}