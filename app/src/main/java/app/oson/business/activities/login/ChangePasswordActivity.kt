package app.oson.business.activities.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.widget.Toast
import app.oson.business.R
import app.oson.business.activities.MainActivity
import app.oson.business.activities.MyActivity
import app.oson.business.activities.SplashActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BaseRestService
import app.oson.business.api.services.UserService
import app.oson.business.models.LoginData
import okhttp3.ResponseBody

class ChangePasswordActivity : MyActivity() {
    lateinit var currentPassEditText: AppCompatEditText
    lateinit var newPassEditText: AppCompatEditText
    lateinit var confirmPassEditText: AppCompatEditText
    lateinit var changePassButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        titleTextView.setText(resources.getString(R.string.fragment_main_preference_change_password_title))
        initViews()

        changePassButton.setOnClickListener(this)

    }

    override fun setupActionBar() {
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        if (v == changePassButton) {
            changePassword()
        }
    }

    fun initViews() {
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

        if (confirmPassEditText.text.toString().isEmpty()) {
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
    val loginData = LoginData();

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
                        cancelProgressDialog();
                    }

                    override fun onSuccess(response: ResponseBody) {
                        if (preferences.getLoginData() != null)
                            preferences.saveLoginData(loginData)

                        val intent = Intent(this@ChangePasswordActivity, SplashActivity::class.java);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        cancelProgressDialog()
                    }

                }
            )
        }
    }


}