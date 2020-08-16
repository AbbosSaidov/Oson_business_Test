package app.oson.business.models

import java.io.Serializable

class PurchaseSendField : Serializable {

    var fID : Int = 0
    lateinit var key : String
    lateinit var prefix : String
    lateinit var value : String
}