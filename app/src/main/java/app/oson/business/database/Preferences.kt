package app.oson.business.database

import android.content.Context
import android.content.SharedPreferences
import app.oson.business.App
import app.oson.business.models.LoginData
import app.oson.business.models.UserData
import com.google.gson.Gson

class Preferences(context: Context) {


    private val LOGIN_DATA = "login_data";
    private val SAVE_LOGIN = "save_login"
    var context: Context
    internal var sharedPreferences: SharedPreferences

    init {
        this.context = context
        sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
    }


    val prefs = sharedPreferences;

    fun setUserData(userData: UserData) {
        val str = Gson().toJson(userData);
        prefs.edit().putString(LOGIN_DATA, str).commit();
    }

    fun getUserData(): UserData? {
        val str = prefs.getString(LOGIN_DATA, null);

        if (str != null) {
            val l = Gson().fromJson(str, UserData::class.java);
            return l;
        } else
            return null;

    }

    fun saveLoginData(loginData: LoginData?) {
        if (loginData != null) {
            val str = Gson().toJson(loginData);
            prefs.edit().putString(SAVE_LOGIN, str).commit();
        } else{
            prefs.edit().remove(SAVE_LOGIN).commit();
        }
    }

    fun getLoginData(): LoginData? {

        val str = prefs.getString(SAVE_LOGIN, null);

        if (str != null) {
            val l = Gson().fromJson(str, LoginData::class.java);
            return l;
        } else
            return null;
    }

    companion object {
        fun getInstance(): Preferences {
            return Preferences(App.context)
        }
    }
}