package com.netrush.netrushapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.netrush.netrushapp.R;
import com.netrush.netrushapp.adapters.OrderAdapter;
import com.netrush.netrushapp.models.Order;
import com.netrush.netrushapp.services.AmazonService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener{
    public final String TAG = this.getClass().getSimpleName();
    private ArrayList<Order> mOrders = new ArrayList<>();
    private OrderAdapter mAdapter;
    public static Map<String, String> mProducts= new HashMap<String, String>();
    public static Button mCheckout;
    @Bind(R.id.orders) RecyclerView mRecyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        StringBuilder profileBuilder = new StringBuilder();
        final String profile = profileBuilder.toString();
        Log.v("userEmail", pref.getString("UserEmail", profile));

        mCheckout = (Button) findViewById(R.id.checkoutButton);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));
        getOrders();
    }

    public static void setButtonVisable(){

        mCheckout.setVisibility(View.VISIBLE);
        int total = 0;
        for (String item : mProducts.values()) {
                total++;
        }
        mCheckout.setText("Checkout(" + total +")");
    }

    private void getOrders() {
        final AmazonService amazonService = new AmazonService();
        amazonService.getOrders("1" , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                mOrders = amazonService.proccssoders(response);
                for(Order order : mOrders){
                    Log.d(TAG, "onResponse: " + order.getUnitprice() );
                    Log.d(TAG, "onResponse: " + order.getImageUrl());
                }
                ProductListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Order> orders = sortByDate(mOrders);
                        mAdapter = new OrderAdapter(ProductListActivity.this, orders);
                        mRecyclerview.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    private ArrayList<Order> sortByDate(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            DateFormat f = new SimpleDateFormat("MM/dd/yyyy");
            @Override public int compare(Order o1, Order o2) {
                try {
                    return f.parse(o2.getDate()).compareTo(f.parse(o1.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        return orders;
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_list_activity, menu);

        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("LoggedIn", false);
        editor.apply();

        Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        createCart();
    }

    private void createCart() {

        final AmazonService amazonService = new AmazonService();
        amazonService.createCart(mProducts, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String purchaseUrl = amazonService.proccessCart(response, 1);
                Log.d(TAG, "CreateCart: " + purchaseUrl);
            }
        });
    }
}

