package com.netrush.netrushapp.services;

import android.util.Log;

import com.google.gson.Gson;
import com.netrush.netrushapp.Constants;
import com.netrush.netrushapp.adapters.OrderAdapter;
import com.netrush.netrushapp.models.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeff on 8/15/2016.
 */
public class AmazonService {
    public static final String TAG = "Backend";
    public static void getOrders(String type, Callback callback){

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder  urlBuilder = HttpUrl.parse(Constants.GETORDERS).newBuilder();
        urlBuilder.addQueryParameter(Constants.TYPE,  type);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<Order> proccssoders(Response response){
        ArrayList<Order> orders = new ArrayList<>();
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONArray orderArray = new JSONArray(jsonData);
                Gson gson = new Gson();
                for(int i = 0; i< orderArray.length(); i++){
                    JSONObject orderJSON = orderArray.getJSONObject(i);

                    Order order = gson.fromJson(orderJSON.toString(), Order.class);
                    orders.add(order);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return orders;
    }


}
