package com.example.ekszerwebshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;

public class ProductList extends AppCompatActivity {
    private static final String LOG_TAG = ProductList.class.getName();
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private ArrayList<ProductItem> productList;
    private ItemAdapter adapter;

    private FrameLayout redCircle;
    private TextView contentText;
    
    private int gridNumber = 1;
    private int cartItems = 0;

    private FirebaseFirestore firestore;
    private CollectionReference products;

    private SharedPreferences sh;
    private static final String PREF_KEY = ProductList.class.getPackage().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        sh = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if(sh != null) {
            cartItems = sh.getInt("cnt_in_cart", 0);
        }

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        
        recyclerView = findViewById(R.id.product_container);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        productList = new ArrayList<>();
        
        adapter = new ItemAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        products = firestore.collection("Products");

        queryData();
    }

    private void queryData(){
        productList.clear();

        products.orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    ProductItem product = document.toObject(ProductItem.class);
                    productList.add(product);
                }
                if(productList.size()==0){
                    initializeData();
                    queryData();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeData() {
        String[] productNames = getResources().getStringArray(R.array.product_titles);
        String[] productPrices = getResources().getStringArray(R.array.product_prices);
        TypedArray productImages = getResources().obtainTypedArray(R.array.product_pictures);

        for(int i = 0; i< productNames.length; i++){
            products.add(new ProductItem(
                    productNames[i],
                    productPrices[i],
                    productImages.getResourceId(i, 0)));
//            productList.add(new ProductItem(productNames[i], productPrices[i], productImages.getResourceId(i, 0)));
        }

        productImages.recycle();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s){
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s){
                //log?
                adapter.getFilter().filter(s);
                return false;
            }
        });

        MenuItem menuShopping = menu.findItem(R.id.products);
        menuShopping.setVisible(false);

        MenuItem menuProfile = menu.findItem(R.id.profile);
        MenuItem menuCart = menu.findItem(R.id.cart);
        MenuItem menuLogin = menu.findItem(R.id.login);
        MenuItem menuSignup = menu.findItem(R.id.signup);
        MenuItem menuLogout = menu.findItem(R.id.logout);
        if (currentUser == null) {
            menuCart.setVisible(false);
            menuLogout.setVisible(false);
            menuProfile.setVisible(false);
        } else {
            menuLogin.setVisible(false);
            menuSignup.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.cart:
                intent = new Intent(this, CartList.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                intent = new Intent(this, Profile.class);
                startActivity(intent);
                return true;
            case R.id.signup:
                intent = new Intent(this, Registration.class);
                startActivity(intent);
                return true;
            case R.id.login:
                intent = new Intent(this, Login.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentText = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        if (0 < cartItems){
            contentText.setText(String.valueOf(cartItems));
        } else {
            contentText.setText("");
        }
        redCircle.setVisibility(cartItems>0 ? View.VISIBLE : View.GONE);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void update(){
        Log.i(LOG_TAG, "Number of cart itmes: " + cartItems);
        cartItems = (cartItems+1);
        if (0 < cartItems){
            contentText.setText(String.valueOf(cartItems));
        } else {
            contentText.setText("");
        }
        redCircle.setVisibility(cartItems>0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = sh.edit();
        editor.putInt("cnt_in_cart", cartItems);
        editor.apply();
        super.onPause();
    }
}