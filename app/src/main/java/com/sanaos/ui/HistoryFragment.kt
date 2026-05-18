package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaos.R
import com.sanaos.data.SanaDatabaseHelper
import com.sanaos.data.HistoryItem

class HistoryFragment : Fragment() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var layoutHistoryEmpty: TextView
    private val items = mutableListOf<HistoryItem>()
    private val adapter = HistoryAdapter(items)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvHistory = view.findViewById(R.id.rvHistory)
        layoutHistoryEmpty = view.findViewById(R.id.layoutHistoryEmpty)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        loadHistory()
    }

    private fun loadHistory() {
        Thread {
            val db = SanaDatabaseHelper(requireContext())
            val rows = db.getAllHistory()
            requireActivity().runOnUiThread {
                items.clear()
                items.addAll(rows)
                adapter.notifyDataSetChanged()
                layoutHistoryEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }.start()
    }

    private class HistoryAdapter(private val data: List<HistoryItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemViewType(position: Int): Int = position % 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
                UserVH(v)
            } else {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_ai, parent, false)
                AiVH(v)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = data[position]
            if (holder is UserVH) holder.tv.text = item.userInput
            if (holder is AiVH) holder.tv.text = item.sanaResponse
        }

        override fun getItemCount(): Int = data.size

        class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv: TextView = itemView.findViewById(R.id.userMessage)
        }

        class AiVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv: TextView = itemView.findViewById(R.id.aiMessage)
        }
    }
}
