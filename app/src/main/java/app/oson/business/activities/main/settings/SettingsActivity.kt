package app.oson.business.activities.main.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.SplashActivity
import app.oson.business.activities.login.ChangePasswordActivity
import app.oson.business.activities.main.purchase.PurchaseItemAdapter
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.api.services.UserService
import app.oson.business.models.Bill
import app.oson.business.models.BlockTime
import app.oson.business.models.Merchant
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody

class SettingsActivity : MyActivity(),PurchaseItemAdapter.ItemClickListener{

    lateinit var changePasswordView: View
    lateinit var languageView: View
    lateinit var viewChangeBlockTime: View
    lateinit var viewNotification: View
    lateinit var viewAskQuestion: View
    lateinit var viewRateApp: View
    lateinit var viewShare: View

    var merchantList: ArrayList<Merchant>? = null
    lateinit var listviewOfBottomSheetAdapter: RecyclerView.Adapter<*>
    lateinit var listviewOfBottomSheetManager: LinearLayoutManager
    lateinit var notificationSwitch: androidx.appcompat.widget.SwitchCompat
    lateinit var dialog: BottomSheetDialog
    lateinit var recyclerView: RecyclerView
    var subsidaryList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        titleTextView.text = resources.getString(R.string.menu_item_main_preference_title)
        initViews()
        subsidaryList = ArrayList<String>()
        subsidaryList!!.add("20 сек")
        subsidaryList!!.add("30 сек")
        subsidaryList!!.add("2 мин")
        subsidaryList!!.add("6 мин")

        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

        viewChangeBlockTime.setOnClickListener(this)
        viewNotification.setOnClickListener(this)
        viewAskQuestion.setOnClickListener(this)
        changePasswordView.setOnClickListener(this)

        languageView.setOnClickListener(this)
        qrCodeImageView.setOnClickListener(this)
        exitImageView.setOnClickListener(this)
        viewRateApp.setOnClickListener(this)
        viewShare.setOnClickListener(this)
    }
    override fun onClick(view: View?){
         if (view == changePasswordView){
            val intent = Intent(this@SettingsActivity, ChangePasswordActivity::class.java)
            startActivity(intent)
       /* }else if (view == logOutView){
            showLogOutDialog()*/
        }else if (view == languageView){
            val intent = Intent(this@SettingsActivity, SetLanguageActivity::class.java)
            startActivity(intent)
        }else if(view==qrCodeImageView){
            billQrCode()
        }else if(view==exitImageView){
            showLogOutDialog()
        }else if(view==viewChangeBlockTime){
             showBlockTimeDialog()
        }else if(view==viewNotification){
             notificationSwitch.isChecked = !notificationSwitch.isChecked
        }else if(view==viewRateApp){
             rateApp()
        }else if(view==viewAskQuestion){
             val emailIntent = Intent(
                 Intent.ACTION_SENDTO, Uri.fromParts(
                     "mailto", "support@oson.uz", null
                 )
             )
             this.startActivity(Intent.createChooser(emailIntent, null))
         }else if(view==viewShare){
             share()
         }
    }
    override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
        qrCodeImageView.visibility=View.VISIBLE
        exitImageView.visibility=View.VISIBLE
    }
    fun initViews(){
       // qrCodeView = findViewById(R.id.view_qr_code)
        changePasswordView = findViewById(R.id.view_change_password)
       // logOutView = findViewById(R.id.view_log_out)
        languageView = findViewById(R.id.view_set_language)
        viewChangeBlockTime = findViewById(R.id.view_change_block_time)
        viewAskQuestion = findViewById(R.id.view_ask_question)
        notificationSwitch = findViewById(R.id.notSwitch)
        viewNotification = findViewById(R.id.view_notification)
        viewRateApp = findViewById(R.id.view_rate_app)
        viewShare = findViewById(R.id.view_share)
    }
    fun share(){
        val sendIntent: Intent = Intent().apply{
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://app.oson.uzcom.oson")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    fun rateApp(){
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.oson.business&hl=ru")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Impossible to find an application for the market", Toast.LENGTH_LONG).show()
        }
    }
    fun showLogOutDialog(){
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
    fun showQrCodeDialog(){

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_qr_code)

        val spinner = dialog.findViewById<Spinner>(R.id.spinner)
        val arrayList = ArrayList<String>()
        for(i in merchantList!!.indices){
            arrayList.add(merchantList!![i].name)
        }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter

        val imageView = dialog.findViewById<ImageView>(R.id.image_view_qr_code)

        if (bill.qrCodeBase!!.isNotEmpty()){
            val bytes: ByteArray = Base64.decode(bill.qrCodeBase, Base64.DEFAULT)
            val decodeByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageBitmap(decodeByte)
        }

        val okTextView = dialog.findViewById<AppCompatTextView>(R.id.text_view_ok)

        okTextView.setOnClickListener{ view ->
            dialog.cancel()
        }

        dialog.show()
    }
    var token: String? = null
    fun logOut(){
        token = preferences.getUserData()!!.token
        UserService().logOut(
            token = token,
            callback = object : BaseCallback<ResponseBody> {
                override fun onLoading() {
                    showProgressDialog()
                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                    cancelProgressDialog()
                }

                override fun onSuccess(response: ResponseBody) {
                    preferences.saveLoginData(null)
                    val intent = Intent(this@SettingsActivity, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                    cancelProgressDialog()
                }
            }
        )
    }
    fun showBlockTimeDialog(){
        dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet)

        recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)!!

        listviewOfBottomSheetManager = LinearLayoutManager(this)
        recyclerView.layoutManager =listviewOfBottomSheetManager
        listviewOfBottomSheetAdapter= PurchaseItemAdapter(this, subsidaryList!!)
        (listviewOfBottomSheetAdapter as PurchaseItemAdapter).setClickListener(this)
        recyclerView.adapter =listviewOfBottomSheetAdapter
        dialog.show()
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
    override fun onItemClick(position: Int){
        val blocktime = BlockTime()
        blocktime.timerBoolen=false
        blocktime.timerBoolen2=true
        when (position){
            0 -> {
                blocktime.time = 20
            }
            1 -> {
                blocktime.time = 30
            }
            2 -> {
                blocktime.time = 120
            }
            3 -> {
                blocktime.time = 360
            }
        }
        dialog.hide()
        preferences.saveBlockTime(blocktime)
    }
}