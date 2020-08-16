package app.oson.business.activities.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.View
import app.oson.business.R
import app.oson.business.activities.MyActivity

class SetLanguage : MyActivity() {

    lateinit var russianView: View
    lateinit var uzbekView: View
    lateinit var englishView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

        titleTextView.setText(resources.getString(R.string.fragment_main_preference_change_language_title))
        initViews()

        russianView.setOnClickListener(this)
        uzbekView.setOnClickListener(this)
        englishView.setOnClickListener(this)
    }

    override fun setupActionBar() {
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        Log.i("qwerty","asdasd")
        val ds : AppCompatImageView  = findViewById(R.id.english_chek)
        val ds2 : AppCompatImageView  = findViewById(R.id.uzbek_chek)
        val ds3 : AppCompatImageView  = findViewById(R.id.russian_chek)

        if (v == russianView) {

            ds.visibility=View.INVISIBLE
            ds2.visibility=View.INVISIBLE
            ds3.visibility=View.VISIBLE
        } else if (v == uzbekView) {
            ds.visibility=View.INVISIBLE
            ds2.visibility=View.VISIBLE
            ds3.visibility=View.INVISIBLE
        } else if (v == englishView) {

            ds.visibility=View.VISIBLE
            ds2.visibility=View.INVISIBLE
            ds3.visibility=View.INVISIBLE
        }
    }
    fun initViews() {
        russianView = findViewById(R.id.view_russian_lang)
        uzbekView = findViewById(R.id.view_uzbek_lang)
        englishView = findViewById(R.id.view_english_lang)
    }
}