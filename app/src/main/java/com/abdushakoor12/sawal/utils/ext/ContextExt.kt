package com.abdushakoor12.sawal.utils.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

fun Context.copyTextToClipboard(
    text: String,
    label: String = "Copied Text",
) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(intent, "Share..."))
}