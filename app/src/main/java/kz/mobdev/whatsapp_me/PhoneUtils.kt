package kz.mobdev.whatsapp_me

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils

object PhoneUtils {
    fun getNormalizedPhoneNumber(
        number: String,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        if (number.isBlank() || number.any { it.isLetter() }) {
            onError()
            return
        }
        var normalizedNumber = PhoneNumberUtils.normalizeNumber(number)
        if (normalizedNumber.startsWith("8")) {
            normalizedNumber = normalizedNumber.replaceFirst("8", "+7")
        }
        if (PhoneNumberUtils.isGlobalPhoneNumber(normalizedNumber)) {
            onSuccess(normalizedNumber)
        } else {
            onError()
        }
    }

    fun openWhatsApp(context: Context, number: String) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        val intent = Intent(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setData(Uri.parse(url))
        context.startActivity(intent)
    }
}