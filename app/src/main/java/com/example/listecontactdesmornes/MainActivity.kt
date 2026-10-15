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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.SideEffect
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.AnnotatedString



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

// Options de tri possibles; labelRes = texte affiche dans le menu
enum class SortOptions(@StringRes val labelRes: Int) {
    NAME_ASC(R.string.sort_name_asc),
    NAME_DESC(R.string.sort_name_desc),
    FAVORITES_FIRST(R.string.sort_favorites_first)
}

// Filtre la liste selon la recherche, puis la trie selon l'option choisie.
fun filterAndSort(contacts: List<Contact>, query: String, sortOptions: SortOptions): List<Contact> {

    val text = query.trim()
    // Le numero est stocker en chiffres seulement: on compare avec les chiffres de la recherche
    val digits = text.filter { it.isDigit() }

    val filtered = if (text.isEmpty()) {
        contacts
    } else {
        contacts.filter {
            "${it.firstname} ${it.lastname}".contains(text, ignoreCase = true) ||
                    // digits.isNotEmpty(): sinon "contains" serait toujours vrai avec un texte vide
                    (digits.isNotEmpty() && it.phonenumber.filter { c -> c.isDigit() }
                        .contains(digits))
        }
    }

    // Ordre de l'alphabet par prenom ,nom
    val byName = compareBy<Contact>(
        { it.firstname.lowercase() },
        { it.lastname.lowercase() })

    return when (sortOptions) {
        SortOptions.NAME_ASC -> filtered.sortedWith(byName)
        SortOptions.NAME_DESC -> filtered.sortedWith(byName.reversed())
        // Favoris d'abord
        SortOptions.FAVORITES_FIRST ->
            filtered.sortedWith(
                compareByDescending<Contact>
                { it.isFavorite }.then(byName)
            )
    }
}
fun formatPhone(digits: String): String =
    NanpVisualTransformation().filter(AnnotatedString(digits)).text.text

@Composable
fun ContactList(
    contacts: List<Contact>,
    seenContacts: MutableSet<Int>,// contacts déjà afficher
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        //key = identifiant unique , pour animer
        items(items = contacts, key = { it.uid }) { contacts ->

            val visibleState = remember {
                MutableTransitionState(
                    contacts.uid in seenContacts
                )
            }
            SideEffect { seenContacts.add(contacts.uid) }
            visibleState.targetState = true

            AnimatedVisibility(
                visibleState = visibleState,
                modifier = Modifier.animateItem(),
                enter = slideInHorizontally(tween(350)) { -it } +
                        expandVertically(tween(350)) +
                        fadeIn(tween(350))
            ) {
                Column {
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
                            Text(formatPhone(contacts.phonenumber))
                        }


                        AnimatedVisibility(
                            visible = contacts.isFavorite,
                            enter = scaleIn(
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            ) + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            Text("★")
                        }

                    }

                    HorizontalDivider()
                }
            }
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

        mutableStateOf(contacts.phonenumber.filter { it.isDigit() }.takeLast(10))
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

// Photo affichée à l'écran et sauvegardée dans Room.
    var photoUri by remember {
        mutableStateOf(
            contacts.photoUri?.let(Uri::parse)
        )
    }
    // Passe à true quand l'utilisateur clique sur Save avec un champ obligatoire vide.
    var showErrors by remember { mutableStateOf(false) }

