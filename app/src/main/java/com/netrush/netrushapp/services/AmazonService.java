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
    public static void getOrders(Callback callback){

        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder  urlBuilder = HttpUrl.parse(Constants.GETORDERS).newBuilder();
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

//    public static void addToCart(String asin, String cartId, String hmac, Callback callback){
//
//        SignedRequestsHelper helper;
//        try {
//            helper = SignedRequestsHelper.getInstance(Constants.ENDPOINT, Constants.AWS_ACCESS_KEY_ID, Constants.AWS_SECRET_KEY);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        String requestUrl = null;
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Service", "AWSECommerceService");
//        params.put("CartId", cartId);
//        params.put("HMAC", hmac);
//        params.put("AssociateTag", Constants.ASSOCIATE_TAG);
//        params.put("Operation", "CartAdd");
//        params.put("Item.1.ASIN", asin);
//        params.put("Item.1.Quantity", "1");
//
//
//        requestUrl = helper.sign(params);
//
//        Log.d(TAG,  requestUrl);
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .build();
//        HttpUrl.Builder  urlBuilder = HttpUrl.parse(requestUrl).newBuilder();
//        String url = urlBuilder.build().toString();
//
//        Request request= new Request.Builder()
//                .url(url)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(callback);
//
//
//    }

    public static void getImage(String asin, Callback callback){
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
        params.put("AssociateTag", Constants.ASSOCIATE_TAG);
        params.put("ResponseGroup", "Images");
        params.put("Operation", "ItemLookup");
        params.put("IdType", "ASIN");
        params.put("ItemId", asin);

        requestUrl = helper.sign(params);

        Log.d(TAG, "getImage: " + requestUrl);

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

    public String proccessImageUrl(Response response) {
        String imageUrl = "";
        try {
            String xmldata = response.body().string();
            JSONObject xmlJSONObj = XML.toJSONObject(xmldata);
            Log.d(TAG, "proccessImageUrl: " + xmlJSONObj);
            imageUrl = xmlJSONObj.getJSONObject("ItemLookupResponse").getJSONObject("Items").getJSONObject("Item").getJSONObject("LargeImage").getString("URL");
        } catch (JSONException je) {
            System.out.println(je.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}
