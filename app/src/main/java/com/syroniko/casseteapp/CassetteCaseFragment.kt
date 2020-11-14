package com.syroniko.casseteapp

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.MainClasses.Cassette
import com.syroniko.casseteapp.MainClasses.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CassetteCaseFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()
    @Inject lateinit var cassetteAdapter: CassetteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_cassette_case, container, false)

        val cassetteRecyclerView = view.findViewById<RecyclerView>(R.id.cassetteRecyclerView)

        cassetteRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = cassetteAdapter
        }

        viewModel.cassettes.observe(viewLifecycleOwner, Observer { cassettes ->
            cassetteAdapter.cassettes = cassettes
        })

        return view
    }

}

