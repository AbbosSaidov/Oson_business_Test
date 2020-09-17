package app.oson.business.activities.main.history

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.SplashActivity
import app.oson.business.activities.main.purchase.PurchaseActivity
import app.oson.business.activities.main.request.RequestBillActivity
import app.oson.business.activities.main.settings.SettingsActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.api.services.MerchantService
import app.oson.business.api.services.UserService
import app.oson.business.fragments.FragmentPurchaseList
import app.oson.business.models.Bill
import app.oson.business.models.Merchant
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import okhttp3.ResponseBody


class MainActivity : MyActivity(), BottomNavigationView.OnNavigationItemSelectedListener{

    var MERCHANT = "merchant"

    lateinit var fragmentManager: FragmentManager

    var transaction: FragmentTransaction? = null

    var fragmentPurchaseList: FragmentPurchaseList? = null
    var fragmentSettings: SettingsActivity? = null
    var fragmentPurchase: PurchaseActivity? = null
    var fragmentRequest: RequestBillActivity? = null

    lateinit var tabLayout: TabLayout

    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var infoRelativeLayout: RelativeLayout

    lateinit var settingsLinearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleTextView.setText(R.string.menu_item_bottomnavigationview_history_title)

        fragmentManager = supportFragmentManager
        transaction = fragmentManager.beginTransaction()
        fragmentPurchaseList = FragmentPurchaseList()
        fragmentSettings = SettingsActivity()
        fragmentPurchase = PurchaseActivity()
        fragmentRequest = RequestBillActivity()

        tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragment_main_history_purchase))
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?){}
            override fun onTabUnselected(p0: TabLayout.Tab?){}
            override fun onTabSelected(tab: TabLayout.Tab){
                when (tab.position) {
                    1 -> {
                        transaction!!.add(R.id.fragment_content, fragmentPurchaseList!!)
                        //   transaction!!.add(1,fragmentPurchaseList,R.id.fragment_content,)
                        transaction!!.commit()
                    }
                }
            }
        })

        fragmentPurchaseList = FragmentPurchaseList()
        transaction!!.add(R.id.fragment_content, fragmentPurchaseList!!)
        transaction!!.commit()

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.menu_main_bottomnavigationview_history_item
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        infoRelativeLayout = findViewById(R.id.relative_layout_info)
        infoRelativeLayout.setOnClickListener(this)
        settingsLinearLayout = findViewById(R.id.linear_layout_settings)
        settingsLinearLayout.setOnClickListener(this)

        getMerchantList()
    }

    override fun setupActionBar(){
        filterImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
        // infoImageView.visibility = View.VISIBLE
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

                override fun onSuccess(response: ResponseBody){
                    preferences.saveLoginData(null)
                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                    cancelProgressDialog()
                }
            }
        )
    }
    override fun onClick(v: View?){
        if(v == filterImageView){
            fragmentPurchaseList!!.onOpenFilterDialog()
        }else if(v == settingsLinearLayout){
            infoRelativeLayout.visibility = View.GONE
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            intent.putExtra(MERCHANT, merchantList)
            startActivity(intent)
        }else if(v==qrCodeImageView){
            billQrCode()
        }else if(v==exitImageView){
            showLogOutDialog()
        }else if(v == infoRelativeLayout){
            infoRelativeLayout.visibility = View.GONE
        }else if(v == clearImageView){
            clearImageView.visibility = View.GONE
            fragmentPurchaseList!!.clear()
        }
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
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean{
        when (menuItem.itemId){
            R.id.menu_main_bottomnavigationview_bill_item -> {

                titleTextView.setText(R.string.menu_item_bottomnavigationview_bill_title)

                qrCodeImageView.visibility = View.GONE
                exitImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                val bundle = Bundle()
                bundle.putSerializable("values",merchantList)
                fragmentRequest?.arguments = bundle
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.fragment_content, fragmentRequest!!)
                //   transaction!!.add(1,fragmentPurchaseList,R.id.fragment_content,)
                transaction!!.commit()
                bottomNavigationView.setOnNavigationItemSelectedListener(null)

                bottomNavigationView.selectedItemId =
                    R.id.menu_main_bottomnavigationview_bill_item
                bottomNavigationView.setOnNavigationItemSelectedListener(this)
                // var intent = Intent(this, RequestBillActivity::class.java)
               // intent.putExtra(MERCHANT, merchantList)
                //startActivity(intent)
            }
            R.id.menu_main_bottomnavigationview_purchase_item -> {

                titleTextView.setText(R.string.menu_item_bottomnavigationview_purchase_title)

                qrCodeImageView.visibility = View.GONE
                exitImageView.visibility = View.GONE
                filterImageView.visibility = View.GONE
                val bundle = Bundle()
                bundle.putSerializable("values",merchantList)
                fragmentPurchase?.arguments = bundle
                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.fragment_content, fragmentPurchase!!)
                //   transaction!!.add(1,fragmentPurchaseList,R.id.fragment_content,)
                transaction!!.commit()
                bottomNavigationView.setOnNavigationItemSelectedListener(null)

                bottomNavigationView.selectedItemId =
                    R.id.menu_main_bottomnavigationview_purchase_item
                bottomNavigationView.setOnNavigationItemSelectedListener(this)
                //    var intent = Intent(this, PurchaseActivity::class.java)
                //   intent.putExtra(MERCHANT, merchantList)
                //   startActivity(intent)
            }
            R.id.menu_main_bottomnavigationview_history_item -> {
                titleTextView.setText(R.string.menu_item_bottomnavigationview_history_title)

                qrCodeImageView.visibility = View.GONE
                exitImageView.visibility = View.GONE
                filterImageView.visibility = View.VISIBLE

                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.fragment_content, fragmentPurchaseList!!)
                //   transaction!!.add(1,fragmentPurchaseList,R.id.fragment_content,)
                transaction!!.commit()
                bottomNavigationView.setOnNavigationItemSelectedListener(null)

                bottomNavigationView.selectedItemId =
                    R.id.menu_main_bottomnavigationview_history_item
                bottomNavigationView.setOnNavigationItemSelectedListener(this)
            }
            R.id.menu_item_bottomnavigationview_settings_item -> {

                titleTextView.text = resources.getString(R.string.settings)

                qrCodeImageView.visibility = View.VISIBLE
                exitImageView.visibility = View.VISIBLE
                filterImageView.visibility = View.GONE

                transaction = supportFragmentManager.beginTransaction()
                transaction!!.replace(R.id.fragment_content, fragmentSettings!!)
                //   transaction!!.add(1,fragmentPurchaseList,R.id.fragment_content,)
                transaction!!.commit()

                bottomNavigationView.setOnNavigationItemSelectedListener(null)
                bottomNavigationView.selectedItemId =
                    R.id.menu_item_bottomnavigationview_settings_item
                bottomNavigationView.setOnNavigationItemSelectedListener(this)
                //  var intent = Intent(this, SettingsActivity::class.java)
                //  intent.putExtra(MERCHANT, merchantList)
                //  startActivity(intent)
            }
        }
        return false
    }
    var merchantList: ArrayList<Merchant>? = null
    fun getMerchantList(){
        MerchantService().merchantList(
            callback = object : BaseCallback<Merchant.MerchantList> {
                override fun onLoading() {}
                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                }

                override fun onSuccess(response: Merchant.MerchantList) {
                    merchantList = response.arrayList
                    //   Log.i("qwer", "JJ="+JsonArray(response.arrayList))
                    for (i in merchantList!!.indices) {
                        //     Log.i("qwer", "JJ="+merchantList!![i].contractDate)
                    }
                }
            })
    }
}