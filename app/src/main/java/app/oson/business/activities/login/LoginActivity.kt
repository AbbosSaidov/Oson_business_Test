package app.oson.business.activities .login

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import app.oson.business.R
import app.oson.business.activities.MainActivity
import app.oson.business.activities.MyActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.UserService
import app.oson.business.models.LoginData
import app.oson.business.models.UserData


class LoginActivity : MyActivity() {

    lateinit var loginEditText: AppCompatEditText
    lateinit var passwordEditText: AppCompatEditText
    lateinit var savePasswordLinearLayout: LinearLayout
    lateinit var savePasswordCheckBox: AppCompatCheckBox
    lateinit var loginButton: Button


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        titleTextView.setText(R.string.authorization_title)

        loginEditText = findViewById(R.id.edit_text_login)

        loginEditText.onRightDrawableClicked { it.text.clear()}


        passwordEditText = findViewById(R.id.edit_text_password)
        passwordEditText.transformationMethod = PasswordTransformationMethod()
        passwordEditText.onRightDrawableClicked {
            Log.i("qwerty","werwer="+it.transformationMethod)
            if(it.transformationMethod == null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    it.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible_off, 0)
                }
                it.transformationMethod = PasswordTransformationMethod()
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    it.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_visible, 0)
                }
                it.transformationMethod = null
            }
        }

        loginEditText.setText(preferences.getLoginData()?.login)
        passwordEditText.setText(preferences.getLoginData()?.password)




        savePasswordLinearLayout = findViewById(R.id.linear_layout_save_password)
        savePasswordCheckBox = findViewById(R.id.check_box_save_password)
        loginButton = findViewById(R.id.button_login)
        disabledButton()
        if(loginEditText.text!!.isNotEmpty() && passwordEditText.text!!.isNotEmpty()){
            loginButton.setAlpha(1.0f)
            loginButton.setEnabled(true)
        }
        loginButton.setOnClickListener(this)
    }

    fun disabledButton(){
        loginButton.setAlpha(.5f)
        loginButton.setEnabled(false)

        var lBoolean =false
        var pBoolean =false

        loginEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.length == 0) {
                    loginButton.setAlpha(.5f)
                    loginButton.setEnabled(false)
                } else {
                    lBoolean=true
                    if(lBoolean && pBoolean){
                        loginButton.setAlpha(1.0f)
                        loginButton.setEnabled(true)
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ){

            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        //
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.length == 0) {
                    loginButton.setAlpha(.5f)
                    loginButton.setEnabled(false)
                } else {
                    pBoolean=true
                    if(lBoolean && pBoolean){
                        loginButton.setAlpha(1.0f)
                        loginButton.setEnabled(true)
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    override fun setupActionBar() {
        titleTextView.visibility = View.VISIBLE
    }
     fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }
    override fun onClick(v: View?) {
        if (v == loginButton) {
            login()

        } else if (v == savePasswordLinearLayout) {
            savePasswordCheckBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    buttonView: CompoundButton,
                    isChecked: Boolean
                ) {
                    if (isChecked) {
                        savePasswordCheckBox.setChecked(true)
                    }
                }
            })
        }
    }

    fun checkLoginData(): Boolean {
        if (loginEditText.text.toString().isEmpty()) {
            Toast.makeText(this@LoginActivity, "Loginni kiriting!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (loginEditText.text.toString().isEmpty()){

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
                    override fun onLoading() {
                        showProgressDialog()
                    }

                    override fun onError(throwable: Throwable){
                        throwable.printStackTrace()
                        cancelProgressDialog();
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