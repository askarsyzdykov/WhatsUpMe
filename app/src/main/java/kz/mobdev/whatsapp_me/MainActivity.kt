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

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == null) {
            return
        }
        when (intent.action) {
            Intent.ACTION_PROCESS_TEXT ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val number = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                    handleNumberAndOpenWhatsApp(number)
                    finish()
                }
        }
    }

    private fun handleNumberAndOpenWhatsApp(number: String?) {
        var normalizedNumber = PhoneNumberUtils.normalizeNumber(number).apply {  }
        if (normalizedNumber.startsWith("8")) {
            normalizedNumber = normalizedNumber.replaceFirst("8", "+7")
        }
        if (!number.isNullOrBlank() && PhoneNumberUtils.isGlobalPhoneNumber(normalizedNumber)) {
            openWhatsApp(normalizedNumber)
        } else {
            Toast.makeText(this, "Wrong phone number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWhatsApp(number: String) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse(url))
        startActivity(intent)
    }
}