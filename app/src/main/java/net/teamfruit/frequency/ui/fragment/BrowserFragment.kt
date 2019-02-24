package net.teamfruit.frequency.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_browser.*
import net.teamfruit.frequency.R
import net.teamfruit.frequency.ui.viewmodel.BrowserViewModel
import net.teamfruit.frequency.util.Extractor
import net.teamfruit.frequency.util.Injector

class BrowserFragment: Fragment() {
    private lateinit var viewModel: BrowserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders
                .of(this, Injector.provideBrowserFragment(context))
                .get(BrowserViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            Extractor.addInfo(
                    editText.text.toString(),
                    viewModel.getBase()
            )
        }
    }
}