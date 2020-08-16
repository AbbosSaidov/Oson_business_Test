package app.oson.business.api.callbacks

  interface BaseCallback <T>{

    fun onLoading()

    fun onError(throwable: Throwable)

    fun onSuccess(response : T)


}