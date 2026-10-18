package com.example.listecontactdesmornes


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.listecontactdesmornes.data.Contact
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource


@Composable
fun ContactList(
    contacts: List<Contact>,
    seenContacts: MutableSet<Int>,
    onShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {

        items(items = contacts, key = { it.uid }) { contact ->

            val visibleState = remember { MutableTransitionState(contact.uid in seenContacts) }

            SideEffect { seenContacts.add(contact.uid) }

            visibleState.targetState = true

            AnimatedVisibility(
                visibleState = visibleState,

                modifier = Modifier.animateItem(),

                enter = slideInHorizontally(tween(350)) { -it } +
                        expandVertically(tween(350)) +
                        fadeIn(tween(350))
            ) {
                ContactRow(contact = contact, onClick = { onShowClick(contact.uid) })
            }
        }
    }
}

@Composable
fun ContactRow(contact: Contact, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (contact.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(contact.photoUri),
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

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${contact.firstname} ${contact.lastname}")
                Text(formatPhone(contact.phonenumber))
            }

            AnimatedVisibility(
                visible = contact.isFavorite,
                enter = scaleIn(
                    spring
                        (dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Text("★")
            }
        }

        HorizontalDivider()
    }
}