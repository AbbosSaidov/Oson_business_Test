package app.oson.business.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import app.oson.business.R
import app.oson.business.activities.login.ChangePasswordActivity
import app.oson.business.activities.login.SetLanguage
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.api.services.UserService
import app.oson.business.models.Bill
import app.oson.business.models.LoginData
import app.oson.business.models.Merchant
import net.glxn.qrgen.android.QRCode
import okhttp3.ResponseBody
import java.lang.Exception

class SettingsActivity : MyActivity() {

    lateinit var qrCodeView: View
    lateinit var changePasswordView: View
    lateinit var logOutView: View
    lateinit var languageView: View

    var merchantList: ArrayList<Merchant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        titleTextView.setText(resources.getString(R.string.menu_item_main_preference_title))
        initViews()

        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

        qrCodeView.setOnClickListener(this)
        changePasswordView.setOnClickListener(this)
        logOutView.setOnClickListener(this)
        languageView.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        if (view == qrCodeView) {
            billQrCode()
        } else if (view == changePasswordView) {
            val intent = Intent(this@SettingsActivity, ChangePasswordActivity::class.java)
            startActivity(intent)

        } else if (view == logOutView) {
            showLogOutDialog()
        }else if (view == languageView) {
            val intent = Intent(this@SettingsActivity, SetLanguage::class.java)
            startActivity(intent)
        }
    }

    override fun setupActionBar() {
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    fun initViews() {
        qrCodeView = findViewById(R.id.view_qr_code)
        changePasswordView = findViewById(R.id.view_change_password)
        logOutView = findViewById(R.id.view_log_out)
        languageView = findViewById(R.id.view_set_language)
    }

    fun showLogOutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.fragment_main_preference_logout_title))
            .setMessage(resources.getString(R.string.dialogfragment_main_preferences_logout_message))
            .setCancelable(false)
            .setPositiveButton("ОК",
                DialogInterface.OnClickListener { dialog, id -> logOut() })
            .setNegativeButton("CANCEL",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }


    fun showQrCodeDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_qr_code)

        var spinner = dialog.findViewById<Spinner>(R.id.spinner)
        val arrayList = ArrayList<String>()
        for (i in merchantList!!.indices) {
            arrayList.add(merchantList!![i].name)
        }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter

        var imageView = dialog.findViewById<ImageView>(R.id.image_view_qr_code)



        if (bill.qrCodeBase!!.isNotEmpty()) {
            val bytes: ByteArray = Base64.decode(bill.qrCodeBase, Base64.DEFAULT)
            val decodeByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//            val bitmap = QRCode.from(decodeByte.toString()).bitmap()
            imageView.setImageBitmap(decodeByte)


        }

        var okTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_ok)

        okTextView.setOnClickListener { view ->


            dialog.cancel()

        }

        dialog.show()
    }

    var token: String? = null
    fun logOut() {

        token = preferences.getUserData()!!.token
        UserService().logOut(
            token = token,
            callback = object : BaseCallback<ResponseBody> {
                override fun onLoading() {
                    showProgressDialog()
                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                    cancelProgressDialog();
                }

                override fun onSuccess(response: ResponseBody) {
                    preferences.saveLoginData(null)

                    val intent = Intent(this@SettingsActivity, SplashActivity::class.java);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    cancelProgressDialog()
                }

            }
        )
    }


    var bill = Bill()
    fun billQrCode() {
        BillService().putBillQrCode(
            callback = object : BaseCallback<Bill> {
                override fun onLoading() {
                    showProgressDialog()
                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()

                    cancelProgressDialog()
                }

                override fun onSuccess(response: Bill) {
                    bill = response

                    showQrCodeDialog()

                    cancelProgressDialog()
                }

            }
        )
    }


}