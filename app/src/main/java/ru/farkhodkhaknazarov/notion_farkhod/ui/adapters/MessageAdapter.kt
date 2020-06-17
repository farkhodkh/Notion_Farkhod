package ru.farkhodkhaknazarov.notion_farkhod.ui.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Message


class MessageAdapter(var context: Context): BaseAdapter() {

    var messages: MutableList<Message> = arrayListOf()

    fun addMessage(message: Message){
        messages.add(message)
    }

    fun clearMessages(){
        messages.clear()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder = MessageViewHolder()
        val messageInflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val message = messages[position]
        var resultView: View

        if (message.isBelongsToCurrentUser()) {
            resultView = messageInflater.inflate(R.layout.my_message, null)
            holder.messageBody = resultView!!.findViewById(R.id.message_body) as TextView
            resultView!!.tag = holder
            holder.messageBody!!.setText(message.text)
        } else{
            resultView = messageInflater.inflate(R.layout.chat_message, null)
            holder.avatar = resultView!!.findViewById(R.id.avatar) as View
            holder.name = resultView!!.findViewById(R.id.name) as TextView
            holder.messageBody = resultView!!.findViewById(R.id.message_body) as TextView
            resultView!!.tag = holder

            holder.name!!.setText(message.getMemberData().name)
            holder.messageBody!!.text = message.text
            val drawable = holder.avatar!!.background as GradientDrawable
            drawable.setColor(Color.parseColor(message.getMemberData().color))
        }

        return resultView!!
    }

    override fun getItem(position: Int): Any = messages.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = messages.size

    internal class MessageViewHolder {
        var avatar: View? = null
        var name: TextView? = null
        var messageBody: TextView? = null
    }
}

