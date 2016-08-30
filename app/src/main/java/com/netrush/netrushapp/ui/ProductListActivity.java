package com.netrush.netrushapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.netrush.netrushapp.Constants;
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
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductListActivity extends AppCompatActivity implements View.OnClickListener{
    public final String TAG = this.getClass().getSimpleName();
    private ArrayList<Order> mOrders = new ArrayList<>();
    private OrderAdapter mAdapter;
    public static ArrayList<String> mAsins = new ArrayList<>();
    private static Button mCheckout;
    private static RecyclerView mRecyclerview;
    private static RelativeLayout.LayoutParams layoutparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        ButterKnife.bind(this);

        mCheckout = (Button) findViewById(R.id.checkoutButton);
        mRecyclerview = (RecyclerView) findViewById(R.id.orders);
        mCheckout.setOnClickListener(this);
        layoutparams = (RelativeLayout.LayoutParams)mRecyclerview.getLayoutParams();
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));

        setButtonVisibility();
        getOrders();
    }
    public static void setButtonVisibility(){
        if(mAsins.size() == 0){
            mCheckout.setVisibility(View.INVISIBLE);
            setRecyclerBottomMargin(0);
        }else {
            setRecyclerBottomMargin(mCheckout.getHeight());
            mCheckout.setVisibility(View.VISIBLE);
            mCheckout.setText("Checkout(" + mAsins.size() + ")");
        }
    }

    private static void setRecyclerBottomMargin(int height) {
        layoutparams.setMargins(0, 0, 0, height);
        mRecyclerview.setLayoutParams(layoutparams);
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
                        ArrayList<Order> orders = sortByDateNewestToOldest(mOrders);
                        setAdapter(orders);
                    }
                });
            }
        });
    }

    private void setAdapter(ArrayList<Order> orders) {
        mAdapter = new OrderAdapter(ProductListActivity.this, orders);
        mRecyclerview.setAdapter(mAdapter);
    }

    private ArrayList<Order> sortByDateNewestToOldest(ArrayList<Order> orders) {
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

    private ArrayList<Order> sortByDateOldestToNewest(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            DateFormat f = new SimpleDateFormat("MM/dd/yyyy");
            @Override public int compare(Order o1, Order o2) {
                try {
                    return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        return orders;
    }
    private ArrayList<Order> sortAlphabeticallyAZ(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(final Order object1, final Order object2) {
                return object1.getTitle().compareTo(object2.getTitle());
            }
        } );
        return orders;
    }

    private ArrayList<Order> sortAlphabeticallyZA(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(final Order object1, final Order object2) {
                return object2.getTitle().compareTo(object1.getTitle());
            }
        } );
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.swing_up_left);
        mRecyclerview.startAnimation(animation);
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
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchProducts(String query) {
        ArrayList<Order> searchResults = new ArrayList<>();
        for (Order order : mOrders){
            if (order.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(order);
            }
        }
        setAdapter(searchResults);
    }


    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<Order> orders;
        switch (id){
            case R.id.logout:
                logout();
                break;
            case R.id.sortByDateNewestToOldest:
                 orders = sortByDateNewestToOldest(mOrders);
                setAdapter(orders);
                break;
            case R.id.sortByDateOldestToNewest:
                orders = sortByDateOldestToNewest(mOrders);
                setAdapter(orders);
                break;
            case R.id.sortAlphabeticallyAZ:
                orders = sortAlphabeticallyAZ(mOrders);
                setAdapter(orders);
                break;
            case R.id.sortAlphabeticallyZA:
                orders = sortAlphabeticallyZA(mOrders);
                setAdapter(orders);
                break;

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
        finish();
    }

    @Override
    public void onClick(View view) {
        createCart();
    }

    private void createCart() {
        Map<String, String> products = new HashMap<>();
        int itemNum = 1;
        for(String asin : mAsins){
            String itemKey = Constants.ITEM + itemNum + Constants.ASIN;
            String quantKey = Constants.ITEM + itemNum + Constants.QUANT;
            products.put(itemKey, asin);
            products.put(quantKey, Constants.AMOUNT);
            itemNum++;
        }

        final AmazonService amazonService = new AmazonService();
        amazonService.createCart(products, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String purchaseUrl = amazonService.proccessCart(response, 1);
                Log.d(TAG, "CreateCart: " + purchaseUrl);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(purchaseUrl));
                startActivity(i);
            }
        });
    }
}

