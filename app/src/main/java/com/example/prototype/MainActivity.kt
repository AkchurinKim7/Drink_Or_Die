package com.example.prototype

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mDBHelper: DatabaseHelper? = null
    private var mDb: SQLiteDatabase? = null

    var players: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        mDBHelper = DatabaseHelper(this)
        try {
            mDBHelper!!.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToUpdateDatabase")
        }
        try {
            mDb = mDBHelper!!.getWritableDatabase()
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }
    }

    fun click(view: View) {
        var product = ""
        val cursor = mDb!!.rawQuery("SELECT * FROM Question where _id =" + (1..31).random() + "", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            product += cursor.getString(1)
            cursor.moveToNext()
        }
        cursor.close()
        var size = players.size - 1
        findViewById<TextView>(R.id.player).setText(players.get((0..size).random()).toString());
        findViewById<TextView>(R.id.quest).setText(product);
    }

    fun start(view: View){
        if(players.size > 1) {
            setContentView(R.layout.game)
        }
        else{
            Toast.makeText(this, "Минимум 2 игрока", Toast.LENGTH_SHORT).show()
        }
    }

    fun add(view: View){
        players.add(findViewById<EditText>(R.id.person).getText().toString())
        Toast.makeText(this, "Игрок " + findViewById<EditText>(R.id.person).getText().toString() + " добавлен", Toast.LENGTH_SHORT).show()
        findViewById<EditText>(R.id.person).setText("")
    }
}