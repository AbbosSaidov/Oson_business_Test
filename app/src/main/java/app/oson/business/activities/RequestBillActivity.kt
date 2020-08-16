package app.oson.business.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.oson.business.R
import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.api.services.BillService
import app.oson.business.models.Bill
import app.oson.business.models.Merchant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestBillActivity : MyActivity() {

    lateinit var spinner: Spinner
    lateinit var phoneNumberEditText: AppCompatEditText
    lateinit var billSumEditText: AppCompatEditText
    lateinit var commentEditText: AppCompatEditText
    lateinit var sendButton: AppCompatButton
    lateinit var qrCodeGenerateButton: AppCompatButton

    var merchantList: ArrayList<Merchant>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_bill)
        titleTextView.setText(R.string.menu_item_bottomnavigationview_bill_title)

        merchantList = intent.getSerializableExtra("merchant") as? ArrayList<Merchant>

        initViews()

        val arrayList = ArrayList<String>()
        for (i in merchantList!!.indices) {
            arrayList.add(merchantList!![i].name)
        }

        val spinnerAdapter = ArrayAdapter(this@RequestBillActivity, android.R.layout.simple_spinner_item, arrayList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinner!!.adapter = spinnerAdapter

    }

    override fun setupActionBar() {
        backImageView.visibility = View.VISIBLE
        titleTextView.visibility = View.VISIBLE

    }

    override fun onClick(v: View?) {
        if (v == sendButton) {
            putBill()
        } else if (v == qrCodeGenerateButton) {
            putBillQrCode()
        }
    }

    fun initViews() {
        spinner = findViewById(R.id.spinner)
        phoneNumberEditText = findViewById(R.id.edit_text_phone_number)
        phoneNumberEditText.setSelection(phoneNumberEditText.text!!?.length);
        billSumEditText = findViewById(R.id.edit_text_bill_sum)
        commentEditText = findViewById(R.id.edit_text_bill_comment)
        sendButton = findViewById(R.id.button_sent_request)
        sendButton.setOnClickListener(this)
        qrCodeGenerateButton = findViewById(R.id.button_generate_qr_code)
        qrCodeGenerateButton.setOnClickListener(this)
    }

    fun checkBillData(): Boolean {

        if (phoneNumberEditText.text.toString().length != 13) {
            showAlertDialog("Error" , resources.getString(R.string.dialogfragment_main_bill_request_phonenumber_empty))
            return false
        }

        if (billSumEditText.text.toString().isEmpty() || billSumEditText.text.toString().toLong() < 1000) {
            showAlertDialog("Error" , (resources.getString(R.string.dialogfragment_main_bill_request_sum_empty) + " " + billSumEditText.text.toString().toInt()))
            return false
        }

        if (commentEditText.text.toString().isEmpty()) {
            showAlertDialog("Error" , resources.getString(R.string.dialogfragment_main_bill_request_comment_empty))
            return false
        }

        return true
    }

    var merchantId: Long? = null
    lateinit var phoneNumber: String
    var sum: Long? = null
    lateinit var commentText: String

    fun putBill() {
        if (checkBillData()) {
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

                        val intent = Intent(this@RequestBillActivity, MainActivity::class.java);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })


        }
    }

    fun putBillQrCode(){
        if (checkBillData()){
            merchantId =
                    if (spinner.selectedItemPosition == 0) null else merchantList!!.get(spinner.selectedItemPosition - 1).id;
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

                        val intent = Intent(this@RequestBillActivity, MainActivity::class.java);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
        }
    }

}