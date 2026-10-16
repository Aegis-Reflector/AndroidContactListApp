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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.listecontactdesmornes.data.Contact


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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${contact.firstname} ${contact.lastname}")
                Text(formatPhone(contact.phonenumber))
            }

            AnimatedVisibility(
                visible = contact.isFavorite,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Text("★")
            }
        }

        HorizontalDivider()
    }
}