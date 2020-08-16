package app.oson.business.api.services

import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.models.Bill
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BillService : BaseRestService() {

    fun putBill(
        merchantId: Long?,
        phone: String?,
        amount: Long?,
        comment: String?,

        callback: BaseCallback<Bill>
    ) {
        callback.onLoading()
        getApi().putBill(
            merchantId = merchantId,
            phone = phone,
            amount = amount,
            comment = comment
        )
            .enqueue(object : Callback<Bill> {
                override fun onFailure(call: Call<Bill>, t: Throwable) {
                    callback.onError(t)
                }

                override fun onResponse(call: Call<Bill>, response: Response<Bill>) {
                    if (response.isSuccessful && response.body() != null) {
                        callback.onSuccess(response.body()!!)
                    } else {
                        callback.onError(Throwable("ERROR"))
                    }
                }

            })

    }

    fun putBillQrCode(

        callback: BaseCallback<Bill>
    ) {
        callback.onLoading()
        getApi().putBillQrCode().enqueue(object : Callback<Bill> {
                override fun onFailure(call: Call<Bill>, t: Throwable) {
                    callback.onError(t)
                }

                override fun onResponse(call: Call<Bill>, response: Response<Bill>) {
                    if (response.isSuccessful && response.body() != null) {
                        callback.onSuccess(response.body()!!)
                    } else {
                        callback.onError(Throwable("ERROR"))
                    }
                }

            })

    }


}