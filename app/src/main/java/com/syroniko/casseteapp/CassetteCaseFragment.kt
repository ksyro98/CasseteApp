package com.syroniko.casseteapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syroniko.casseteapp.firebase.UserDB
import com.syroniko.casseteapp.mainClasses.MainViewModel
import com.syroniko.casseteapp.mainClasses.StoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CassetteCaseFragment : Fragment() {
    private lateinit var storyAdapter: StoryAdapter///////////
    private val viewModel by activityViewModels<MainViewModel>()
    @Inject lateinit var cassetteAdapter: CassetteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_cassette_case, container, false)

        val cassetteRecyclerView = view.findViewById<RecyclerView>(R.id.cassetteRecyclerView)

        cassetteRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cassetteAdapter
        }

        viewModel.cassettes.observe(viewLifecycleOwner, Observer { cassettes ->
            cassetteAdapter.cassettes = cassettes
        })

        Log.d("POUTZAROS","POUTZAROS")
        val recyclerView: RecyclerView = view.findViewById(R.id.story_adapter)
        var friendList:ArrayList<String> = arrayListOf()
        UserDB.getFriends() { userFriends ->
            for (friend in userFriends){
                friendList.add(friend.toString())}
            Log.v("Jajajaja",friendList.toString())
            friendList.addAll(friendList)
            storyAdapter = StoryAdapter(requireContext(),friendList)
            val layoutManager = LinearLayoutManager(requireContext())
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = storyAdapter
            }
            return view
    }

}

