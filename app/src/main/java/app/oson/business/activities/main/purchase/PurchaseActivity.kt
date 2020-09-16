package app.oson.business.activities.main.purchase

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
//import android.support.design.widget.BottomSheetBehavior
//import android.support.design.widget.BottomSheetDialog
//import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.MerchantService
import app.oson.business.api.services.PurchaseService
import app.oson.business.database.Preferences
import app.oson.business.models.Merchant
import app.oson.business.models.PurchaseTransaction
import app.oson.business.views.FieldsLinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialog

class PurchaseActivity : Fragment(),PurchaseItemAdapter.ItemClickListener{

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
    lateinit var preferences: Preferences


    var merchantList: ArrayList<Merchant>? = null
    var subsidaryList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
     //   setContentView(R.layout.activity_purchase)
    //    titleTextView.setText(R.string.menu_item_bottomnavigationview_purchase_title)


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        val view = inflater.inflate(R.layout.activity_purchase, container, false)
        preferences = Preferences(context!!)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        var bundle:Bundle?=arguments
        merchantList = bundle?.getSerializable("values") as ArrayList<Merchant>?

        subsidaryList = ArrayList<String>()
        for (i in merchantList!!.indices){
            subsidaryList!!.add(merchantList!![i].name)
        }

        disabledButton()
        subsidiaryListDialog()

        /*   val spinnerAdapter = ArrayAdapter(this@PurchaseActivity, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter*/
        bootomSheetItemClickText.text = subsidaryList!![0]


        getMerchantWithFields()
    }
    private fun disabledButton(){
        sendButton.alpha = .5f
        sendButton.isEnabled = false


        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <= ' '}.isEmpty()){
                    sendButton.alpha = .5f
                    sendButton.isEnabled = false
                }else{
                    if(  amountEditText.text!!.isNotEmpty() && cardExpireEditText.text!!.isNotEmpty()){
                        sendButton.alpha = 1.0f
                        sendButton.isEnabled = true
                    }
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ){
            }
            override fun afterTextChanged(s: Editable){
            }
        })

        cardExpireEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.length == 0) {
                    sendButton.alpha = .5f
                    sendButton.isEnabled = false
                } else {
                    if(cardNumberEditText.text!!.isNotEmpty() &&  amountEditText.text!!.isNotEmpty()){
                        sendButton.alpha = 1.0f
                        sendButton.isEnabled = true
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

        amountEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.length == 0) {
                    sendButton.alpha = .5f
                    sendButton.isEnabled = false
                } else {
                    if(cardNumberEditText.text!!.isNotEmpty() && cardExpireEditText.text!!.isNotEmpty()){
                        sendButton.alpha = 1.0f
                        sendButton.isEnabled = true
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

        if(cardNumberEditText.text!!.isNotEmpty() && cardExpireEditText.text!!.isNotEmpty()&& amountEditText.text!!.isNotEmpty()){
            sendButton.alpha = 1.0f
            sendButton.isEnabled = true
        }
    }
 /*   override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }*/

     fun onClick(v: View?){
        if (v == sendButton){
            putPurchase()
        }else if(v == bootomSheetItemClick){
            dialog.show()
        }
    }

    fun subsidiaryListDialog(){

        dialog = BottomSheetDialog(context!!)
        dialog.setContentView(R.layout.bottom_sheet)

        recyclerView = dialog.findViewById<RecyclerView>(R.id.recycler_view)!!

        listviewOfBottomSheetManager = LinearLayoutManager(context)
        recyclerView.layoutManager =listviewOfBottomSheetManager
        listviewOfBottomSheetAdapter=PurchaseItemAdapter(context!!,subsidaryList!!)
        (listviewOfBottomSheetAdapter as PurchaseItemAdapter).setClickListener(this)
        recyclerView.adapter =listviewOfBottomSheetAdapter

    }

    fun initViews(view: View?){
        cardNumberEditText = view!!.findViewById(R.id.edit_text_card_number)
        cardExpireEditText = view!!.findViewById(R.id.edit_text_card_expire)
        amountEditText = view!!.findViewById(R.id.edit_text_amount)
        sendButton = view!!.findViewById(R.id.button_send_purchase)
        linearLayout = view!!.findViewById(R.id.linear_layout)
        bootomSheetItemClick = view!!.findViewById(R.id.bottom_sheet_click_view)
        bootomSheetItemClickText = view!!.findViewById(R.id.bottom_sheet_click_text)
        bootomSheetItemClick.setOnClickListener{onClick(bootomSheetItemClick)}
        sendButton.setOnClickListener{onClick(sendButton)}

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
    fun showAlertDialog(title: String, message: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("ОК",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel()})
        val alert = builder.create()
        alert.show()
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