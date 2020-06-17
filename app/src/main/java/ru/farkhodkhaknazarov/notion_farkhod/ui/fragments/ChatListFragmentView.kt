package ru.farkhodkhaknazarov.notion_farkhod.ui.fragments

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ChatListFragmentView: MvpView {
}