package app.oson.business.activities.main.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.SplashActivity
import app.oson.business.activities.login.ChangePasswordActivity
import app.oson.business.activities.main.purchase.PurchaseItemAdapter
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.api.services.UserService
import app.oson.business.database.Preferences
import app.oson.business.models.Bill
import app.oson.business.models.BlockTime
import app.oson.business.models.Merchant
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.ResponseBody

class SettingsActivity : Fragment(),PurchaseItemAdapter.ItemClickListener{

    lateinit var changePasswordView: View
    lateinit var languageView: View
    lateinit var viewChangeBlockTime: View
    lateinit var viewNotification: View
    lateinit var viewAskQuestion: View
    lateinit var viewRateApp: View


    var merchantList: ArrayList<Merchant>? = null
    lateinit var listviewOfBottomSheetAdapter: RecyclerView.Adapter<*>
    lateinit var listviewOfBottomSheetManager: LinearLayoutManager
    lateinit var notificationSwitch: androidx.appcompat.widget.SwitchCompat
    lateinit var dialog: BottomSheetDialog
    lateinit var recyclerView: RecyclerView
    var subsidaryList: ArrayList<String>? = null
    lateinit var preferences: Preferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        val view = inflater.inflate(R.layout.activity_settings, container, false)
        preferences = Preferences(context!!)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        //initialize your view here for use view.findViewById("your view id")


        subsidaryList = ArrayList<String>()
        subsidaryList!!.add("20 сек")
        subsidaryList!!.add("30 сек")
        subsidaryList!!.add("2 мин")
        subsidaryList!!.add("6 мин")

        //merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

        viewChangeBlockTime.setOnClickListener {onClick(viewChangeBlockTime) }
        viewNotification.setOnClickListener{onClick(viewNotification) }
        viewAskQuestion.setOnClickListener{onClick(viewAskQuestion) }
        changePasswordView.setOnClickListener{onClick(changePasswordView) }

        languageView.setOnClickListener{onClick(languageView) }

        viewRateApp.setOnClickListener{onClick(viewRateApp) }
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_settings)
       // titleTextView.text = resources.getString(R.string.menu_item_main_preference_title)
       // initViews()
       // subsidaryList = ArrayList<String>()
       // subsidaryList!!.add("20 сек")
       // subsidaryList!!.add("30 сек")
       // subsidaryList!!.add("2 мин")
       // subsidaryList!!.add("6 мин")
       //
       // merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>
       //
       // viewChangeBlockTime.setOnClickListener(this)
       // viewNotification.setOnClickListener(this)
       // viewAskQuestion.setOnClickListener(this)
       // changePasswordView.setOnClickListener(this)
       //
       // languageView.setOnClickListener(this)
       // qrCodeImageView.setOnClickListener(this)
       // exitImageView.setOnClickListener(this)
       // viewRateApp.setOnClickListener(this)
       // viewShare.setOnClickListener(this)
    }
     fun onClick(view: View?){
         if (view == changePasswordView){
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
       /* }else if (view == logOutView){
            showLogOutDialog()*/
        }else if (view == languageView){
            val intent = Intent(activity, SetLanguageActivity::class.java)
            startActivity(intent)
      /**  }else if(view==qrCodeImageView){
            billQrCode()
        }else if(view==exitImageView){
            showLogOutDialog()*/
        }else if(view==viewChangeBlockTime){
             showBlockTimeDialog()
        }else if(view==viewNotification){
             notificationSwitch.isChecked = !notificationSwitch.isChecked
        }else if(view==viewRateApp){
             rateApp()
        }else if(view==viewAskQuestion){
             val emailIntent = Intent(
                 Intent.ACTION_SENDTO, Uri.fromParts("mailto", "support@oson.uz", null)
             )
             this.startActivity(Intent.createChooser(emailIntent, null))
         }
    }

    fun initViews(view: View?){
       // qrCodeView = findViewById(R.id.view_qr_code)
        // logOutView = findViewById(R.id.view_log_out)
        changePasswordView = view!!.findViewById(R.id.view_change_password)


        languageView = view!!.findViewById(R.id.view_set_language)
        viewChangeBlockTime = view!!.findViewById(R.id.view_change_block_time)
        viewAskQuestion = view!!.findViewById(R.id.view_ask_question)
        notificationSwitch = view!!.findViewById(R.id.notSwitch)
        viewNotification = view!!.findViewById(R.id.view_notification)
        viewRateApp = view!!.findViewById(R.id.view_rate_app)
    }
    fun rateApp(){
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.oson.business&hl=ru")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
        //    Toast.makeText(this, "Impossible to find an application for the market", Toast.LENGTH_LONG).show()
        }
    }
    fun showLogOutDialog(){
        val builder = AlertDialog.Builder(context)
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

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_qr_code)

        val spinner = dialog.findViewById<Spinner>(R.id.spinner)
        val arrayList = ArrayList<String>()
        for(i in merchantList!!.indices){
            arrayList.add(merchantList!![i].name)
        }

        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayList)
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
                    val intent = Intent(context, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                    cancelProgressDialog()
                }
            }
        )
    }
    var progressDialog: Dialog? = null

    fun showProgressDialog(){
        if (progressDialog == null){
            progressDialog = Dialog(context)
            progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog?.setCancelable(false)
            progressDialog?.setContentView(ProgressBar(context))
        }
        progressDialog?.show()
    }

    fun cancelProgressDialog(){
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog?.cancel()
    }
    fun showBlockTimeDialog(){
        dialog = BottomSheetDialog(context!!)
        dialog.setContentView(R.layout.bottom_sheet)

        recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)!!

        listviewOfBottomSheetManager = LinearLayoutManager(context)
        recyclerView.layoutManager =listviewOfBottomSheetManager
        listviewOfBottomSheetAdapter= PurchaseItemAdapter(context!!, subsidaryList!!)
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