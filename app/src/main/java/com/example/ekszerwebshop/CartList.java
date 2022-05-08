package com.example.ekszerwebshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CartList extends AppCompatActivity {
    private static final String LOG_TAG = CartList.class.getName();
    private FrameLayout redCircle;
    private TextView contentText;

    private NotificationHandler handler;
    private int priceSum;

    private int cartItems = 0;

    private SharedPreferences sh;
    private static final String PREF_KEY = CartList.class.getPackage().toString();
    private Set<String> product_names;

    private FirebaseFirestore firestore;
    private CollectionReference products;

    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        handler = new NotificationHandler(this);
        priceSum = 0;

        firestore = FirebaseFirestore.getInstance();
        products = firestore.collection("Products");

        layout = findViewById(R.id.linearRoot);

        sh = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if (sh == null) {
            return;
        } else {
            cartItems = sh.getInt("cnt_in_cart", 0);
            product_names = sh.getStringSet("products", new HashSet<String>());
            sum(product_names);

            if (product_names.size() != 0) {
                Log.i(LOG_TAG, product_names.toString());
                Iterator<String> it = product_names.iterator();
                while (it.hasNext()) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    textView.setGravity(Gravity.CENTER);
                    String random = it.next();
                    textView.setText(random + " - " + sh.getInt(random, 0));
                    layout.addView(textView);
                }

                Button placeOrder = new Button(this);
                placeOrder.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                placeOrder.setGravity(Gravity.CENTER);
                placeOrder.setText("Rendelés");
                placeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(LOG_TAG, "From hell");
                        handler.send("Sikeres rendelés a következő összegben: " + priceSum + " Ft");

                        Log.i(LOG_TAG, String.valueOf(sum(product_names)) + "riiiiiiiiiiiiiii");
                        reset();
//                        Intent intent = new Intent(CartList.this, CartList.class);
//                        startActivity(intent);
                    }
                });
                layout.addView(placeOrder);
            } else {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setGravity(Gravity.CENTER);
                textView.setText("A kosarad jelenleg üres!");
                layout.addView(textView);
            }
        }
    }

    public void reset(){
        SharedPreferences.Editor editor = sh.edit();
        editor.putInt("cnt_in_cart", 0);
        editor.clear().apply();
//        finish();
        Intent intent = new Intent(CartList.this, CartList.class);
        startActivity(intent);
    }

    public int sum(Set<String> keys){
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String random = it.next();
            Log.i(LOG_TAG, random + " from sum function");
            products.whereEqualTo("name", random).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        ProductItem product = document.toObject(ProductItem.class);
                        priceSum += (sh.getInt(random, 0)*Integer.parseInt(product.getPrice().substring(0, product.getPrice().indexOf(' '))));
                    }
                }
            });
        }

        return priceSum;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        menuSearch.setVisible(false);
        MenuItem menuCart = menu.findItem(R.id.cart);
        menuCart.setVisible(false);
        MenuItem menuLogin = menu.findItem(R.id.login);
        menuLogin.setVisible(false);
        MenuItem menuSignup = menu.findItem(R.id.signup);
        menuSignup.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.products:
                intent = new Intent(this, ProductList.class);
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

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        if(sh != null) {
            SharedPreferences.Editor editor = sh.edit();
//        editor.putInt("cnt_in_cart", cartItems);
            editor.putStringSet("products", sh.getStringSet("products", new HashSet<>()));
            Iterator<String> it = sh.getStringSet("products", new HashSet<>()).iterator();
            while (it.hasNext()) {
                String random = it.next();
                editor.putInt(random, sh.getInt(random, 0));
            }
            editor.apply();
        }
        super.onPause();
    }

//    @Override
//    protected void onDestroy() {
//        Log.i(LOG_TAG, "Ondisztröj");
//
//        super.onDestroy();
//    }
}