package app.oson.business.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Bill : Serializable {

    @SerializedName("errno")
    var errno : Long = 0;
    @SerializedName("errstr")
    lateinit var errstr : String
    @SerializedName("bill_id")
    var billId : Long = 0;
    @SerializedName("qr")
    var qrCodeBase : String? = null

}