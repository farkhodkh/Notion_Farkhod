package ru.farkhodkhaknazarov.notion_farkhod.ui.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import moxy.MvpAppCompatFragment

class RepoListPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var titels: ArrayList<String> = arrayListOf()
    var fragments: ArrayList<MvpAppCompatFragment> = arrayListOf()

    override fun getItem(position: Int): MvpAppCompatFragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titels[position]
    }
}