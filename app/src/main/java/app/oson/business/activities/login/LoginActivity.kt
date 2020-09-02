package app.oson.business.activities.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import app.oson.business.R
import app.oson.business.activities.MainActivity
import app.oson.business.activities.MyActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.UserService
import app.oson.business.models.LoginData
import app.oson.business.models.UserData

class LoginActivity : MyActivity(){

    lateinit var loginEditText: AppCompatEditText
    lateinit var passwordEditText: AppCompatEditText
    lateinit var savePasswordLinearLayout: LinearLayout
    lateinit var savePasswordCheckBox: AppCompatCheckBox
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

      //   throw RuntimeException("Test Crash") // Force a crash

        titleTextView.setText(R.string.authorization_title)

        loginEditText = findViewById(R.id.edit_text_login)

        passwordEditText = findViewById(R.id.edit_text_password)
        passwordEditText.transformationMethod = PasswordTransformationMethod()

        loginEditText.setText(preferences.getLoginData()?.login)
        passwordEditText.setText(preferences.getLoginData()?.password)

        savePasswordLinearLayout = findViewById(R.id.linear_layout_save_password)
        savePasswordCheckBox = findViewById(R.id.check_box_save_password)
        loginButton = findViewById(R.id.button_login)
        disabledButton()

    }

    private fun disabledButton(){
        loginButton.alpha = .5f
        loginButton.isEnabled = false

        loginEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <= ' '}.isEmpty()){
                    loginButton.alpha = .5f
                    loginButton.isEnabled = false
                }else{
                    if(passwordEditText.text!!.isNotEmpty()){
                        loginButton.alpha = 1.0f
                        loginButton.isEnabled = true
                    }
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ){
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <=' '}.isEmpty()){
                    loginButton.alpha = .5f
                    loginButton.isEnabled = false
                }else if(loginEditText.text!!.isNotEmpty()){
                        loginButton.alpha = 1.0f
                        loginButton.isEnabled = true
                    }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        if(loginEditText.text!!.isNotEmpty() && passwordEditText.text!!.isNotEmpty()){
            loginButton.alpha = 1.0f
            loginButton.isEnabled = true
        }
        loginButton.setOnClickListener(this)
    }

    override fun setupActionBar(){
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?){
        if (v == loginButton) {
            login()

        } else if (v == savePasswordLinearLayout) {
            savePasswordCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    buttonView: CompoundButton,
                    isChecked: Boolean
                ) {
                    if (isChecked) {
                        savePasswordCheckBox.isChecked = true
                    }
                }
            })
        }
    }

    fun checkLoginData(): Boolean {
        if (loginEditText.text.toString().isEmpty()){
            Toast.makeText(this@LoginActivity, "Loginni kiriting!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(loginEditText.text.toString().isEmpty()){

            Toast.makeText(this@LoginActivity, "Parolni kiriting!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    lateinit var loginData: LoginData

    fun login(){

        if (checkLoginData()) {
            loginData = LoginData()
            loginData.login = loginEditText.text.toString()
            loginData.password = passwordEditText.text.toString()

            UserService().login(
                login = loginData.login,
                password = loginData.password,
                callback = object : BaseCallback<UserData> {
                    override fun onLoading(){
                        showProgressDialog()
                    }

                    override fun onError(throwable: Throwable){
                        throwable.printStackTrace()
                        cancelProgressDialog()
                    }

                    override fun onSuccess(response: UserData){

                        val userData = response
                        Log.i("qwerty", "asd"+response.logged)
                        if(response.logged){
                            preferences.setUserData(userData)

                            if (savePasswordCheckBox.isChecked == true)
                                preferences.saveLoginData(loginData)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)

                            cancelProgressDialog()
                        }else{
                            cancelProgressDialog()
                            Toast.makeText(this@LoginActivity,
                                    "Введен неверный логин или пароль", Toast.LENGTH_SHORT).show()

                        }
                    }
                })
        }
    }

    fun onClickPhone(view: View) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:+998712078080")
        startActivity(intent)
    }


}