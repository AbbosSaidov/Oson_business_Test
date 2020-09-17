package app.oson.business.activities

//import android.support.v7.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatImageView
//import androidx.appcompat.widget.AppCompatTextView
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import app.oson.business.R
import app.oson.business.activities.login.LoginActivity
import app.oson.business.api.Api
import app.oson.business.database.Preferences
import app.oson.business.models.BlockTime
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class MyActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var backImageView: AppCompatImageView
    internal lateinit var filterImageView: AppCompatImageView
    internal lateinit var qrCodeImageView: AppCompatImageView
    internal lateinit var exitImageView: AppCompatImageView

    //internal lateinit var infoImageView: AppCompatImageView;
    internal lateinit var titleTextView: AppCompatTextView
    internal lateinit var clearImageView: AppCompatImageView

    lateinit var retrofit: Retrofit
    lateinit var api: Api

    lateinit var preferences: Preferences

     var timerBoolean: Boolean = false
     lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        preferences = Preferences(applicationContext)
    }

    override fun setContentView(layoutResID: Int){
        super.setContentView(layoutResID)

        initActionBar()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()

                if (preferences.getUserData() != null)
                    builder.addHeader("token", preferences.getUserData()!!.token)

                var request = builder.build()

                var response = chain.proceed(request)


                //                    if (response.code() == 401 && preferences.getLoginData() != null) {
                //                        val r = api!!.refreshToken("refresh_token", Api.CLIENT_ID, Api.CLIENT_SECRET, preferences.getUserData()!!.token).execute()
                //
                //                        if (r.code() == 200 && r.body() != null) {
                //                            preferences.saveLoginData(r.body()!!)
                //
                //
                //                            builder.removeHeader("authorization")
                //                            builder.addHeader("authorization", "Bearer " + preferences.getLoginData()!!.accessToken)
                //
                //                            request = builder.build()
                //
                //                            response = chain.proceed(request)
                //                        }
                //                    }


                response
            }
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()


        retrofit = Retrofit.Builder()
            .baseUrl(Api.API_HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }


    fun initActionBar() {

        backImageView = findViewById(R.id.image_view_back)
        filterImageView = findViewById(R.id.image_view_filter)
        exitImageView = findViewById(R.id.image_exit)
        qrCodeImageView = findViewById(R.id.qr_code)
        //  infoImageView = findViewById(R.id.image_view_info)
        titleTextView = findViewById(R.id.text_view_title)
        clearImageView = findViewById(R.id.image_view_clear)


        backImageView.setOnClickListener { view ->
            finish()
        }
        filterImageView.setOnClickListener(this)
        exitImageView.setOnClickListener(this)
        qrCodeImageView.setOnClickListener(this)
        clearImageView.setOnClickListener(this)

        setupActionBar()
    }

    abstract fun setupActionBar()

    @SuppressLint("MissingPermission")
    fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting

    }

    var progressDialog: Dialog? = null
    fun showProgressDialog(){
        if (progressDialog == null){
            progressDialog = Dialog(this)
            progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog?.setCancelable(false)
            progressDialog?.setContentView(ProgressBar(this))
        }
        progressDialog?.show()
    }

    fun cancelProgressDialog(){
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog?.cancel()
    }
    override fun onPause(){
        super.onPause()
        if(!timerBoolean){
            val className=this.localClassName
            if(this.localClassName!= LoginActivity::class.java.name && className!=SplashActivity::class.java.name){
                if(preferences.getBlockTime()!=null && preferences.getBlockTime()!!.timerBoolen){
                    val blocktime = BlockTime()
                    blocktime.timerBoolen=false
                    blocktime.timerBoolen2=true
                    blocktime.time=preferences.getBlockTime()!!.time
                    preferences.saveBlockTime(blocktime)}
                var time:Int = if(preferences.getBlockTime()!=null){
                    preferences.getBlockTime()!!.time
                }else{
                    20
                }
                    object : CountDownTimer((time*1000).toLong(), 1000){
                    override fun onTick(millisUntilFinished: Long){
                        val blocktime = BlockTime()
                        blocktime.timerBoolen=true
                        blocktime.timerBoolen2=true
                        if(preferences.getBlockTime()!=null ){blocktime.time=preferences.getBlockTime()!!.time}else{blocktime.time=20}
                        preferences.saveBlockTime(blocktime)
                    }
                    override fun onFinish(){
                        val blocktime = BlockTime()
                        blocktime.timerBoolen=false
                        blocktime.timerBoolen2=false
                        blocktime.time=preferences.getBlockTime()!!.time
                        preferences.saveBlockTime(blocktime)
                        timerBoolean=true
                    }
                }.start()
            }
        }
    }
    override fun onResume(){
        super.onResume()
        if(timerBoolean && preferences.getBlockTime()!=null && !preferences.getBlockTime()!!.timerBoolen2){
            Log.i("qwer","open Login")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else if(preferences.getBlockTime()!=null && preferences.getBlockTime()!!.timerBoolen){
            Log.i("qwer","canceled")
            val blocktime = BlockTime()
            blocktime.timerBoolen=false
            blocktime.timerBoolen2=true
            blocktime.time=preferences.getBlockTime()!!.time
            preferences.saveBlockTime(blocktime)
        }
        timerBoolean=false
    }
    fun showAlertDialog(title: String, message: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("ОК",
                DialogInterface.OnClickListener {dialog, id -> dialog.cancel()})
        val alert = builder.create()
        alert.show()
    }
}