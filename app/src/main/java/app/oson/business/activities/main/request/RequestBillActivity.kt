package app.oson.business.activities.main.request

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
//import android.support.design.widget.BottomSheetBehavior
//import android.support.design.widget.BottomSheetDialog
//import android.support.v7.widget.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.oson.business.R
import app.oson.business.activities.MyActivity
import app.oson.business.activities.main.history.MainActivity
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.models.Bill
import app.oson.business.models.Merchant
import app.oson.business.activities.main.purchase.PurchaseItemAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog


class RequestBillActivity : MyActivity(), PurchaseItemAdapter.ItemClickListener {

    lateinit var phoneNumberEditText: AppCompatEditText
    lateinit var billSumEditText: AppCompatEditText
    lateinit var commentEditText: AppCompatEditText
    lateinit var qrCodeGenerateButton: AppCompatButton
    lateinit var sendButton: AppCompatButton
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
        setContentView(R.layout.activity_request_bill)
        titleTextView.setText(R.string.menu_item_bottomnavigationview_bill_title)

        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>



        subsidaryList = ArrayList<String>()
        for (i in merchantList!!.indices){
            subsidaryList!!.add(merchantList!![i].name)
        }
        initViews()
        disabledButton()
        subsidiaryListDialog()
        /*val spinnerAdapter = ArrayAdapter(
            this@RequestBillActivity,
            android.R.layout.simple_spinner_item,
            arrayList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter*/
        bootomSheetItemClickText.text = subsidaryList!![0]

        sendButton.setOnClickListener(this)

    }

    override fun setupActionBar(){
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE
    }

    override fun onClick(v: View?){
        if (v == sendButton){
            putBill()
        }else if(v == qrCodeGenerateButton){
            putBillQrCode()
        }else if(v == bootomSheetItemClick){
            dialog.show()
        }
    }

    fun initViews(){
        phoneNumberEditText = findViewById(R.id.edit_text_phone_number)
        phoneNumberEditText.setSelection(phoneNumberEditText.text!!.length)
        billSumEditText = findViewById(R.id.edit_text_bill_sum)
        commentEditText = findViewById(R.id.edit_text_bill_comment)
        sendButton = findViewById(R.id.button_sent_request)
        sendButton.setOnClickListener(this)
        bootomSheetItemClick = findViewById(R.id.bottom_sheet_click_view)
        bootomSheetItemClickText = findViewById(R.id.bottom_sheet_click_text)
        bootomSheetItemClick.setOnClickListener(this)
        qrCodeGenerateButton = findViewById(R.id.button_generate_qr_code)
        qrCodeGenerateButton.setOnClickListener(this)
        /*       bottomSheet = findViewById(R.id.bottom_sheet)
        var sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        listviewOfBottomSheet=findViewById(R.id.mobile_list)
        listviewOfBottomSheetAdapter = ArrayAdapter<String>(this,R.layout.item_recycler_view_purchase_activity, arrayList)
        listviewOfBottomSheet.adapter = listviewOfBottomSheetAdapter

        listviewOfBottomSheet.setOnItemClickListener {parent, view, position, id ->
            selectedItemPosition=position
            bootomSheetItemClickText.text = arrayList[position]
            var sheetBehavior = BottomSheetBehavior.from(bottomSheet)
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }*/
    }
    private fun disabledButton(){
        sendButton.alpha = .5f
        sendButton.isEnabled = false

        phoneNumberEditText.addTextChangedListener(object : TextWatcher{
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
                    if(billSumEditText.text!!.isNotEmpty() && commentEditText.text!!.isNotEmpty()){
                        sendButton.alpha = 1.0f
                        sendButton.isEnabled = true
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,after: Int){}
            override fun afterTextChanged(s: Editable){}
        })
        billSumEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    sendButton.alpha = .5f
                    sendButton.isEnabled = false
                } else {
                    if(phoneNumberEditText.text!!.isNotEmpty()&& commentEditText.text!!.isNotEmpty()){
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

        commentEditText.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ){
                if(s.toString().trim { it <= ' ' }.isEmpty()) {
                    sendButton.alpha = .5f
                    sendButton.isEnabled = false
                }else{
                    if(phoneNumberEditText.text!!.isNotEmpty() && billSumEditText.text!!.isNotEmpty()){
                        sendButton.alpha = 1.0f
                        sendButton.isEnabled = true
                    }
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ){}
            override fun afterTextChanged(s: Editable){}
        })

        if(phoneNumberEditText.text!!.isNotEmpty() && billSumEditText.text!!.isNotEmpty()&& commentEditText.text!!.isNotEmpty()){
            sendButton.alpha = 1.0f
            sendButton.isEnabled = true
        }
    }
    fun checkBillData(): Boolean{

        if (phoneNumberEditText.text.toString().length != 13){
            showAlertDialog(
                "Error",
                resources.getString(R.string.dialogfragment_main_bill_request_phonenumber_empty)
            )
            return false
        }

        if (billSumEditText.text.toString().isEmpty() || billSumEditText.text.toString().toLong() < 1000) {
            showAlertDialog(
                "Error",
                (resources.getString(R.string.dialogfragment_main_bill_request_sum_empty) + " " + billSumEditText.text.toString()
                    .toInt())
            )
            return false
        }

        if (commentEditText.text.toString().isEmpty()) {
            showAlertDialog(
                "Error",
                resources.getString(R.string.dialogfragment_main_bill_request_comment_empty)
            )
            return false
        }

        return true
    }

    var merchantId: Long? = null
    lateinit var phoneNumber: String
    var sum: Long? = null
    lateinit var commentText: String

    fun putBill() {
        if (checkBillData()){
//            merchantId =
//                    if (spinner.selectedItemPosition == 0) null else merchantList!!.get(spinner.selectedItemPosition - 1).id;
//            phoneNumber = phoneNumberEditText.text.toString()
//            sum = billSumEditText.text.toString().toLong()
//            commentText = commentEditText.text.toString()

            BillService().putBill(
                merchantId = merchantId,
                phone = phoneNumber,
                amount = sum,
                comment = commentText,
                callback = object : BaseCallback<Bill> {
                    override fun onLoading() {

                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                    }

                    override fun onSuccess(response: Bill) {

                        val intent = Intent(this@RequestBillActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                })


        }
    }

    fun putBillQrCode(){
        if (checkBillData()){
            merchantId =
                    if (selectedItemPosition == 0) null else merchantList!![selectedItemPosition - 1].id
            phoneNumber = phoneNumberEditText.text.toString()
            sum = billSumEditText.text.toString().toLong()
            commentText = commentEditText.text.toString()

            BillService().putBillQrCode(
                callback = object : BaseCallback<Bill> {
                    override fun onLoading() {

                    }

                    override fun onError(throwable: Throwable) {
                        throwable.printStackTrace()
                    }

                    override fun onSuccess(response: Bill) {

                        val intent = Intent(this@RequestBillActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                })
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
    override fun onItemClick(position: Int) {
        Log.i("werty", "qwe=$position")
        dialog.hide()
        bootomSheetItemClickText.text = subsidaryList!![position]    }

}