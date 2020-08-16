package app.oson.business.api.services

import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.models.Purchase
import app.oson.business.models.PurchaseTransaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PurchaseService() : BaseRestService() {

    fun getPurchaseList(
        merchantId: Long?,
        userId: Long?,
        fromDate: Long?,
        toDate: Long?,

        callback: BaseCallback<Purchase.PurchaseList>
    ) {
        var format = SimpleDateFormat("yyyy-MM-dd");
        var from = if (fromDate == null) null else format.format(Date(fromDate!!))
        var to = if (toDate == null) null else format.format(Date(toDate!!))


        callback.onLoading()
        getApi().getPurchaseList(
            merchantId = merchantId,
            userId = userId,
            fromDate = from,
            toDate = to
        ).enqueue(object : Callback<Purchase.PurchaseList> {
            override fun onFailure(call: Call<Purchase.PurchaseList>, t: Throwable) {
                callback.onError(t)
            }

            override fun onResponse(call: Call<Purchase.PurchaseList>, response: Response<Purchase.PurchaseList>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError(Throwable("Error"))
                }
            }

        })
    }


    fun putPublicPurchase(
        merchantId: Long?,
        fields : String?,
        cardNumber : String?,
        cardExpireDate : String?,
        amount : Long?,

        callback: BaseCallback<PurchaseTransaction>
    ){

        callback.onLoading()
        getApi().putPublicPurchase(
            merchantId = merchantId,
            fields = fields,
            cardNumber = cardNumber,
            cardExpireDate = cardExpireDate,
            amount = amount
        ).enqueue(object : Callback<PurchaseTransaction>{
            override fun onFailure(call: Call<PurchaseTransaction>, t: Throwable) {
                callback.onError(t)
            }

            override fun onResponse(call: Call<PurchaseTransaction>, response: Response<PurchaseTransaction>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError(Throwable("ERROR"))
                }
            }

        })
    }




}