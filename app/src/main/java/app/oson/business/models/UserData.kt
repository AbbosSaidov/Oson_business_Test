package app.oson.business.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserData: Serializable{

    @SerializedName("logged")
    var logged : Boolean = true
    @SerializedName("token")
    lateinit var token: String;
    @SerializedName("aid")
    var aid: Int = 0;
    @SerializedName("errno")
    var errno: Int = 0;
    @SerializedName("errstr")
    lateinit var errstr: String;





}