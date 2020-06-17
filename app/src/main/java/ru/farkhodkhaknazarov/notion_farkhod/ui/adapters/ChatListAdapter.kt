package ru.farkhodkhaknazarov.notion_farkhod.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat

class ChatListAdapter(context: Context, chatList: ArrayList<Chat>, listener: ChatListAdapterListener) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
    var context: Context

    var chatList: ArrayList<Chat>
    var listener: ChatListAdapterListener

    init {
        this.context = context
        this.listener = listener
        this.chatList = chatList
    }

    fun refreshData(){

        GlobalScope.launch(Dispatchers.Main) {
            getTestData()
            notifyDataSetChanged()
        }
    }

    suspend fun getTestData() {
        //Иммитация получения данных из базы
        chatList.clear()
        chatList = arrayListOf(Chat("Приветствую"), Chat("Согласование встречи"))
    }

    inner class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txVChatHeader: TextView

        init {
            txVChatHeader = itemView.findViewById(R.id.txVChatHeader)

            itemView.setOnClickListener { v: View? ->
                onClickListener(v)
            }
        }

        private fun onClickListener(v: View?) {
            listener.onItemSelected(chatList.get(adapterPosition))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_row_item, parent, false)

        return ChatListViewHolder(itemView)
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val value: Chat = chatList.get(position)

        holder.txVChatHeader.text = value.chatHeader
    }
}