package ru.farkhodkhaknazarov.notion_farkhod.ui.presenters

import android.view.View
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.adapters.ChatListAdapter
import ru.farkhodkhaknazarov.notion_farkhod.ui.adapters.ChatListAdapterListener
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatListFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatListFragmentView

@InjectViewState
class ChatListFragmentPresenter : MvpPresenter<ChatListFragmentView>(), ChatListAdapterListener {
    lateinit var fragmentView: View
    lateinit var recyclerView: RecyclerView
    lateinit var mAdapter: ChatListAdapter
    lateinit var chatList: ArrayList<Chat>
    override fun attachView(view: ChatListFragmentView?) {
        super.attachView(view)

        chatList = arrayListOf()

        fragmentView = (view as ChatListFragment).fargmentView
        recyclerView = fragmentView.findViewById(R.id.chatListRecyclerView)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(App.mInstance)
        recyclerView.layoutManager = mLayoutManager

        mAdapter = ChatListAdapter(App.mInstance, chatList, this)
        recyclerView.adapter = mAdapter

        mAdapter.refreshData()

    }

    override fun onItemSelected(chat: Chat) {
        App.activity.changeFragment(2, chat)
    }

}