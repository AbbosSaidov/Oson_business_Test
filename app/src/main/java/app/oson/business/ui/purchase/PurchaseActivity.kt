package app.oson.business.ui.purchase

import android.app.Dialog
import android.os.Bundle
//import android.support.design.widget.BottomSheetBehavior
//import android.support.design.widget.BottomSheetDialog
//import android.support.v7.widget.*
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.MerchantService
import app.oson.business.api.services.PurchaseService
import app.oson.business.models.Merchant
import app.oson.business.models.PurchaseTransaction
import app.oson.business.views.FieldsLinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class PurchaseActivity : MyActivity(),PurchaseItemAdapter.ItemClickListener{

    lateinit var cardNumberEditText: AppCompatEditText
    lateinit var cardExpireEditText: AppCompatEditText
    lateinit var amountEditText: AppCompatEditText
    lateinit var sendButton: AppCompatButton
    lateinit var linearLayout: FieldsLinearLayout
    lateinit var bootomSheetItemClick: View
    lateinit var bootomSheetItemClickText: AppCompatTextView
    lateinit var bottomSheet: LinearLayout
    lateinit var recyclerView: RecyclerView
    lateinit var listviewOfBottomSheetAdapter: RecyclerView.Adapter<*>
    lateinit var listviewOfBottomSheetManager: LinearLayoutManager
    lateinit var dialog: BottomSheetDialog
    var selectedItemPosition: Int = 0


    var merchantList: ArrayList<Merchant>? = null
    var subsidaryList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        titleTextView.setText(R.string.menu_item_bottomnavigationview_purchase_title)
        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

         subsidaryList = ArrayList<String>()
        for (i in merchantList!!.indices){
            subsidaryList!!.add(merchantList!![i].name)
        }
        initViews()


        subsidiaryListDialog()


        /*   val spinnerAdapter = ArrayAdapter(this@PurchaseActivity, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter*/
        bootomSheetItemClickText.text = subsidaryList!![0]

        sendButton.setOnClickListener(this)

        getMerchantWithFields()
    }
    override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?){
        if(v == backImageView){
            finish()
        } else if (v == sendButton){
            putPurchase()
        }else if(v == bootomSheetItemClick){
            dialog.show()
        }
    }

    fun subsidiaryListDialog(){

        dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet)

        recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)!!

        listviewOfBottomSheetManager = LinearLayoutManager(this)
        recyclerView.layoutManager =listviewOfBottomSheetManager
        listviewOfBottomSheetAdapter=PurchaseItemAdapter(this,subsidaryList!!)
        (listviewOfBottomSheetAdapter as PurchaseItemAdapter).setClickListener(this)
        recyclerView.adapter =listviewOfBottomSheetAdapter

    }

    fun initViews(){
        cardNumberEditText = findViewById(R.id.edit_text_card_number)
        cardExpireEditText = findViewById(R.id.edit_text_card_expire)
        amountEditText = findViewById(R.id.edit_text_amount)
        sendButton = findViewById(R.id.button_send_purchase)
        linearLayout = findViewById(R.id.linear_layout)
        bootomSheetItemClick = findViewById(R.id.bottom_sheet_click_view)
        bootomSheetItemClickText = findViewById(R.id.bottom_sheet_click_text)
        bootomSheetItemClick.setOnClickListener(this)
        //bottomSheet = findViewById(R.id.bottom_sheet)
        //var sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        //sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


       // listviewOfBottomSheetAdapter = ArrayAdapter<String>(this,R.layout.item_recycler_view_purchase_activity, arrayList)


     /*  listviewOfBottomSheet.setOnItemClickListener {parent, view, position, id ->
            selectedItemPosition=position
            bootomSheetItemClickText.text = arrayList[position]
    //          var sheetBehavior = BottomSheetBehavior.from(bottomSheet)
   //         sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }*/
    }

    fun checkPurchaseData(): Boolean {
        if (merchantList == null) {
            showAlertDialog("Error", "NULLLLL")
            return false
        }

        if (cardNumberEditText.text.toString().length != 16) {
            showAlertDialog("Error", "Kartani to'liq kiriting")
            return false
        }

        if (cardExpireEditText.text.toString().length != 4) {
            showAlertDialog("Error", "Karta amal qilish muddatini kiriting")
            return false
        }

        if (amountEditText.text.toString().isEmpty()) {
            showAlertDialog("Error", "Summani kiriting!")
            return false
        }

        return true
    }

    fun getFields() {
        if (merchantWithFields != null) {
            linearLayout.buildWithFields(merchantWithFields!![0].fields())
        }
    }

    var merchantId: Long? = null
    lateinit var fields: String
    lateinit var cardNumber: String
    lateinit var cardExpireData: String
    var amount: Long? = null

    fun putPurchase() {
        if (checkPurchaseData()) {
            if (linearLayout.getPurchaseData()!! != null){
                merchantId = merchantList!![selectedItemPosition].id
                fields = linearLayout.getPurchaseData()!!
                cardNumber = cardNumberEditText.text.toString()
                cardExpireData = cardExpireEditText.text.toString()
                amount = amountEditText.text.toString().toLong()

                PurchaseService().putPublicPurchase(
                    merchantId = merchantId,
                    fields = fields,
                    cardNumber = cardNumber,
                    cardExpireDate = cardExpireData,
                    amount = amount!! * 100,
                    callback = object : BaseCallback<PurchaseTransaction> {
                        override fun onLoading() {

                        }

                        override fun onError(throwable: Throwable) {

                        }

                        override fun onSuccess(response: PurchaseTransaction) {

                        }

                    })
            } else {
                showAlertDialog("Error", "Malumotlarni kiritishda xatolik bo'ldi")
            }
        }
    }

    var merchantWithFields: ArrayList<Merchant>? = null

    fun getMerchantWithFields(){

        MerchantService().getMerchant(
            merchantId = merchantList!![selectedItemPosition].id,
            callback = object : BaseCallback<Merchant.MerchantList> {
                override fun onLoading() {

                }

                override fun onError(throwable: Throwable) {
                    throwable.printStackTrace()
                }

                override fun onSuccess(response: Merchant.MerchantList) {
                    merchantWithFields = response.arrayList

                    getFields()

                }
            }
        )
    }

    override fun onItemClick(position: Int) {
        Log.i("werty", "qwe=$position")
         dialog.hide()
        bootomSheetItemClickText.text = subsidaryList!![position]
    }

}