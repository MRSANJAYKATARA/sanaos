package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaos.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private var root: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        return root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded || root == null) return
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvInstruction: TextView = view.findViewById(R.id.tvInstruction)
        val rvEvents: RecyclerView = view.findViewById(R.id.rvEvents)
        val startBtn: LinearLayout = view.findViewById(R.id.startBtn)

        tvDate.text = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
        tvInstruction.text = "Tap Start to activate SANA listening"

        rvEvents.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rvEvents.adapter = object : RecyclerView.Adapter<EmptyVH>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyVH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_sys, parent, false)
                return EmptyVH(v)
            }
            override fun getItemCount(): Int = 3
            override fun onBindViewHolder(holder: EmptyVH, position: Int) {}
        }

        startBtn.setOnClickListener {
            if (!isAdded || root == null) return@setOnClickListener
            view.findViewById<TextView>(R.id.statusText).text = getString(R.string.status_listening)
            view.findViewById<TextView>(R.id.btnText).text = "ACTIVE"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        root = null
    }

    private class EmptyVH(v: View) : RecyclerView.ViewHolder(v)
}
