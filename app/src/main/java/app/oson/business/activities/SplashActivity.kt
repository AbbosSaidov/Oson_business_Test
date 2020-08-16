package app.oson.business.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import app.oson.business.R
import app.oson.business.activities.login.LoginActivity
import app.oson.business.models.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : MyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            login()

        }, 1500);

    }

    override fun onClick(v: View?) {

    }

    override fun setupActionBar() {

    }

    fun login() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
       /* if (preferences.getLoginData() != null) {
            api.login(preferences.getLoginData()!!.login, preferences.getLoginData()!!.password).enqueue(
                object : Callback<UserData> {
                    override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                        if (response.isSuccessful && response.body() != null) {
                            preferences.setUserData(response.body()!!)
                            val intent = Intent(this@SplashActivity, MainActivity::class.java);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            println("ERROR")
                        }

                        cancelProgressDialog();
                    }

                    override fun onFailure(call: Call<UserData>, t: Throwable) {
                        println(t.message)
                        cancelProgressDialog();
                    }
                });
        } else{
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        }*/
    }


}