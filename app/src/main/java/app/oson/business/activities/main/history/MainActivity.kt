package app.oson.business.activities.main.history

import android.content.Intent
import android.os.Bundle
//import android.support.design.widget.BottomNavigationView
//import com.google.android.material.tabs.TabLayout
//import android.support.v4.app.FragmentManager
//import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.main.request.RequestBillActivity
import app.oson.business.activities.main.settings.SettingsActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.MerchantService
import app.oson.business.fragments.FragmentPurchaseList
import app.oson.business.models.Merchant
import app.oson.business.activities.main.purchase.PurchaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonArray

class MainActivity : MyActivity(), BottomNavigationView.OnNavigationItemSelectedListener{

    var MERCHANT = "merchant"

    lateinit var fragmentManager: FragmentManager
    var transaction: FragmentTransaction? = null
    var fragmentPurchaseList: FragmentPurchaseList? = null

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

        tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fragment_main_history_purchase))
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?){
            }
            override fun onTabUnselected(p0: TabLayout.Tab?){
            }
            override fun onTabSelected(tab: TabLayout.Tab){
                val position = tab.position
                when (position){
                    1->{
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

    override fun onClick(v: View?){
        if(v == filterImageView){
            fragmentPurchaseList!!.onOpenFilterDialog()
        } else if(v == settingsLinearLayout){
            infoRelativeLayout.visibility = View.GONE
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            intent.putExtra(MERCHANT, merchantList)
            startActivity(intent)
        } else if (v == infoRelativeLayout){
            infoRelativeLayout.visibility = View.GONE
        } else if (v == clearImageView){
            clearImageView.visibility = View.GONE
            fragmentPurchaseList!!.clear()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean{
        when (menuItem.itemId){
            R.id.menu_main_bottomnavigationview_bill_item ->{
                var intent = Intent(this, RequestBillActivity::class.java)
                intent.putExtra(MERCHANT, merchantList)
                startActivity(intent)
            }
            R.id.menu_main_bottomnavigationview_purchase_item ->{
                var intent = Intent(this, PurchaseActivity::class.java)
                intent.putExtra(MERCHANT, merchantList)
                startActivity(intent)
            }
            R.id.menu_main_bottomnavigationview_history_item ->{}
            R.id.menu_item_bottomnavigationview_settings_item ->{
                var intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra(MERCHANT, merchantList)
                startActivity(intent)

            }
        }
        return false
    }

    var merchantList: ArrayList<Merchant>? = null
    fun getMerchantList(){
        MerchantService().merchantList(
            callback = object : BaseCallback<Merchant.MerchantList>{
                override fun onLoading(){
                }
                override fun onError(throwable: Throwable){
                    throwable.printStackTrace()
                }

                override fun onSuccess(response: Merchant.MerchantList){
                    merchantList = response.arrayList
                    //   Log.i("qwer", "JJ="+JsonArray(response.arrayList))

                    for(i in merchantList!!.indices){
                     //     Log.i("qwer", "JJ="+merchantList!![i].contractDate)
                    }
                }
            })
    }
}
