package app.oson.business.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Purchase : Serializable {

    @SerializedName("id")
    var id : Int = 0;
    @SerializedName("userId")
    var uid : Long = 0;
    @SerializedName("merchant_id")
    var merchantId : Int = 0;
    @SerializedName("amount")
    var amount : Long = 0
    @SerializedName("commission")
    var commission : Int = 0;
    @SerializedName("login")
    lateinit var login : String
    @SerializedName("ts")
    lateinit var time : String
    @SerializedName("status")
    var status : Int = 0;
    @SerializedName("src_phone")
    lateinit var srcPhone : String
    @SerializedName("phone")
    lateinit var phoneNumber : String
    @SerializedName("receipt_id")
    var receiptId : Int = 0;
    @SerializedName("status_text")
    lateinit var statusText : String
    @SerializedName("card_id")
    var cardId : Int = 0;
    @SerializedName("card_number")
    lateinit var cardNumber : String
    @SerializedName("paynet_tr_id")
    lateinit var paynetTrId : String
    @SerializedName("oson_paynet_tr_id")
    var osonPaynetTrId : Int = 0;
    @SerializedName("transaction_id")
    var transactionId : Long = 0;






    class PurchaseList : Serializable{
        var count : Int = 0;
        var sum : Long = 0;
        @SerializedName("array")
        var arrayList : ArrayList<Purchase>? = null
    }


}