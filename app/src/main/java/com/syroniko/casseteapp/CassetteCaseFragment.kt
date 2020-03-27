package com.syroniko.casseteapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.syroniko.casseteapp.MainClasses.MainActivity
import com.syroniko.casseteapp.room.AppDatabase
import com.syroniko.casseteapp.room.LocalCassette
import kotlinx.android.synthetic.main.fragment_cassette_case.*
import kotlinx.coroutines.launch


class CassetteCaseFragment : Fragment() {

    private var cassettes = mutableListOf<LocalCassette>()
    private var cassetteAdapter: CassetteAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_cassette_case, container, false)

        val cassetteRecyclerView = view.findViewById<RecyclerView>(R.id.cassetteRecyclerView)

        val viewManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

//        if(context == null){
//            Log.d(MainActivity::class.java.simpleName, "NULL :(")
//        }


        cassetteAdapter = context?.let { CassetteAdapter(it, cassettes) }
        Log.d(MainActivity::class.java.simpleName, "!$cassetteAdapter")

        cassetteRecyclerView.apply {
            cassetteAdapter?.notifyDataSetChanged()
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = cassetteAdapter
        }

        return view
    }

    fun updateData(cassettes: MutableList<LocalCassette>){
        this.cassettes.addAll(cassettes)
        this.cassetteAdapter?.notifyDataSetChanged()
        Log.d(MainActivity::class.java.simpleName, "!$cassetteAdapter")
    }
}

