package com.example.listecontactdesmornes

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// Ouvre une autre application phone, messages, courriel

fun openIntent(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun ContactActions(
    phonenumber: String,
    email: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(

            onClick = {
                openIntent(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phonenumber")))
            },
            enabled = phonenumber.isNotBlank(),
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.action_call)) }

        OutlinedButton(
            // smsto:ouvre l'application de messages avec le contact rempli
            onClick = {
                openIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phonenumber")))
            },
            enabled = phonenumber.isNotBlank(),
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.action_text)) }

        OutlinedButton(
            // mailto: ouvre l'application de courriel;
            onClick = {
                openIntent(
                    context,
                    Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email.trim(), null))
                )
            },
            enabled = email.isNotBlank(),
            modifier = Modifier.weight(1f)
        ) { Text(stringResource(R.string.action_email)) }
    }
}