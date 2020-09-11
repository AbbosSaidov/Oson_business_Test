package app.oson.business.database

import android.content.Context
import android.content.SharedPreferences
import app.oson.business.App
import app.oson.business.models.BlockTime
import app.oson.business.models.LoginData
import app.oson.business.models.UserData
import com.google.gson.Gson

class Preferences(context: Context){

    private val LOGIN_DATA = "login_data";
    private val SAVE_LOGIN = "save_login"
    private val SAVE_BockTime = "save_block_time"
    private val Language_data = "language_data"
    var context: Context
    internal var sharedPreferences: SharedPreferences

    init {
        this.context = context
        sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
    }

    val prefs = sharedPreferences

    fun setUserData(userData: UserData){
        val str = Gson().toJson(userData)
        prefs.edit().putString(LOGIN_DATA, str).commit();
    }

    fun getUserData(): UserData?{
        val str = prefs.getString(LOGIN_DATA, null)

        return if (str != null) {
            Gson().fromJson(str, UserData::class.java)
        } else
            null
    }

    fun saveLoginData(loginData: LoginData?) {
        if (loginData != null){
            val str = Gson().toJson(loginData);
            prefs.edit().putString(SAVE_LOGIN, str).commit();
        } else{
            prefs.edit().remove(SAVE_LOGIN).commit();
        }
    }

    fun getLoginData(): LoginData? {
        val str = prefs.getString(SAVE_LOGIN, null);

        if (str != null) {
            val l = Gson().fromJson(str, LoginData::class.java)
            return l
        } else
            return null
    }
    fun saveBlockTime(blockTime: BlockTime?){
        if(blockTime != null){
            val str = Gson().toJson(blockTime);
            prefs.edit().putString(SAVE_BockTime, str).commit();
        }else{
            prefs.edit().remove(SAVE_BockTime).commit();
        }
    }

    fun getBlockTime(): BlockTime? {
        val str = prefs.getString(SAVE_BockTime, null);

        if (str != null){
            val l = Gson().fromJson(str, BlockTime::class.java);
            return l
        } else
            return null
    }

    fun saveLangData(langData: String?){
        if (langData != null){
            prefs.edit().putString(Language_data, langData).commit()
        } else{
            prefs.edit().remove(Language_data).commit()
        }
    }

    fun getLangData(): String? {
        val str = prefs.getString(Language_data, null);

        if (str != null) {
            return str
        } else
            return null
    }

    companion object {
        fun getInstance(): Preferences {
            return Preferences(App.context)
        }
    }
}