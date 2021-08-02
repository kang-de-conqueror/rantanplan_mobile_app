package khangtl.rantanplan.utils;

import khangtl.rantanplan.api.IGeneralWifiAPI;

public class APIUtils {
    public static final String BASE_URL = "http://87fe8b121240.ngrok.io/";

    public static IGeneralWifiAPI getGeneralWifiAPI() {
        return APIClient.getClient(BASE_URL).create(IGeneralWifiAPI.class);
    }
}
