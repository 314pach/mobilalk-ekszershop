package com.example.ekszerwebshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private FrameLayout redCircle;
    private TextView contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        ImageView image = (ImageView)findViewById(R.id.background);
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        image.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        menuSearch.setVisible(false);

        MenuItem menuCart = menu.findItem(R.id.cart);
        MenuItem menuLogin = menu.findItem(R.id.login);
        MenuItem menuSignup = menu.findItem(R.id.signup);
        MenuItem menuLogout = menu.findItem(R.id.logout);
        MenuItem menuProfile = menu.findItem(R.id.profile);
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
            case R.id.products:
                intent = new Intent(this, ProductList.class);
                startActivity(intent);
                return true;
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

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }
}