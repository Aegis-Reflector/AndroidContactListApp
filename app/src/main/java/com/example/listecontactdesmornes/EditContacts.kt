package com.example.listecontactdesmornes

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.listecontactdesmornes.data.Contact

@Composable
fun EditContacts(
    contact: Contact,
    onCancel: () -> Unit,
    onSave: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {

    var firstname by remember { mutableStateOf(contact.firstname) }
    var lastname by remember { mutableStateOf(contact.lastname) }

    var phonenumber by remember {
        mutableStateOf(contact.phonenumber.filter { it.isDigit() }.takeLast(10))
    }
    var email by remember { mutableStateOf(contact.email) }
    var address by remember { mutableStateOf(contact.address) }

    var age by remember { mutableStateOf(if (contact.age == 0) "" else contact.age.toString()) }
    var isFav by remember { mutableStateOf(contact.isFavorite) }


    var showErrors by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var photoUri by remember { mutableStateOf(contact.photoUri?.let(Uri::parse)) }


    val cameraUri by remember {
        mutableStateOf(
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        "photo_${System.currentTimeMillis()}.jpg"
                    )
                }
            )
        )
    }


    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri = cameraUri
            }
        }
    )


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) photoUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .clip(CircleShape)   // découpe en cercle
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                Image(

                    painter = rememberAsyncImagePainter(
                        photoUri,
                        error = painterResource(R.drawable.dm),
                        fallback = painterResource(R.drawable.dm)
                    ),
                    contentDescription = stringResource(R.string.contact_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {

                Image(
                    painterResource(R.drawable.dm),
                    contentDescription = stringResource(R.string.contact_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally)
        ) {
            Button(

                onClick = { cameraUri?.let { takePicture.launch(it) } },
                shape = RoundedCornerShape(50),
            ) { Text(stringResource(R.string.take_photo)) }

            Button(
                // "image/*" = on n'affiche que les images dans la galerie
                onClick = { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(50),
            ) { Text(stringResource(R.string.gallery)) }
        }

        Spacer(modifier = Modifier.height(16.dp))


        ContactActions(
            phonenumber = phonenumber,
            email = email
        )

        Spacer(modifier = Modifier.height(16.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            RequiredTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = stringResource(R.string.first_name),
                showError = showErrors
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequiredTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = stringResource(R.string.last_name),
                showError = showErrors
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequiredTextField(
                value = phonenumber,

                onValueChange = { phonenumber = it.filter { c -> c.isDigit() }.take(10) },
                label = stringResource(R.string.phone_number),
                showError = showErrors,
                // Affiche le texte formater
                visualTransformation = NanpVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                //seulement les chiffres
                value = age, onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(R.string.age)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isFav, onCheckedChange = { isFav = it })
                Text(stringResource(R.string.favorite))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { onCancel() }) {
                    Text(stringResource(R.string.cancel), color = Color.Black)
                }
                Text("|", color = Color.Gray, fontSize = 16.sp)
                TextButton(
                    onClick = {
                        if (firstname.isBlank() || lastname.isBlank() || phonenumber.isBlank()) {

                            showErrors = true
                        } else {

                            onSave(
                                contact.copy(
                                    firstname = firstname.trim(),
                                    lastname = lastname.trim(),
                                    phonenumber = phonenumber,
                                    email = email,
                                    address = address,

                                    age = age.toIntOrNull() ?: 0,
                                    isFavorite = isFav,

                                    photoUri = photoUri?.toString()
                                )
                            )
                        }
                    }) {
                    Text(stringResource(R.string.save), color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}


@Composable
fun RequiredTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    showError: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val hasError = showError && value.isBlank()

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        // isError met le champ en rouge, supportingText affiche le message sous le champ
        isError = hasError,
        supportingText = { if (hasError) Text(stringResource(R.string.required_field)) },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}