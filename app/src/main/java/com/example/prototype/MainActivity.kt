package com.example.prototype

import android.content.ContentValues
import android.content.Intent
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnClickListener {
    var layout = 0;

    private var mDBHelper: DatabaseHelper? = null
    private var mDb: SQLiteDatabase? = null

    var players: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        layout = 1;
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

        val scanBtn = findViewById<Button>(R.id.scanBtn)
        val vkBtn = findViewById<ImageButton>(R.id.vkBtn)

        scanBtn.setOnClickListener(this)

        vkBtn.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vk.com/club200233275")
            )
            startActivity(browserIntent)
        })
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when(layout){
                1 -> android.os.Process.killProcess(android.os.Process.myPid())
                2 -> {setContentView(R.layout.start)
                    layout = 1}
                3 -> {setContentView(R.layout.start)
                    layout = 1}
            }
        }
        return true
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
            layout = 2
        }
        else{
            Toast.makeText(this, "Минимум 2 игрока", Toast.LENGTH_SHORT).show()
        }
    }

    fun add(view: View){
        players.add(findViewById<EditText>(R.id.person).getText().toString())
        Toast.makeText(
                this,
                "Игрок " + findViewById<EditText>(R.id.person).getText().toString() + " добавлен",
                Toast.LENGTH_SHORT
        ).show()
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
        layout = 3
        setContentView(R.layout.question)
    }

    override fun onClick(v: View?) {
        scanCode()
    }

    fun scanCode() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = CaptureAct::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scanning Code")
        integrator.initiateScan()
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        val result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val builder =
                        AlertDialog.Builder(this)
                builder.setMessage(result.contents)
                builder.setTitle("Scanning Result")
                builder.setPositiveButton(
                        "Scan Again"
                ) { dialog, which -> scanCode() }.setNegativeButton(
                        "Finish"
                ) { dialog, which -> finish() }
                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

