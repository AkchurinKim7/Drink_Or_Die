package com.example.prototype

import android.content.ContentValues
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
        var col = 0;
        var cursor = mDb!!.rawQuery("SELECT Count(*) FROM Question", null)
        cursor.moveToFirst()
        col = cursor.getInt(0)
        cursor.close()
        var product = ""
        cursor = mDb!!.rawQuery("SELECT * FROM Question where _id =" + (1..col).random() + "", null)
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

    fun newQuestion(view: View){
        if(!findViewById<EditText>(R.id.question).getText().toString().equals("")) {
            val database: SQLiteDatabase = mDBHelper!!.getWritableDatabase()
            val contentValues = ContentValues()
            var col = 0;
            val cursor = mDb!!.rawQuery("SELECT Count(*) FROM Question", null)
            cursor.moveToFirst()
            col = cursor.getInt(0)
            cursor.close()
            contentValues.put("_id", col + 1)
            contentValues.put("text", findViewById<EditText>(R.id.question).getText().toString())
            database.insert("Question", null, contentValues)
            findViewById<TextView>(R.id.question).setText("")
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
        }

        else{
            Toast.makeText(this, "Пустое поле", Toast.LENGTH_SHORT).show()
        }
    }

    fun winQuest(view: View){
        setContentView(R.layout.question)
    }
}