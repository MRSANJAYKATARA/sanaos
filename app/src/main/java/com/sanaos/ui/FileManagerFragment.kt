package com.sanaos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanaos.R

class FileManagerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvFiles = view.findViewById<RecyclerView>(R.id.rvFiles)
        rvFiles.layoutManager = LinearLayoutManager(requireContext())
        rvFiles.adapter = SimpleAdapter()
    }

    private class SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            (holder.itemView as android.widget.TextView).text = "File $position"
        }

        override fun getItemCount(): Int = 0

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}
