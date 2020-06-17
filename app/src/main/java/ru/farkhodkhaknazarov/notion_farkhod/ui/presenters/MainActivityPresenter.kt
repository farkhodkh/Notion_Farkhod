package ru.farkhodkhaknazarov.notion_farkhod.ui.presenters

import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.ChatListFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.FotoFragment
import ru.farkhodkhaknazarov.notion_farkhod.ui.MainActivity
import ru.farkhodkhaknazarov.notion_farkhod.ui.adapters.RepoListPagerAdapter
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat
import ru.farkhodkhaknazarov.notion_farkhod.ui.fragments.MainActivityView
import kotlin.reflect.KProperty

@InjectViewState
class MainActivityPresenter : MvpPresenter<MainActivityView>() {

    lateinit var pager: ViewPager
    lateinit var adapter: RepoListPagerAdapter
    lateinit var view: MainActivityView

    var fotoFragment: FotoFragment by lazy {
        FotoFragment()
    }

    var chatListFragment: ChatListFragment by lazy {
        ChatListFragment()
    }

    var chatFragment: ChatFragment by lazy{
        ChatFragment()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        prepareView()
    }

    fun prepareView() {
        val views = attachedViews

        view = views.first()

        pager = (view as MainActivity).findViewById(R.id.pager)

        adapter = RepoListPagerAdapter((view as MainActivity).supportFragmentManager)

        val job = GlobalScope.launch(Dispatchers.Main) {
            App.premissionManager.checkPermissions()
        }

        job.onJoin
        preparePagerAdapter()
        pager.adapter = adapter
    }

    fun preparePagerAdapter() {
        adapter.titels.add("Фото")
        adapter.titels.add("Список чатов")
        adapter.titels.add("Переписка")

        adapter.fragments.add(fotoFragment)
        adapter.fragments.add(chatListFragment)
        adapter.fragments.add(chatFragment)
    }

    fun changeFragment(value: Int, chat: Chat){
        pager.currentItem = value
        if (value==2){
            (adapter.fragments.get(value) as ChatFragment).setChat(chat)
        }
    }
}

private operator fun Any.setValue(
    mainActivityPresenter: MainActivityPresenter,
    property: KProperty<*>,
    chatFragment: ChatFragment
) {
    TODO("Not yet implemented")
}

private operator fun Any.setValue(
    mainActivityPresenter: MainActivityPresenter,
    property: KProperty<*>,
    chatListFragment: ChatListFragment
) {
    TODO("Not yet implemented")
}

private operator fun Any.setValue(
    mainActivityPresenter: MainActivityPresenter,
    property: KProperty<*>,
    fotoFragment: FotoFragment
) {
    TODO("Not yet implemented")
}
