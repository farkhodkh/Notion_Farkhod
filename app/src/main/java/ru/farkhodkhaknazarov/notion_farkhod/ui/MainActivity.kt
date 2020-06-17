package ru.farkhodkhaknazarov.notion_farkhod.ui

import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.MainActivityView
import ru.farkhodkhaknazarov.notion_farkhod.ui.presenters.MainActivityPresenter

class MainActivity : MvpAppCompatActivity(), MainActivityView {

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.activity = this
    }

    override fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun changeFragment(value: Int, chat: Chat) {
        presenter.changeFragment(value, chat)
    }
}