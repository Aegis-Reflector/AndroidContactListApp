package com.example.listecontactdesmornes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.listecontactdesmornes.data.Contact
import com.example.listecontactdesmornes.ui.theme.ListeContactDesmornesTheme


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    contactViewModel: ContactViewModel, modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val contacts by contactViewModel.contacts.collectAsState()
    var screen by rememberSaveable() { mutableStateOf(Screen.LIST) }
    var selectedUId by rememberSaveable(){ mutableStateOf<Int?>(null) }

    val seenContacts = remember { mutableSetOf<Int>() }


    var searchQuery by rememberSaveable() { mutableStateOf("") }
    var sortOption by rememberSaveable() { mutableStateOf(SortOptions.NAME_ASC) }


    val displayedContacts = remember(contacts, searchQuery, sortOption) {
        filterAndSort(contacts, searchQuery, sortOption)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = Color.White) },

                actions = {
                    when (screen) {
                        Screen.LIST -> IconButton({
                            selectedUId = null
                            screen = Screen.EDIT
                        }) {
                            Icon(
                                Icons.Default.Add,
                                stringResource(R.string.add_contact),
                                tint = Color.White
                            )
                        }

                        Screen.EDIT -> {

                            if (selectedUId != null) {
                                IconButton({
                                    contacts.find { it.uid == selectedUId }?.let {
                                        contactViewModel.delete(it)
                                        Toast.makeText(
                                            context, R.string.toast_deleted, Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    selectedUId = null
                                    screen = Screen.LIST
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        stringResource(R.string.delete_contact),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->

            when (screen) {
                Screen.LIST -> {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        SearchAndSortBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            sortOption = sortOption,
                            onSortChange = { sortOption = it }
                        )

                        if (displayedContacts.isEmpty() && searchQuery.isNotBlank()) {
                            // Recherche sans résultat
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.no_results))
                            }
                        } else {
                            ContactList(
                                contacts = displayedContacts,
                                seenContacts = seenContacts,

                                modifier = Modifier.weight(1f),
                                onShowClick = {
                                    selectedUId = it
                                    screen = Screen.EDIT
                                }
                            )
                        }
                    }
                }

                Screen.EDIT -> {
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {

                        val selectedContact = contacts.find { it.uid == selectedUId }

                        EditContacts(

                            contact = selectedContact ?: Contact(),
                            onCancel = { screen = Screen.LIST },
                            onSave = { updatedContact ->
                                if (selectedContact == null) {
                                    contactViewModel.add(updatedContact)
                                    Toast.makeText(
                                        context, R.string.toast_added, Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    contactViewModel.update(updatedContact)
                                    Toast.makeText(
                                        context, R.string.toast_updated, Toast.LENGTH_SHORT
                                    ).show()
                                }
                                screen = Screen.LIST
                            }
                        )
                    }
                }
            }
        }
    )
}