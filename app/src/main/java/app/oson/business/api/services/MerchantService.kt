package app.oson.business.api.services

import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.models.Merchant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MerchantService : BaseRestService(){

    fun merchantList(
        callback: BaseCallback<Merchant.MerchantList>
    ){
        callback.onLoading()
        getApi().getMerchantList()
            .enqueue(object : Callback<Merchant.MerchantList>{
                override fun onResponse(call: Call<Merchant.MerchantList>, response: Response<Merchant.MerchantList>){
                    if(response.isSuccessful && response.body() != null){
                        callback.onSuccess(response.body()!!)
                    }else{
                        callback.onError(Throwable("Error"))
                    }
                }
                override fun onFailure(call: Call<Merchant.MerchantList>, t: Throwable){
                    callback.onError(t)
                }
            })
    }
    fun getMerchant(
        merchantId : Long?,
        callback: BaseCallback<Merchant.MerchantList>
    ){
        callback.onLoading()
        getApiOther().getMerchantWithFields(
            merchantId = merchantId
        ).enqueue(object : Callback<Merchant.MerchantList>{
                override fun onResponse(call: Call<Merchant.MerchantList>, response: Response<Merchant.MerchantList>){
                    if (response.isSuccessful && response.body() != null){
                        callback.onSuccess(response.body()!!)
                    }else{
                        callback.onError(Throwable("Error"))
                    }
                }
                override fun onFailure(call: Call<Merchant.MerchantList>, t: Throwable){
                    callback.onError(t)
                }
            })
    }
}