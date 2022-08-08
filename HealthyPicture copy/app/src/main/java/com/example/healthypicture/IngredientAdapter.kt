package com.example.healthypicture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

class IngredientAdapter(val gredientList: ArrayList<Gredient>) :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gredientName: TextView = view.findViewById(R.id.gredientName)
        val gredientImage: ImageView = view.findViewById(R.id.gredientImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gredient_item, parent, false)
        return ViewHolder(view)
    }

    //为RecyclerView的子项赋值，会在当被滚到到屏幕内的子项赋值
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val gredient = gredientList[position]//获得当前项Fruit实例
        //将实例fruit里的数据放置到对应的图片位置和文字位置上
        holder.gredientImage.setImageResource(gredient.imageId)
        holder.gredientName.text = gredient.name
    }

    //告诉一共有多少个子项
    override fun getItemCount() = gredientList.size
}

class Gredient(val name: String, val imageId: Int):Serializable
internal class ingrePack:Serializable{
    fun ingreList(arrayList: ArrayList<Gredient>) {
        ingreList = arrayList
    }
    fun getList(): ArrayList<Gredient>? {
        return ingreList
    }


     var ingreList:ArrayList<Gredient> ? = null
}
