package com.evanditaWiratamaPutraJBusER.jbus_android.request;

public class UtilsApi {
    public static final String BASE_URL_API = "http://192.168.1.29:5000/";
    public static BaseApiService getApiService() {
        return
                RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