// Il est séparé de photoUri: la galerie donne un URI en lecture seule,
    var cameraUri by remember {
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
                // Aucune photo: image par défaut
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
                    cameraUri?.let { takePicture.launch(it) }
                },
                shape = RoundedCornerShape(50),
            ) {
                Text(stringResource(R.string.take_photo))
            }

            Button(
                onClick = {
                    // "image/*" = on n'affiche que les images dans la galerie
                    galleryLauncher.launch("image/*")
                },
                shape = RoundedCornerShape(50),
            ) {
                Text(stringResource(R.string.gallery))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Champ obligatoire: isError met le champ en rouge
            // supportingText affiche le message sous le champ
            OutlinedTextField(
                value = firstname, onValueChange = {
                    firstname = it
                }, label = {
                    Text(stringResource(R.string.first_name))
                },
                isError = showErrors && firstname.isBlank(),
                supportingText = {
                    if (showErrors && firstname.isBlank())
                        Text(stringResource(R.string.required_field))
                },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = lastname, onValueChange = {
                    lastname = it
                }, label = {
                    Text(stringResource(R.string.last_name))
                }, isError = showErrors && lastname.isBlank(),
                supportingText = {
                    if (showErrors && lastname.isBlank())
                        Text(stringResource(R.string.required_field))
                },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = phonenumber, onValueChange = {
                    phonenumber = it.filter { c->c.isDigit() }.take(10)
                }, label = {
                    Text(stringResource(R.string.phone_number))
                },
                // Affiche le texte formaté: 5140001234-> (514) 000-1234
                visualTransformation = NanpVisualTransformation(),

                isError = showErrors && phonenumber.isBlank(),
                supportingText = {
                    if (showErrors && phonenumber.isBlank())
                        Text(stringResource(R.string.required_field))
                },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = email, onValueChange = {
                    email = it
                }, label = {
                    Text(stringResource(R.string.email))
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedTextField(
                value = address, onValueChange = {
                    address = it
                }, label = {
                    Text(stringResource(R.string.address))
                }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )
            OutlinedTextField(
                value = age, onValueChange = {
                    age = it.filter { c -> c.isDigit() }
                }, label = {
                    Text(stringResource(R.string.age))
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

                Text(stringResource(R.string.favorite))
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
                    Text(stringResource(R.string.cancel), color = Color.Black)
                }
                Text("|", color = Color.Gray, fontSize = 16.sp)
                TextButton(
                    onClick = {
                        if (firstname.isBlank() || lastname.isBlank() || phonenumber.isBlank()) {
                            // Champ obligatoire manquant: on affiche les erreurs, on ne sauvegarde pas
                            showErrors = true
                        } else {
                            // copy() garde l'uid du contact et remplace seulement les champs modifiés
                            onSave(
                                contacts.copy(
                                    firstname = firstname.trim(),
                                    lastname = lastname.trim(),
                                    phonenumber = phonenumber,
                                    email = email,
                                    address = address,
                                    // toIntOrNull: retourne null si le texte est vide, alors on met 0
                                    age = age.toIntOrNull() ?: 0,
                                    isFavorite = isFav,
                                    photoUri = (photoUri
                                        ?: contacts.photoUri?.let(Uri::parse))?.toString()
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
fun SearchAndSortBar(
    query: String,
    onQueryChange: (String) -> Unit,
    sortOption: SortOptions,
    onSortChange: (SortOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    // Menu de tri ouvert ou fermé
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            // Bouton pour effacer, visible seulement s'il y a du texte
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, stringResource(R.string.clear_search))
                    }
                }
            }
        )

        // Box: ancre le menu déroulant sous le bouton "Trier"
        Box {
            TextButton(onClick = { menuOpen = true }) {
                Text(stringResource(R.string.sort))
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                SortOptions.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.labelRes)) },
                        // Coche devant l'option actuellement active
                        leadingIcon = {
                            if (option == sortOption) Icon(
                                Icons.Default.Check,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            onSortChange(option)
                            menuOpen = false
                        }
                    )
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
    val context = LocalContext.current
    val contacts by contactViewModel.contacts.collectAsState()
    var screen by remember { mutableStateOf(Screen.LIST) }
    var selectedUId by remember { mutableStateOf<Int?>(null) }
    val seenContacts = remember { mutableSetOf<Int>() }
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(SortOptions.NAME_ASC) }

    // Liste affichée: recalculée seulement quand la liste, la recherche ou le tri changent
    val displayedContacts = remember(contacts, searchQuery, sortOption) {
        filterAndSort(contacts, searchQuery, sortOption)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = Color.White) },
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
                            if (selectedUId != null)
                                contacts.find { it.uid == selectedUId }?.let {
                                    contactViewModel.delete(it)
                                    Toast.makeText(
                                        context,
                                        R.string.toast_deleted,
                                        Toast.LENGTH_SHORT
                                    ).show()
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

            when (screen) {

                Screen.LIST -> {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                    ) {
                        SearchAndSortBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            sortOption = sortOption,
                            onSortChange = { sortOption = it }
                        )

                        if (displayedContacts.isEmpty() && searchQuery.isNotBlank()) {
                            // Recherche sans resultat
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
                                })
                        }
                    }

                }

                Screen.EDIT -> {
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()

                    ) {
                        val selectedContact = contacts.find {
                            it.uid == selectedUId
                        }
                        EditContacts(
                            contacts = selectedContact ?: Contact(),
                            onCancel = { screen = Screen.LIST },
                            onSave = { updatedContact ->
                                if (selectedContact == null) {
                                    contactViewModel.add(updatedContact)
                                    Toast.makeText(
                                        context,
                                        R.string.toast_added,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    contactViewModel.update(updatedContact)
                                    Toast.makeText(
                                        context,
                                        R.string.toast_updated,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
