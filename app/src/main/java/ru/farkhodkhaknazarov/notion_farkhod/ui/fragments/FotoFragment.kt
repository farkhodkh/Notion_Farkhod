package ru.farkhodkhaknazarov.notion_farkhod.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import ru.farkhodkhaknazarov.notion_farkhod.R
import ru.farkhodkhaknazarov.notion_farkhod.ui.presenters.FotoFragmentPresenter

class FotoFragment : MvpAppCompatFragment(), FotoFragmentView {
    @InjectPresenter
    lateinit var presenter: FotoFragmentPresenter

    lateinit var fragmentView: View


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater.inflate(R.layout.fragment_foto, container, false)
        return fragmentView
    }

    override fun onResume() {
        super.onResume()
        presenter.onResumeView()
    }

    override fun onPause() {
        presenter.onPauseView()
        super.onPause()
    }


}