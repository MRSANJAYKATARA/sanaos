package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaos.R

class HistoryFragment : Fragment() {
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = inflater.inflate(R.layout.fragment_history, container, false)
        return root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded || root == null) return
        val empty = view.findViewById<View>(R.id.layoutHistoryEmpty)
        val list = view.findViewById<RecyclerView>(R.id.rvChatHistory)
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_ai, parent, false)
                return object : RecyclerView.ViewHolder(v) {}
            }
            override fun getItemCount(): Int = 0
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
        }
        empty.visibility = View.VISIBLE
        list.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root = null
    }
}
