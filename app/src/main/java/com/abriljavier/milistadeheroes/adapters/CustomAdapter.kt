package com.abriljavier.milistadeheroes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.abriljavier.milistadeheroes.R

class CustomAdapter(context: Context, private val dataList: List<String>) : ArrayAdapter<String>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list, parent, false)
        val textView: TextView = itemView.findViewById(R.id.textViewItem)
        textView.text = dataList[position]
        return itemView
    }
}