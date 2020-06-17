package ru.farkhodkhaknazarov.notion_farkhod.ui.presenters

import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.size
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.adapters.MessageAdapter
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Member
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Message
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatFragmentView

@InjectViewState
class ChatFragmentPresenter : MvpPresenter<ChatFragmentView>(){
    lateinit var fragmentView: View
    var vChat:Chat? = null
    lateinit var tvChatHeader: TextView
    lateinit var fabSend: FloatingActionButton
    lateinit var tietChat: TextInputEditText
    lateinit var listOfMessages: ListView
    lateinit var adapter: MessageAdapter
    lateinit var messages: ArrayList<String>

    override fun attachView(view: ChatFragmentView?) {
        super.attachView(view)
        messages = arrayListOf()

        fragmentView = (view as ChatFragment).fargmentView
        tvChatHeader = fragmentView.findViewById(R.id.tvChatHeader)
        fabSend = fragmentView.findViewById(R.id.fabSend)
        tietChat = fragmentView.findViewById(R.id.tietChat)
        listOfMessages = fragmentView.findViewById(R.id.listOfMessages)
//        adapter = ArrayAdapter(App.context, R.layout.chat_row_item, messages)
        adapter = MessageAdapter(App.context)

        tvChatHeader.text = vChat?.chatHeader

        listOfMessages.adapter = adapter

        fabSend.setOnClickListener {
            adapter.addMessage(getMessage(true, tietChat.text.toString()))
            adapter.addMessage(getMessage(false, "Привет"))
            adapter.notifyDataSetChanged()

            listOfMessages.setSelection(adapter.messages.size)
            tietChat.setText("")
        }
    }

    fun getMessage(belongsToCurrentuser: Boolean, text:String): Message =
         Message(belongsToCurrentuser, text, Member(if (belongsToCurrentuser) "Вы" else "Собеседник", if (belongsToCurrentuser) "White" else "Black"))

    fun setChat(chat: Chat){
        this.vChat = chat
        tvChatHeader.text = chat.chatHeader
        refreshChatList()
    }

    fun refreshChatList(){
        adapter.clearMessages()
        adapter.addMessage(getMessage(true, "Привет"))
        adapter.notifyDataSetChanged()
    }
}
