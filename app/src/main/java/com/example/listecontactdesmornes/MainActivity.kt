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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListeContactDesmornesTheme {
                  ContactScreen()

                }
            }
        }
    }


data class TvShow(
    val id: Int,
    val title: String,
    val year: Int,
    val network: String,
    val isFavorite: Boolean
)

val sampleShows = listOf(
    TvShow(1, "Breaking Bad", 2008, "AMC", true),
    TvShow(2, "Better Call Saul", 2015, "AMC", false),
    TvShow(3, "Game of Thrones", 2011, "HBO", false),
    TvShow(4, "The Sopranos", 1999, "HBO", true),
    TvShow(5, "The Wire", 2002, "HBO", false),
    TvShow(6, "Stranger Things", 2016, "Netflix", false),
    TvShow(7, "Succession", 2018, "HBO", true),
    TvShow(8, "Mad Men", 2007, "AMC", false),
    TvShow(9, "Dark", 2017, "Netflix", false),
    TvShow(10, "Mindhunter", 2017, "Netflix", true),
    TvShow(11, "The Crown", 2016, "Netflix", false),
    TvShow(12, "True Detective", 2014, "HBO", false)
)

enum class Screen {
    LIST,
    EDIT
}

@Composable
fun Application(modifier: Modifier = Modifier) {

    var screen by remember {
        mutableStateOf(Screen.LIST)
    }

    var selectedId by remember {
        mutableIntStateOf(0)
    }
    var shows by remember {
        mutableStateOf(sampleShows)
    }

    when (screen) {

        Screen.LIST -> {
            ShowsList(
                shows = shows,
                onShowClick = {
                    selectedId = it
                    screen = Screen.EDIT
                }
            )
        }

        Screen.EDIT -> {
            val selectedShow = sampleShows.find {
                it.id == selectedId
            }

            if (selectedShow != null) {
                Formulaire(
                    selectedShow,
                    onCancel = { screen = Screen.LIST },
                    onSave = { updatedShow ->
                        shows = shows.map { show ->
                            if (show.id == updatedShow.id) {
                                updatedShow
                            } else {
                                show
                            }
                        }

                        screen = Screen.LIST
                    }
                )

            }
        }
    }
}

@Composable
fun ShowsList(
    shows: List<TvShow>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
    ) {

        items(items = shows) { show ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onShowClick(show.id)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(show.title)
                    Text("${show.network} • ${show.year}")
                }

                if (show.isFavorite) {
                    Text("★")
                }
            }

            Divider()
        }
    }
}

@Composable
fun Formulaire(show: TvShow,
               onCancel: () -> Unit,
               onSave : (TvShow) -> Unit,
               modifier: Modifier = Modifier) {

    var title by remember {
        mutableStateOf(show.title)
    }

    var year by remember {
        mutableStateOf(show.year.toString())
    }

    var network by remember {
        mutableStateOf(show.network)
    }

    var isFav by remember {
        mutableStateOf(show.isFavorite)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp)

            ) {

                Button(
                    onClick = {
                        onSave(
                            TvShow(
                                id = show.id,
                                title = title,
                                year = year.toIntOrNull() ?: show.year,
                                network = network,
                                isFavorite = isFav
                            )
                        )
                    },
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
            value = title,
            onValueChange = {
                title = it
            },
            label = {
                Text("Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = year,
            onValueChange = {
                year = it
            },
            label = {
                Text("Phone Number")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = network,
            onValueChange = {
                network = it
            },
            label = {
                Text("Age")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = network,
            onValueChange = {
                network = it
            },
            label = {
                Text("Email")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = network,
            onValueChange = {
                network = it
            },
            label = {
                Text("Address")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )


        Spacer(
            modifier = Modifier.height(16.dp)
        )



        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = isFav,
                onCheckedChange = {
                    isFav = it
                }
            )

            Text("Favorite")
        }

        Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier= Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onSave(
                            TvShow(
                                id = show.id,
                                title = title,
                                year = year.toIntOrNull() ?: show.year,
                                network = network,
                                isFavorite = isFav
                            )
                        )
                    }
                ) {
                    Text("Cancel", color = Color.Black)
                }
                Text("|", color = Color.Gray, fontSize = 16.sp)
                TextButton(
                    onClick = { onCancel() }

                ) {
                    Text("Save", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen (modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact App ", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary)
            )
        },

        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Application()


            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListeContactDesmornesTheme {
        ContactScreen()
    }
}