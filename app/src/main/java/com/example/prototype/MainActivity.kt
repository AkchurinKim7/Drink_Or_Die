package com.example.prototype

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mDBHelper: DatabaseHelper? = null
    private var mDb: SQLiteDatabase? = null

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
        findViewById<TextView>(R.id.textView).setText(product);
    }

    fun start(view: View){
        setContentView(R.layout.game)
    }
}