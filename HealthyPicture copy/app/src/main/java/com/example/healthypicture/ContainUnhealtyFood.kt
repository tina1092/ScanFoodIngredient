package com.example.healthypicture

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContainUnhealtyFood : AppCompatActivity() {
    private val gredientList = ArrayList<Gredient>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contain_unhealty_food)

        val se = intent.getSerializableExtra("gredientList")
        //val gredient = intent.getStringExtra("gredientList")

        //val gredientArray = gredient?.let { stringToWords(it) }

        if(se is ingrePack){
            val pack: ingrePack ?= se as ingrePack?
            if (pack != null) {
                initGredient(pack.getList())
            }
        }else{
            Log.e("ContainUnhealtyFood", "incorrect intent")
            return
        }

        //gredientArray?.let { initGredient(it) }
        val layoutManager = LinearLayoutManager(this)
        val recyclerView:RecyclerView = findViewById(R.id.recyclerview)
        //告诉recyclerView，布局方式是linear layout(line 20)线性布局，
        recyclerView.layoutManager = layoutManager
        val adapter = IngredientAdapter(gredientList)
        recyclerView.adapter = adapter


        val backButton: Button = findViewById(R.id.BackButton)
        backButton.setOnClickListener {
            val text_data: TextView? = findViewById(R.id.text_data)
            if(text_data!=null)text_data.setText(null)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    //句子转为list
    fun stringToWords(s : String) = s.trim().splitToSequence(' ')
        .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
        .toList()
    fun initGredient(arr:ArrayList<Gredient>?){
        if (arr != null) {
            for(word in arr){
                Log.i("WangJiayu", "Word: " + word)
                gredientList.add(word)
            }
        }
    }
    /*

    fun initGredient(){
        for(i in 1..30){
            Temp_TransferData.add(Gredient(i.toString(),R.drawable.false_pic))
        }
    }
    class Gredient(val name:String, val imageid: Int)
    class GredientAdapter(val GredientList:List<Gredient>): RecyclerView.Adapter<GredientAdapter.ViewHolder>(){
        //定义了一个内部的viewHolder，这个继承了RecyclerView的viewHolder
        inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val gredientImage: ImageView = view.findViewById(R.id.gredientImage)
            val gredientName: TextView = view.findViewById(R.id.gredientName)
        }

        //用于创建viewHolder实例的
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //将fruit_item.xml布局加载进来(R.layout.fruit_item)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gredient_item,parent,false)
            return ViewHolder(view)
        }

        //为RecyclerView的子项赋值，会在当被滚到到屏幕内的子项赋值
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val gredient = GredientList[position]//获得当前项Fruit实例
            //将实例fruit里的数据放置到对应的图片位置和文字位置上
            holder.gredientImage.setImageResource(gredient.imageid)
            holder.gredientName.text = gredient.name
        }
        //告诉一共有多少个子项
        override fun getItemCount() = GredientList.size

    }

     */
}