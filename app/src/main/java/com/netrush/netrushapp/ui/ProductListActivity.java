package com.netrush.netrushapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.netrush.netrushapp.Constants;
import com.netrush.netrushapp.R;
import com.netrush.netrushapp.adapters.OrderAdapter;
import com.netrush.netrushapp.models.Order;
import com.netrush.netrushapp.services.AmazonService;
import com.netrush.netrushapp.utils.DateHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
    private static Button mCheckoutButton;
    private static RecyclerView mRecyclerview;
    private static RelativeLayout.LayoutParams layoutparams;
    private String mUserId;
    private SharedPreferences mSharedPreferences;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;
    private String mOrderNum;
    private static Context mContext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        mContext = this;
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProductListActivity.this);
        mUserId = mSharedPreferences.getString(Constants.USER_ID_REF, null);

        mCheckoutButton = (Button) findViewById(R.id.checkout);
        mRecyclerview = (RecyclerView) findViewById(R.id.orders);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCheckoutButton.setOnClickListener(this);
        layoutparams = (RelativeLayout.LayoutParams)mRecyclerview.getLayoutParams();
        mRecyclerview.setHasFixedSize(true);
        mOrderNum = "103-1967714-9334646";
        createProgressDialog();
        checkIfExists();

    }

    public static Context getContext(){
        return mContext;
    }

    private void checkIfExists() {
        progressDialog.show();
        Query query = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF + mUserId);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());

                        if(dataSnapshot.getValue() == null){
                            getOrders();
                        } else{
                            double timeStamp = Double.valueOf(dataSnapshot.child(Constants.PUSH_DATA_CHILD).child(Constants.TIME_STAMP_CHILD).getValue().toString());
                            if(DateHelper.getDiffInDays(timeStamp) > 1){
                                Toast.makeText(ProductListActivity.this, R.string.timestamp_update, Toast.LENGTH_SHORT).show();
                                getOrders();
                            }else{
                                retrieveOrders();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public static void setButtonVisibility(int fadeType){
        if(mAsins.size() == 0 && fadeType == 0){
            Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener(){

                @Override
                public void onAnimationStart(Animation animation){}

                @Override
                public void onAnimationRepeat(Animation animation){}

                @Override
                public void onAnimationEnd(Animation animation){
                    mCheckoutButton.setVisibility(View.INVISIBLE);

                }
            });
            mCheckoutButton.startAnimation(fadeOut);
            setRecyclerBottomMargin(0);

        }else if(mAsins.size() == 1 && fadeType == 1) {

            Animation fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            fadeIn.setAnimationListener(new Animation.AnimationListener(){

                @Override
                public void onAnimationStart(Animation animation){
                    mCheckoutButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation){}

                @Override
                public void onAnimationEnd(Animation animation){

                    setRecyclerBottomMargin(mCheckoutButton.getHeight());

                }
            });
            mCheckoutButton.startAnimation(fadeIn);
            mCheckoutButton.setText(ProductListActivity.getContext().getString(R.string.checkout) + mAsins.size() + ")");
        }else {
            mCheckoutButton.setText(ProductListActivity.getContext().getString(R.string.checkout) + mAsins.size() + ")");
        }
    }

    private static void setRecyclerBottomMargin(int height) {
        layoutparams.setMargins(0, 0, 0, height);
        mRecyclerview.setLayoutParams(layoutparams);
    }

    private void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Loading Orders From Firebase...");
        progressDialog.setCancelable(false);
    }

    private void retrieveOrders() {
        Query query = FirebaseDatabase.getInstance().getReference(Constants.USERS_REF + mUserId + Constants.ORDER_DATA_REF);
        mChildEventListener = query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded: " ,dataSnapshot.getValue().toString());
                progressDialog.dismiss();
                mOrders.add(dataSnapshot.getValue(Order.class));
                ArrayList<Order> orders = sortByMostPurchased(mOrders);
                setAdapter(orders);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: ");
                final Order order = dataSnapshot.getValue(Order.class);
                for(Iterator<Order> iterator = mOrders.iterator(); iterator.hasNext(); ) {
                    if(iterator.next().getAsin().equals(order.getAsin()))
                        iterator.remove();
                }
                mOrders.add(dataSnapshot.getValue(Order.class));
                ArrayList<Order> orders = sortByMostPurchased(mOrders);
                setAdapter(orders);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });


    }

    private void getOrders() {
        final AmazonService amazonService = new AmazonService();
        amazonService.getOrders(mUserId , mOrderNum, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                final int code = amazonService.proccssResult(response);
                ProductListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(code == 3){
                            Toast.makeText(ProductListActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                        }else if(code == 1){
                            Toast.makeText(ProductListActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                        }else if(code == 4){
                            Toast.makeText(ProductListActivity.this, R.string.no_purchase, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ProductListActivity.this, R.string.no_update, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void setAdapter(ArrayList<Order> orders) {
        mRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new OrderAdapter(ProductListActivity.this, orders);
        mRecyclerview.setAdapter(mAdapter);
    }

    private ArrayList<Order> sortByDateNewestToOldest(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            DateFormat f = new SimpleDateFormat(Constants.DATE_FORMAT_SOURCE);
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


    private ArrayList<Order> sortByMostPurchased(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            @Override public int compare(Order p1, Order p2) {
                return (Integer.valueOf(p2.getQuantity()) - (Integer.valueOf(p1.getQuantity())));
            }
        });
        return orders;
    }
    private ArrayList<Order> sortByDateOldestToNewest(ArrayList<Order> orders) {
        Collections.sort(orders, new Comparator<Order>() {
            DateFormat f = new SimpleDateFormat(Constants.DATE_FORMAT_SOURCE);
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
            case R.id.update:
                getOrders();
                break;
            case R.id.sortByMostPurchased:
                orders = sortByMostPurchased(mOrders);
                setAdapter(orders);
                break;


        }
        return super.onOptionsItemSelected(item);
    }



    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProductListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {createCart();}

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
    public void showMessage(final String message) {
        Toast.makeText(ProductListActivity.this, message, Toast.LENGTH_LONG).show();
    }
}

