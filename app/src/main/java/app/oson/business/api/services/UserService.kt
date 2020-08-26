package app.oson.business.api.services


import app.oson.business.api.callbacks.BaseCallback
import app.oson.business.models.UserData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserService : BaseRestService(){

    fun login(
        login: String?,
        password: String?,
        callback: BaseCallback<UserData>
    ){
        callback.onLoading()
        getApi().login(
            login = login,
            password = password
        ).enqueue(object : Callback<UserData>{
            override fun onFailure(call: Call<UserData>, t: Throwable){
                callback.onError(t)
            }
            override fun onResponse(call: Call<UserData>, response: Response<UserData>){
                if(response.isSuccessful && response.body() != null){
                    callback.onSuccess(response.body()!!)
                }else{
                    callback.onError(Throwable("Error"))
                }
            }
        })
    }

    fun changePassword(
        aid: Int?,
        oldPassword: String?,
        newPassword: String?,
        callback: BaseCallback<ResponseBody>
    ){
        callback.onLoading()
        getApi().changePassword(
            aid = aid,
            oldPassword = oldPassword,
            newPassword = newPassword
        ).enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError(Throwable("Error"))
                }

            }

        })
    }

    fun logOut(
        token : String?,
        callback: BaseCallback<ResponseBody>
    ) {

        callback.onLoading()
        getApi().logout(
            token = token
        ).enqueue(object : Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable){
                callback.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                if (response.isSuccessful && response.body() != null){
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError(Throwable("Error"))
                }
            }
        })
    }


}
