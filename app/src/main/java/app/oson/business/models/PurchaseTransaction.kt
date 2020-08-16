package app.oson.business.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PurchaseTransaction : Serializable {

    @SerializedName("trn_id")
    var transactionId: Long = 0
    @SerializedName("phone")
    lateinit var phoneNumber: String
}