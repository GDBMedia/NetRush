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
    public static void getOrders(String id, String orderNum, Callback callback){

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder  urlBuilder = HttpUrl.parse(Constants.GETORDERS).newBuilder();
        urlBuilder.addQueryParameter(Constants.KEYQ,  Constants.KEY)
                  .addQueryParameter(Constants.ID,  id)
                  .addQueryParameter(Constants.ORDER_NUMQ, orderNum);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static int proccssResult(Response response){
        int code = 1;
        try {

            String jsonData = response.body().string();
            Log.d(TAG, "proccssResult: " + jsonData);
            if (response.isSuccessful()) {
                JSONObject statusObj = new JSONObject(jsonData);
                code = statusObj.getInt("code");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return code;
    }

        public static void addToCart(String asin, String cartId, String hmac, Callback callback){

        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(Constants.ENDPOINT, Constants.AWS_ACCESS_KEY_ID, Constants.AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String requestUrl = null;
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("CartId", cartId);
        params.put("HMAC", hmac);
        params.put("AssociateTag", Constants.ASSOCIATE_TAG);
        params.put("Operation", "CartAdd");
        params.put("Item.1.ASIN", asin);
        params.put("Item.1.Quantity", "1");


        requestUrl = helper.sign(params);

        Log.d(TAG,  requestUrl);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder  urlBuilder = HttpUrl.parse(requestUrl).newBuilder();
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);


    }

    public static String proccessCart(Response response, int type){
        String purchaseUrl= "";

        try {
            String xmldata = response.body().string();
            JSONObject xmlJSONObj = XML.toJSONObject(xmldata);
            JSONObject cartObj = xmlJSONObj.getJSONObject("CartCreateResponse").getJSONObject("Cart");
            purchaseUrl = cartObj.getString("PurchaseURL");
        } catch (JSONException je) {
            System.out.println(je.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return purchaseUrl;
    }

    public static void createCart(Map<String, String> params, Callback callback){

        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(Constants.ENDPOINT, Constants.AWS_ACCESS_KEY_ID, Constants.AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String requestUrl = null;

        params.put("Service", "AWSECommerceService");
        params.put("AssociateTag", Constants.ASSOCIATE_TAG);
        params.put("Operation", "CartCreate");


        requestUrl = helper.sign(params);

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder  urlBuilder = HttpUrl.parse(requestUrl).newBuilder();
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);

    }




}
