package com.italkutalk.lab17

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button
    //定義資料結構存放 Server 回傳的資料
    data class MyObject (
        var id:String,
        var name: String
    )




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        btn_query.setOnClickListener {
            //關閉按鈕避免再次查詢
            btn_query.isEnabled = false
            //發送請求
            sendRequest()
        }
    }
    //發送請求
    private fun sendRequest() {

        val url = "https://jsonplaceholder.typicode.com/comments?postId=1"

        //建立 Request.Builder 物件，藉由 url()將網址傳入，再建立 Request 物件
        val req = Request.Builder()
            .url(url)
            .build()
        //建立 OkHttpClient 物件，藉由 newCall()發送請求，並在 enqueue()接收回傳
        OkHttpClient().newCall(req).enqueue(object : Callback  {
            //發送成功執行此方法
            override fun onResponse(call: Call, response: Response) {


                //使用 response.body?.string()取得 JSON 字串
                val json = response.body?.string()

                val parser = JsonParser()
                val JsonArray = parser.parse(json).asJsonArray

                val gson = Gson()
                val userList = mutableListOf<MyObject>()
                for (user in JsonArray) {
                    val myObject = gson.fromJson(user, MyObject::class.java)
                    userList.add(myObject)
                }
                println(userList)
                val items = arrayOfNulls<String>(userList.size)
                userList.forEachIndexed { index, data ->
                    items[index] = "Id：${data.id}, 名稱：${data.name}"
                }
                runOnUiThread {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("台北捷運")
                        .setItems(items, null)
                        .show()
                }
            }
            //發送失敗執行此方法
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    //開啟按鈕可再次查詢
                    btn_query.isEnabled = true
                    Toast.makeText(this@MainActivity,
                        "查詢失敗$e", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}