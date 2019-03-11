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
import android.widget.LinearLayout
import net.teamfruit.frequency.R
import net.teamfruit.frequency.ui.viewmodel.BrowserViewModel
import net.teamfruit.frequency.util.Injector
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.UI

class BrowserFragment: Fragment() {
    private lateinit var viewModel: BrowserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders
                .of(this, Injector.provideBrowserFragment(context))
                .get(BrowserViewModel::class.java)
        viewModel.searchResult.observe(this, Observer {
            viewModel.adapter.addList(it!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return UI {
            constraintLayout {
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        val query = editText {
                            id = R.id.editText
                        }.lparams(width = matchParent) {
                            weight = 1F
                        }
                        button {
                            text = "Search"
                            setOnClickListener {
                                try {
                                    viewModel.search(query.text.toString())
                                } catch (e: IllegalStateException) {
                                    return@setOnClickListener
                                }
                            }
                        }
                    }
                    recyclerView {
                        adapter = viewModel.adapter
                        layoutManager = LinearLayoutManager(context)
                    }.lparams(width = matchParent, height = matchParent)
                }.lparams(width = matchParent, height = matchParent)
            }
        }.view
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            Extractor.addInfo(
                    editText.text.toString(),
                    viewModel.getBase()
            )
        }
    }*/
}