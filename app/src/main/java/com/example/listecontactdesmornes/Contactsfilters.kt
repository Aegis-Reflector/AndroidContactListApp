package com.example.listecontactdesmornes

import androidx.annotation.StringRes
import com.example.listecontactdesmornes.data.Contact


enum class SortOptions(@StringRes val labelRes: Int) {
    NAME_ASC(R.string.sort_name_asc),
    NAME_DESC(R.string.sort_name_desc),
    FAVORITES_FIRST(R.string.sort_favorites_first)
}


fun filterAndSort(
    contacts: List<Contact>,
    query: String,
    sortOptions: SortOptions
): List<Contact> {
    val text = query.trim()
    // Le numero est stocker en chiffres seulement:
    val digits = text.filter { it.isDigit() }

    val filtered = if (text.isEmpty()) {
        contacts
    } else {
        contacts.filter {
            "${it.firstname} ${it.lastname}".contains(text, ignoreCase = true) ||

                    (digits.isNotEmpty() && it.phonenumber.filter { c -> c.isDigit() }
                        .contains(digits))
        }
    }

    // Ordre prénom,nom
    val byName = compareBy<Contact>(
        { it.firstname.lowercase() },
        { it.lastname.lowercase() })

    return when (sortOptions) {
        SortOptions.NAME_ASC -> filtered.sortedWith(byName)
        SortOptions.NAME_DESC -> filtered.sortedWith(byName.reversed())

        SortOptions.FAVORITES_FIRST ->
            filtered.sortedWith(compareByDescending<Contact> { it.isFavorite }.then(byName))
    }
}