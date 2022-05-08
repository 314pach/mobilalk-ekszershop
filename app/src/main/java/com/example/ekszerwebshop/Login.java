package com.example.ekszerwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText usernameET;
    EditText passwordET;

    private FrameLayout redCircle;
    private TextView contentText;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private SharedPreferences sh;
    private static final String PREF_KEY = Login.class.getPackage().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);

        sh = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if(sh != null) {
            usernameET.setText(sh.getString("loginEmail", ""));
            passwordET.setText(sh.getString("loginPassword", ""));
        }

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    public void login(View view) {
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    redirect();
                } else {
                    Toast.makeText(Login.this, "Sikertelen bejelentkezés!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void redirect(){
        Intent intent = new Intent(this, ProductList.class);
        startActivity(intent);
    }

    public void signup(View view) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        menuSearch.setVisible(false);
        MenuItem menuLogout = menu.findItem(R.id.logout);
        menuLogout.setVisible(false);
        MenuItem menuLogin = menu.findItem(R.id.login);
        menuLogin.setVisible(false);
        MenuItem menuCart = menu.findItem(R.id.cart);
        menuCart.setVisible(false);
        MenuItem menuProfile = menu.findItem(R.id.profile);
        menuProfile.setVisible(false);
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
            case R.id.signup:
                intent = new Intent(this, Registration.class);
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
        SharedPreferences.Editor editor = sh.edit();
        editor.putString("loginEmail", usernameET.getText().toString());
        editor.putString("loginPassword", passwordET.getText().toString());
        editor.apply();
        super.onPause();
    }
}