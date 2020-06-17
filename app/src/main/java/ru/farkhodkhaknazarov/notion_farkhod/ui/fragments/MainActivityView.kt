package ru.farkhodkhaknazarov.notion_farkhod.ui.fragments

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.farkhodkhaknazarov.notion_farkhod.ui.data.Chat

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface MainActivityView: MvpView {
    fun showToast(message: String)
    fun changeFragment(value: Int, chat: Chat)
}