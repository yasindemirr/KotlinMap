package com.demir.kotlinmap.adepter

import android.content.Intent
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demir.kotlinmap.databinding.RowBinding
import com.demir.kotlinmap.model.Place
import com.demir.kotlinmap.view.MapsActivity

class PlaceAdepter(val placeList:List<Place>):RecyclerView.Adapter<PlaceAdepter.PlaceHolder>() {



    class PlaceHolder(val binding: RowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val view=RowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.binding.recText.text= placeList[position].name
        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("place",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)



        }

    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}