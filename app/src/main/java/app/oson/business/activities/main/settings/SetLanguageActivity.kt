package app.oson.business.activities.main.settings

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
//import androidx.appcompat.widget.AppCompatImageView
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.main.history.MainActivity
import java.util.*


class SetLanguageActivity : MyActivity(){

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

        titleTextView.text = resources.getString(R.string.fragment_main_preference_change_language_title)
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

    override fun onClick(v: View?){
        Log.i("qwerty","asdasd")

        if (v == russianView){
            setLocale("")
            setVisible("rus")
            preferences.saveLangData("rus")
        } else if (v == uzbekView) {
            setLocale("uzs")
            setVisible("uzs")
            preferences.saveLangData("uzs")
        }else if (v == englishView) {
            setLocale("eng")
            setVisible("eng")
            preferences.saveLangData("eng")
        }
    }
    fun setLocale(lang: String?){
        val locale2 = Locale(lang)
        Locale.setDefault(locale2)

        val config2 = Configuration()
        config2.locale = locale2

        baseContext.resources.updateConfiguration(
            config2, baseContext.resources.displayMetrics
        )

        finish()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
    fun setVisible(lang:String?){
        if (lang == "rus"){
            ds.visibility=View.INVISIBLE
            ds2.visibility=View.INVISIBLE
            ds3.visibility=View.VISIBLE
        } else if (lang == "uzs"){
            ds.visibility=View.INVISIBLE
            ds2.visibility=View.VISIBLE
            ds3.visibility=View.INVISIBLE
        } else if(lang == "eng"){
            ds.visibility=View.VISIBLE
            ds2.visibility=View.INVISIBLE
            ds3.visibility=View.INVISIBLE
        }
    }

    fun initViews(){
        russianView = findViewById(R.id.view_russian_lang)
        uzbekView = findViewById(R.id.view_uzbek_lang)
        englishView = findViewById(R.id.view_english_lang)
    }
}