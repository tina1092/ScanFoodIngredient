package com.example.healthypicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {
    val fromAlbum = 2
    var haveImage = false
    lateinit var text_data : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //提取照片并导入到app
        val albumbutton: Button = findViewById(R.id.photoButton)
        text_data = findViewById(R.id.text_data)


        albumbutton.setOnClickListener{
            //打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只选择图片
            intent.type = "image/*"
            startActivityForResult(intent,fromAlbum)
        }

        //创建数据库
        val dbHelper = MyDatabaseHelpler(this,"UnhealthyGredient.db",2)

        //加载数据库
        readDataBaseInfo(dbHelper)

        //开始分析图片数据
        val analyzeBut:Button=findViewById(R.id.analyzeButton)
        analyzeBut.setOnClickListener{
            var gredientList: ArrayList<Gredient>? = null
            gredientList = checkContent(dbHelper)
            if(haveImage == false){
                Toast.makeText(this,"Please upload image",Toast.LENGTH_SHORT).show()
            }else if (gredientList.size == 0){//如果没有有害成分，跳转到nounhealthyfood页面
                val intent2 = Intent(this, NoUnhealtyFood::class.java)
                if(text_data!=null)text_data.setText(null)
                val image:ImageView = findViewById(R.id.imageview)
                image.setImageResource(0)
                haveImage = false
                startActivity(intent2)
            }else{ //如果含有有害成分，则跳转到ContainUnhealthyFood页面
                //Toast.makeText(this,"you click analyze button",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,ContainUnhealtyFood::class.java)
                val pack = ingrePack()
                pack.ingreList(gredientList)
                intent.putExtra("gredientList",pack)
                val image:ImageView = findViewById(R.id.imageview)
                image.setImageResource(0)
                if(text_data!=null)text_data.setText(null)
                haveImage = false
                startActivity(intent)
            }

/*
            if(checkContain){
                //如果含有有害成分，则跳转到ContainUnhealthyFood页面
                Toast.makeText(this,"you click analyze button",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,ContainUnhealtyFood::class.java)
                startActivity(intent)
            }else {
                //如果没有有害成分，跳转到nounhealthyfood页面
                val intent2 = Intent(this, NoUnhealtyFood::class.java)
                startActivity(intent2)
            }

 */
        }

    }
    //查询有害成分
    fun checkContent(dbHelper: MyDatabaseHelpler):ArrayList<Gredient>{
        text_data = findViewById(R.id.text_data)
        val SearchedText = text_data.text.toString()
        val StringList = stringToWords(SearchedText)

        val db = dbHelper.writableDatabase
        //val stringBuilder = StringBuilder()

        val gredientList = ArrayList<Gredient>()


        for(word in StringList){
            val cursor = db.rawQuery("select id from Ingredient where id = ?", arrayOf(word))
            if(cursor.getCount() > 0){
                //if(word == "")
                cursor.moveToFirst()
                @SuppressLint("Range") val id = cursor.getString(cursor.getColumnIndex("id"))
                Log.d("checkQuery","for $word :  find word")
               // stringBuilder.append(word+" ")
                gredientList.add(Gredient(word,R.drawable.false_pic))
            }else{
                Log.d("checkQuery","for $word : no find word")
            }

        }
        return gredientList;
    }

    //句子转为list
    fun stringToWords(s : String): MutableSet<String> {
        val list = mutableSetOf("")
        var i = 0
        var temp = ""
        while (i < s.length) {

            if (s.get(i) == '\n') {
                temp = temp+" "
                i ++
                temp = ""
            }else if(s.get(i) == ','||s.get(i)==':'||s.get(i) == '('||s.get(i) == ')'||s.get(i) == '.'||s.get(i) == ';'){
                if(temp.length > 0&&temp.get(temp.length-1) == ' '){
                    temp = temp.substring(0,temp.length-1)
                }
                list.add(temp.lowercase(Locale.getDefault()))
                i++
                temp=""
            }else if(s.get(i) == ' '&& temp.length == 0){
                i++
            }else{
                temp = temp+s.get(i)
                i++
            }
        }
        return list;
    }

    //图片转换文字
    private fun getTextFromImage( bitmap: Bitmap){
        val recognizer = TextRecognizer.Builder(this).build()
        if(!recognizer.isOperational){
            Toast.makeText(this,"Error in recognize",Toast.LENGTH_SHORT).show()
        }else{
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlockSparseArray = recognizer.detect(frame)//SparseArray<Tex>
            val stringBuilder = StringBuilder()
            for(i in 0 until textBlockSparseArray.size()){
                val textBlock = textBlockSparseArray.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append(" ")
            }
            text_data.setText(stringBuilder.toString())
            //Log.d("checkForText",text_data.text.toString())
        }

    }


    //图片相关信息
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            fromAlbum ->{
                if(resultCode == Activity.RESULT_OK && data!=null){
                    data.data?.let{uri ->
                        //将选择的图片显示
                        val bitmap = getBitmapFromUri(uri)

                        bitmap?.let {
                            haveImage = true
                            getTextFromImage(bitmap)
                        }
                        val imageView: ImageView = findViewById(R.id.imageview)
                        imageView.setImageBitmap(bitmap)
                    }
                }
            }

        }

    }
    //图片相关信息
    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri,"r")?.use{
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    //加载数据库
    fun readDataBaseInfo(dbHelper:MyDatabaseHelpler) {
        val inputStream: InputStream = assets.open("data.txt")
        val db = dbHelper.writableDatabase
        db.delete("Ingredient",null,null)

        try {
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                while (reader.ready()) {
                    val line: String = reader.readLine()
                    insertData(line,dbHelper)
                }
            }
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
    private fun insertData(data:String,dbHelper: MyDatabaseHelpler){
        val db = dbHelper.writableDatabase
        val value =  ContentValues()
        value.put("id",data)
        val ret = db.insert("Ingredient",null,value)
    }
}


//数据库相关信息
class MyDatabaseHelpler(val context: Context, name:String, version:Int):
    SQLiteOpenHelper(context,name,null,version){
    //SQL建表定义为一个字符串
    private val createBook = "create table Ingredient("+
            "id text primary key)"

    override fun onCreate(db: SQLiteDatabase?) {
        //执行组件sql表格

        if (db != null) {
            db.execSQL(createBook)
        }
        Log.d("SQL_ingredient","create success")
        /*
        val db1 = this.writableDatabase
        Toast.makeText(context,"I can run this part",Toast.LENGTH_SHORT).show()
        val value = ContentValues().apply{
            put("name","代可可脂")
        }
        val ret = db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)
        db1.insert("Ingredient",null,value)

        if(-1 != ret.toInt()){
            Log.d("SQL_ingredient","add success")
        }else{
            Log.d("SQL_ingredient","add failed")
        }
        */

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("drop table if exists Ingredient")
        }
        onCreate(db)
    }


}