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
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listecontactdesmornes.data.Contact
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon

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
        contactViewModel.add(
            Contact(
                firstname = "Test",
                lastname = "Contact",
                phonenumber = "555-0000",
                email = "test@test.com",
                address = "1 Test St",
                age = 20,
                isFavorite = true
            )
        )
    }
}


enum class Screen {
    LIST, EDIT
}

@Composable
fun Application(
    contacts: List<Contact>, viewModel: ContactViewModel, modifier: Modifier = Modifier
) {

    var screen by remember {
        mutableStateOf(Screen.LIST)
    }

    var selectedUId by remember {
        mutableIntStateOf(0)
    }


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

            if (selectedContact != null) {
                EditContacts(
                    contacts = selectedContact,
                    onCancel = { screen = Screen.LIST },
                    onSave = { updatedContact ->
                        viewModel.update(updatedContact)
                        screen = Screen.LIST
                    })
            }
        }
    }
}


@Composable
fun ContactList(
    contacts: List<Contact>, onShowClick: (Int) -> Unit, modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
    ) {
        items(items = contacts) { contacts ->

            Row(modifier = Modifier
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp)

            ) {

                Button(
                    onClick = {},
                    shape = RoundedCornerShape(50),
                ) {
                    Text("Take Photo")
                }

                Button(
                    onClick = { onCancel() },
                    shape = RoundedCornerShape(50),

                    ) {
                    Text("Gallery")
                }
            }
        }
        Spacer(
            modifier = Modifier.height(30.dp)
        )


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

        Spacer(modifier = Modifier.weight(1f))
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
                            isFavorite = isFav
                        )
                    )
                }) {
                Text("Save", color = Color.Black, fontSize = 16.sp)
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


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact App ", color = Color.White) }, actions = {
                IconButton({}) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter un contact"
                    )
                }
            }, colors = topAppBarColors(
                MaterialTheme.colorScheme.primary
            )


            )

        },

        content = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                Application(contacts = contacts, viewModel = contactViewModel)


            }
        })
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListeContactDesmornesTheme {

    }
}