package app.oson.business.activities.login

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import app.oson.business.R
import app.oson.business.activities.MyActivity
import java.util.*


class SetLanguage : MyActivity() {

    lateinit var russianView: View
    lateinit var uzbekView: View
    lateinit var englishView: View
    lateinit var ds : AppCompatImageView
    lateinit var ds2 : AppCompatImageView
    lateinit var ds3 : AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_language)

         ds  = findViewById(R.id.english_chek)
         ds2  = findViewById(R.id.uzbek_chek)
         ds3  = findViewById(R.id.russian_chek)

        titleTextView.setText(resources.getString(R.string.fragment_main_preference_change_language_title))
        initViews()

        russianView.setOnClickListener(this)
        uzbekView.setOnClickListener(this)
        englishView.setOnClickListener(this)
        if(preferences.getLangData()==""||preferences.getLangData()==null){
            setVisible("rus")
        }else{
            setVisible(preferences.getLangData())
        }

    }

    override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        Log.i("qwerty","asdasd")

        if (v == russianView) {
            setLocale("")
            setVisible("rus")
            preferences.saveLangData("rus")
        } else if (v == uzbekView) {
            setLocale("uzs")
            setVisible("uzs")
            preferences.saveLangData("uzs")
        } else if (v == englishView) {
            setLocale("eng")
            setVisible("eng")
            preferences.saveLangData("eng")

        }
    }

    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)

        //val refresh = Intent(this, SetLanguage::class.java)
        // startActivity(refresh)
        finish()
    }
    fun setVisible(lang:String?){
        if (lang == "rus") {
            ds.visibility=View.INVISIBLE
            ds2.visibility=View.INVISIBLE
            ds3.visibility=View.VISIBLE
        } else if (lang == "uzs") {
            ds.visibility=View.INVISIBLE
            ds2.visibility=View.VISIBLE
            ds3.visibility=View.INVISIBLE
        } else if (lang == "eng") {
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