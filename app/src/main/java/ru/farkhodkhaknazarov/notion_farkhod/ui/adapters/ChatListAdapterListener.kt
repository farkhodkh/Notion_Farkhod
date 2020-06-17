package ru.farkhodkhaknazarov.notion_farkhod.ui.adapters

import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat

interface ChatListAdapterListener {
    fun onItemSelected(value: Chat)
}