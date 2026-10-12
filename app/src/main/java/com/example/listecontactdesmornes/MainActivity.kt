package com.example.listecontactdesmornes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.listecontactdesmornes.ui.theme.ListeContactDesmornesTheme
import androidx.compose.material3.*
import androidx.compose.ui.graphics.*
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listecontactdesmornes.data.Contact
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    private val contactViewModel: ContactViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListeContactDesmornesTheme {
                ContactScreen(contactViewModel)

            }
        }
    }
}


enum class Screen {
    LIST, EDIT
}

@Composable
fun ContactList(
    contacts: List<Contact>, onShowClick: (Int) -> Unit, modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
    ) {
        items(items = contacts) { contacts ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onShowClick(contacts.uid)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {

                Column {
                    Text("${contacts.firstname} ${contacts.lastname}")
                    Text(contacts.phonenumber)
                }

                if (contacts.isFavorite) {
                    Text("★")
                }
            }

            HorizontalDivider()
        }
    }
}

@Composable
fun EditContacts(
    contacts: Contact,
    onCancel: () -> Unit,
    onSave: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {

    var firstname by remember {
        mutableStateOf(contacts.firstname)
    }

    var lastname by remember {
        mutableStateOf(contacts.lastname)
    }
    var phonenumber by remember {
        mutableStateOf(contacts.phonenumber)
    }
    var email by remember {
        mutableStateOf(contacts.email)
    }
    var address by remember {
        mutableStateOf(contacts.address)
    }
    var age by remember {
        mutableStateOf(contacts.age.toString())
    }
    var isFav by remember {
        mutableStateOf(contacts.isFavorite)
    }

    val context = LocalContext.current
    var photoUri by remember {
        mutableStateOf(
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // What type of image
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
            }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) photoUri = uri
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            val displayUri = photoUri ?: contacts.photoUri?.let(Uri::parse)
            if (displayUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        displayUri,
                        error = painterResource(R.drawable.dm),
                        fallback = painterResource(R.drawable.dm)
                    ),
                    contentDescription = "Photo du contact",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painterResource(R.drawable.dm),
                    contentDescription = "Photo du contact",
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
                onClick = {
                    if (photoUri != null) takePicture.launch(photoUri!!)
                },
                shape = RoundedCornerShape(50),
            ) {
                Text("Take Photo")
            }

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                shape = RoundedCornerShape(50),
            ) {
                Text("Gallery")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)                              // 👈 only weight
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = firstname, onValueChange = {
                    firstname = it
                }, label = {
                    Text(text = "First Name")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = lastname, onValueChange = {
                    lastname = it
                }, label = {
                    Text("Last Name")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = phonenumber, onValueChange = {
                    phonenumber = it
                }, label = {
                    Text("Phone Number")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = email, onValueChange = {
                    email = it
                }, label = {
                    Text("Email")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = address, onValueChange = {
                    address = it
                }, label = {
                    Text("Address")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )
            OutlinedTextField(
                value = age, onValueChange = {
                    age = it
                }, label = {
                    Text("Age")
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )



            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = isFav, onCheckedChange = {
                        isFav = it
                    })

                Text("Favorite")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onCancel()
                    }) {
                    Text("Cancel", color = Color.Black)
                }
                Text("|", color = Color.Gray, fontSize = 16.sp)
                TextButton(
                    onClick = {
                        onSave(
                            contacts.copy(
                                firstname = firstname,
                                lastname = lastname,
                                phonenumber = phonenumber,
                                email = email,
                                address = address,
                                age = age.toIntOrNull() ?: 0,
                                isFavorite = isFav,
                                photoUri = (photoUri
                                    ?: contacts.photoUri?.let(Uri::parse))?.toString()
                            )
                        )
                    }) {
                    Text("Save", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ContactScreen(
        contactViewModel: ContactViewModel, modifier: Modifier = Modifier
    ) {

        val contacts by contactViewModel.contacts.collectAsState()
        var screen by remember { mutableStateOf(Screen.LIST) }
        var selectedUId by remember { mutableStateOf<Int?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Contact App ", color = Color.White) },
                    actions = {
                        when (screen) {
                            Screen.LIST ->
                                IconButton({
                                    selectedUId = null
                                    screen = Screen.EDIT
                                }) {
                                    Icon(
                                        Icons.Default.Add,
                                        "Ajouter un contact",
                                        tint = Color.White
                                    )
                                }

                            Screen.EDIT -> IconButton({
                                contacts.find { it.uid == selectedUId }?.let {
                                    contactViewModel.delete(it)
                                }
                                selectedUId = null
                                screen = Screen.LIST
                            })
                            {
                                Icon(
                                    Icons.Default.Delete,
                                    "Supprimer le contact",
                                    tint = Color.White
                                )
                            }
                        }
                    }, colors = topAppBarColors(
                        MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )


                )

            },

            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()

                ) {
                    when (screen) {

                        Screen.LIST -> {
                            ContactList(
                                contacts = contacts, onShowClick = {
                                    selectedUId = it
                                    screen = Screen.EDIT
                                })
                        }

                        Screen.EDIT -> {
                            val selectedContact = contacts.find {
                                it.uid == selectedUId
                            }
                            EditContacts(
                                contacts = selectedContact ?: Contact(),
                                onCancel = { screen = Screen.LIST },
                                onSave = { updatedContact ->
                                    if (selectedContact == null) {
                                        contactViewModel.add(updatedContact)     // 👈 INSERT
                                    } else {
                                        contactViewModel.update(updatedContact)  // 👈 UPDATE
                                    }
                                    screen = Screen.LIST
                                })

                        }
                    }


                }
            })
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ListeContactDesmornesTheme {

        }
    }