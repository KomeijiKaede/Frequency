package net.teamfruit.frequency.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.teamfruit.frequency.database.MediaMetadataFactory
import net.teamfruit.frequency.ui.viewmodel.RecyclerViewModel
import net.teamfruit.frequency.util.Injector
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.wrapContent

class RecyclerFragment : Fragment() {
    private lateinit var viewModel: RecyclerViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProviders
                .of(this, Injector.provideRecyclerFragment(context))
                .get(RecyclerViewModel::class.java)

        viewModel.getList().observe(this, Observer {
            viewModel.adapter.addList(it!!)
            MediaMetadataFactory(context)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            constraintLayout {
                recyclerView {
                    adapter = viewModel.adapter
                    layoutManager = LinearLayoutManager(context)
                }.lparams(width = matchParent, height = wrapContent)
            }
        }.view
    }
}