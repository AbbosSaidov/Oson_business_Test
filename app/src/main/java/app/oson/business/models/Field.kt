package app.oson.business.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Field : Serializable {

    companion object {
        val LIST = "list"
        val INPUT = "input"
        val AMOUNT_INFO = "amount_info"
    }

    @SerializedName("fID")
    var fID: Int = 0
    @SerializedName("parent_fID")
    var parentId: Int = 0
    @SerializedName("label")
    lateinit var label: String
    @SerializedName("prefixlabel")
    lateinit var prefixlabel: String
    @SerializedName("type")
    lateinit var type: String
    @SerializedName("inputtype")
    lateinit var inputtype: InputType
    @SerializedName("min_length")
    var minLength: Int = 0
    @SerializedName("max_length")
    var maxLength: Int = 0
    @SerializedName("prefix")
    var prefix = listOf<String>();
    @SerializedName("app_usage")
    var appUsage: Int = 0


    class InputType : Serializable {
        var d = 0;
        var ch = 0
    }


    val isInput: Boolean get() = type == INPUT
    val isList: Boolean get() = type == LIST

}