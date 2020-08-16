package app.oson.business.api


import app.oson.business.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*
import retrofit2.http.Field


interface Api {

    companion object {
        val API_HOST = "https://core.oson.uz:9443/"
        val API_HOST_OTHER = "https://core.oson.uz:8443/"
    }

    // Login
    @FormUrlEncoded
    @POST("/api/admin/login")
    fun login(
        @Field("login") login: String? = null,
        @Field("password") password: String? = null
    ): Call<UserData>


    @GET("/api/admin/logout/")
    fun logout(
        @Query("token") token: String? = null
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/api/admin/password/" + "{aid}")
    fun changePassword(
        @Path("aid") aid: Int? = null,
        @Field("oldpwd") oldPassword: String? = null,
        @Field("newpwd") newPassword: String? = null
    ): Call<ResponseBody>


    // PURCHASE
    @GET("/api/purchase?count=100&offset=0")
    fun getPurchaseList(
        @Query("merchant_id") merchantId: Long? = null,
        @Query("userId") userId: Long? = null,
        @Query("to_date") toDate: String? = null,
        @Query("from_date") fromDate: String? = null
    ): Call<Purchase.PurchaseList>


    // MERCHANT
    @GET("/api/merchant")
    fun getMerchantList(): Call<Merchant.MerchantList>

    @GET("/api/merchant/list?showall=1")
    fun getMerchantWithFields(
        @Query("merchant_id") merchantId: Long? ) : Call<Merchant.MerchantList>

    // BILL
    @FormUrlEncoded
    @PUT("/api/bill?offset=0&count=100")
    fun putBill(
        @Field("merchant_id") merchantId: Long? = null,
        @Field("phone") phone: String? = null,
        @Field("amount") amount: Long? = null,
        @Field("comment") comment: String? = null
    ) : Call<Bill>


    @PUT("/api/bill/qr")
    fun putBillQrCode() : Call<Bill>

    @FormUrlEncoded
    @PUT("/api/purchase/public_buy_start")
    fun putPublicPurchase(
        @Field("merchant_id") merchantId: Long? = null,
        @Field("fields") fields: String? = null,
        @Field("cardnumber") cardNumber: String? = null,
        @Field("cardexpire") cardExpireDate: String? = null,
        @Field("amount") amount: Long? = null

    ) : Call<PurchaseTransaction>

    @FormUrlEncoded
    @PUT("/api/purchase/public_buy_confirm")
    fun putPublicPurchaseConfirm(
        @Field("trn_id") transactionId: Long,
        @Field("code") smsCode: String,
        @Field("cardnumber") cardNumber: String,
        @Field("cardexpire") expireDate: String
    ) : Call<ResponseBody>
}