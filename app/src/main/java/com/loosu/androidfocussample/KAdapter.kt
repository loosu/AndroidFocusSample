package com.loosu.androidfocussample

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class KAdapter : Adapter<KAdapter.KHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): KHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false)
        return KHolder(itemView)
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: KHolder, position: Int) {
    }

    class KHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}