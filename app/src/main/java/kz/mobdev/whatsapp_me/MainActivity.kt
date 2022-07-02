package kz.mobdev.whatsapp_me

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kz.mobdev.whatsapp_me.databinding.ActivityMainBinding

const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"

class MainActivity : AppCompatActivity() {

    companion object {
        fun showNotification(context: Context) {
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                    FLAG_IMMUTABLE
                )
            val notification =
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.notification_text))
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .build()
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1, notification)
        }
    }

    private lateinit var binding: ActivityMainBinding

    private val clipBoardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    private var hasPhoneInIntent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        hasPhoneInIntent = handleIntent(intent)

        binding.openButton.setOnClickListener {
            handleNumberAndOpenWhatsApp(binding.phoneEditText.text.toString())
        }

        binding.phoneEditText.requestFocus()

        showNotification(this)
    }

    override fun onNewIntent(intent: Intent?) {
        hasPhoneInIntent = handleIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasPhoneInIntent) {
            getTextFromClipboard()?.let {
                PhoneUtils.getNormalizedPhoneNumber(
                    it,
                    onSuccess = { binding.phoneEditText.setText(it) },
                    onError = {}
                )

            }
        }
    }

    private fun handleIntent(intent: Intent?): Boolean {
        if (intent?.action == null) {
            return false
        }
        when (intent.action) {
            Intent.ACTION_SEND ->
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { number ->
                    handleNumberAndOpenWhatsApp(number)
                    return true
                }
            Intent.ACTION_PROCESS_TEXT ->
                intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.let { number ->
                    handleNumberAndOpenWhatsApp(number)
                    return true
                }
        }
        return false
    }

    private fun handleNumberAndOpenWhatsApp(number: String) {
        PhoneUtils.getNormalizedPhoneNumber(
            number,
            onSuccess = { PhoneUtils.openWhatsApp(this, it) },
            onError = { showError() }
        )
        binding.phoneEditText.setText(number)
    }

    private fun showError() {
        Toast.makeText(this, getString(R.string.error_unable_to_normalize_phone_number), Toast.LENGTH_LONG)
            .show()
    }

    private fun getTextFromClipboard(): String? {
        val item = clipBoardManager.primaryClip?.getItemAt(0)
        return item?.text?.toString()
    }
}