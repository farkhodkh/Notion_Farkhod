package ru.farkhodkhaknazarov.notion_farkhod.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat
import ru.farkhodkhaknazarov.notion_farkhod.ui.presenters.ChatFragmentPresenter

class ChatFragment : MvpAppCompatFragment(), ChatFragmentView {
    @InjectPresenter
    lateinit var presenter: ChatFragmentPresenter
    lateinit var fargmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fargmentView = inflater.inflate(R.layout.fragment_chat, container, false)

        return fargmentView
    }

    override fun setChat(chat: Chat) {
        presenter.setChat(chat)
    }
}