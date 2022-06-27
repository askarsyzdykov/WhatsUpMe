package kz.mobdev.whatsapp_me

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kz.mobdev.whatsapp_me.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        handleIntent(intent)

        binding.openButton.setOnClickListener {
            handleNumberAndOpenWhatsApp(binding.phoneEditText.text.toString())
        }

        binding.phoneEditText.requestFocus()
    }

    override fun onNewIntent(intent: Intent?) {
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == null) {
            return
        }
        when (intent.action) {
            Intent.ACTION_SEND -> {
                val number = intent.getStringExtra(Intent.EXTRA_TEXT)
                handleNumberAndOpenWhatsApp(number)
            }
            Intent.ACTION_PROCESS_TEXT ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val number = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                    handleNumberAndOpenWhatsApp(number)
                }
        }
    }

    private fun handleNumberAndOpenWhatsApp(number: String?) {
        if (number.isNullOrBlank() || number.any { it.isLetter() }) {
            showError()
            return
        }
        var normalizedNumber = PhoneNumberUtils.normalizeNumber(number)
        if (normalizedNumber.startsWith("8")) {
            normalizedNumber = normalizedNumber.replaceFirst("8", "+7")
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(normalizedNumber)) {
            openWhatsApp(normalizedNumber)
        } else {
            showError()
        }
        binding.phoneEditText.setText(number)
    }

    private fun showError() {
        Toast.makeText(this, "Не удалось распознать номер, введите вручную", Toast.LENGTH_LONG)
            .show()
    }

    private fun openWhatsApp(number: String) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse(url))
        startActivity(intent)
    }
}