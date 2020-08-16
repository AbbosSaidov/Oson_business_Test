package app.oson.business.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Merchant : Serializable{

    @SerializedName("id")
    var id : Long = 0;
    @SerializedName("name")
    lateinit var name : String
    @SerializedName("group")
    var group : Int = 0;
    @SerializedName("status")
    var status : Int = 0;
    @SerializedName("inn")
    lateinit var inn : String
    @SerializedName("mfo")
    lateinit var mfo : String
    @SerializedName("url")
    lateinit var url : String
    @SerializedName("ch_account")
    lateinit var chAccount : String
    @SerializedName("contract")
    lateinit var contract : String
    @SerializedName("contract_date")
    lateinit var contractDate : String
    @SerializedName("min_amount")
    var minAmount : Int = 0;
    @SerializedName("max_amount")
    var maxAmount : Int = 0;
    @SerializedName("merchant_id")
    lateinit var merchantId : String
    @SerializedName("terminal_id")
    lateinit var terminalId : String
    @SerializedName("port")
    var port : Int = 0;
    @SerializedName("external")
    var external : Int = 0;
    @SerializedName("external_service_id")
    var externalServiceId : Int = 0;
    @SerializedName("bank_id")
    var bankId : Int = 0;
    @SerializedName("view_mode")
    var viewMode : Int = 0;
    @SerializedName("rate")
    var rate : Int = 0;
    @SerializedName("rate_money")
    var rateMoney : Int = 0;
    @SerializedName("position")
    var position : Int = 0;
    @SerializedName("filial_flag")
    var filialFlag : Int = 0;
    @SerializedName("parent_id")
    var parentId : Int = 0;
    @SerializedName("api_id")
    var apiId : Int = 0;
    @SerializedName("dayli_tran_limit")
    var dayliTranLimit : Int = 0;
    @SerializedName("version_android")
    lateinit var versionAndroid : String
    @SerializedName("legal_name")
    lateinit var legalName : String
    @SerializedName("fields")
    lateinit var fieldsMap: Map<String, Map<String, Any>>


    class MerchantList : Serializable {
        var count : Int = 0;
        @SerializedName("array")
        var arrayList : ArrayList<Merchant>? = null
    }

    fun fields() : List<Field> {
        var fields = listOf<Field>()

        for (map in fieldsMap.values){

            val str = Gson().toJson(map)
            val field = Gson().fromJson(str, Field::class.java)

            fields = fields.plus( field )
        }

        return fields
    }


}