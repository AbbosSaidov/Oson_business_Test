package app.oson.business.activities.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.AppCompatEditText
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.SplashActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.UserService
import app.oson.business.models.LoginData
import okhttp3.ResponseBody

class ChangePasswordActivity : MyActivity(){
    lateinit var currentPassEditText: AppCompatEditText
    lateinit var newPassEditText: AppCompatEditText
    lateinit var confirmPassEditText: AppCompatEditText
    lateinit var changePassButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        titleTextView.text = resources.getString(R.string.fragment_main_preference_change_password_title)
        initViews()
        disabledButton()

        changePassButton.setOnClickListener(this)
    }

    private fun disabledButton(){
        changePassButton.alpha = .5f
        changePassButton.isEnabled = false


        currentPassEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <= ' '}.isEmpty()){
                    changePassButton.alpha = .5f
                    changePassButton.isEnabled = false
                }else{
                    if(confirmPassEditText.text!!.isNotEmpty() && newPassEditText.text!!.isNotEmpty()){
                        changePassButton.alpha = 1.0f
                        changePassButton.isEnabled = true
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

        newPassEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.length == 0) {
                    changePassButton.alpha = .5f
                    changePassButton.isEnabled = false
                } else {
                    if(currentPassEditText.text!!.isNotEmpty() && confirmPassEditText.text!!.isNotEmpty()){
                        changePassButton.alpha = 1.0f
                        changePassButton.isEnabled = true
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

        confirmPassEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <= ' ' }.isEmpty()){
                    changePassButton.alpha = .5f
                    changePassButton.isEnabled = false
                }else{
                    if(currentPassEditText.text!!.isNotEmpty() && newPassEditText.text!!.isNotEmpty()){
                        changePassButton.alpha = 1.0f
                        changePassButton.isEnabled = true
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }
            override fun afterTextChanged(s: Editable){
                // TODO Auto-generated method stub
            }
        })
        if(currentPassEditText.text!!.isNotEmpty() && newPassEditText.text!!.isNotEmpty() && confirmPassEditText.text!!.isNotEmpty()){
            changePassButton.alpha = 1.0f
            changePassButton.isEnabled = true
        }
    }
    override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?){
        if (v == changePassButton){
            changePassword()
        }
    }

    fun initViews(){
        currentPassEditText = findViewById(R.id.edit_text_current_password)
        newPassEditText = findViewById(R.id.edit_text_new_password)
        confirmPassEditText = findViewById(R.id.edit_text_confirm_password)
        changePassButton = findViewById(R.id.button_change_password)
    }

    fun checkPasswordData(): Boolean {

        if (currentPassEditText.text.toString().isEmpty()) {
            showAlertDialog("Error", resources.getString(R.string.dialogfragment_main_preferences_old_password_empty))
            return false
        }

        if (newPassEditText.text.toString().isEmpty()) {
            showAlertDialog("Error", resources.getString(R.string.dialogfragment_main_preferences_new_password_empty))
            return false
        }

        if(confirmPassEditText.text.toString().isEmpty()){
            showAlertDialog(
                "Error",
                resources.getString(R.string.dialogfragment_main_preferences_confirm_new_password_empty)
            )
            return false
        }

        if (!newPassEditText.text!!.isEmpty()) {
            if (!newPassEditText.text.toString().equals(confirmPassEditText.text.toString())) {
                showAlertDialog("Error", "Yangi parolni tasdiqlang!")

            } else {
                return true
            }
        }

        return true
    }

    var aid: Int? = null
    lateinit var oldPassword: String
    lateinit var newPassword: String
    val loginData = LoginData()

    fun changePassword() {

        if (checkPasswordData()) {
            aid = preferences.getUserData()!!.aid
            oldPassword = currentPassEditText.text.toString()
            newPassword = newPassEditText.text.toString()


            if (preferences.getLoginData() != null) {
                loginData.login = preferences.getLoginData()!!.login
                loginData.password = newPassword
            }

            UserService().changePassword(
                aid = aid,
                oldPassword = oldPassword,
                newPassword = newPassword,

                callback = object : BaseCallback<ResponseBody> {
                    override fun onLoading() {
                        showProgressDialog()
                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                        cancelProgressDialog()
                    }

                    override fun onSuccess(response: ResponseBody) {
                        if (preferences.getLoginData() != null)
                            preferences.saveLoginData(loginData)

                        val intent = Intent(this@ChangePasswordActivity, SplashActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)

                        cancelProgressDialog()
                    }

                }
            )
        }
    }


}