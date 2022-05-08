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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration extends AppCompatActivity {

    private FrameLayout redCircle;
    private TextView contentText;

    EditText usernameET;
    EditText emailET;
    EditText passwordET;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private FirebaseFirestore firestore;
    private CollectionReference users;

    private SharedPreferences sh;
    private static final String PREF_KEY = Registration.class.getPackage().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameET = findViewById(R.id.Rusername);
        emailET = findViewById(R.id.Remail);
        passwordET = findViewById(R.id.Rpassword);

        sh = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if(sh != null) {
            usernameET.setText(sh.getString("signupUsername", ""));
            emailET.setText(sh.getString("signupEmail", ""));
            passwordET.setText(sh.getString("signupPassword", ""));
        }

        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("Users");
    }

    public void signup(View view) {
        String username = usernameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    users.document(email).set(new User(
                            username,
                            email,
                            password));
                    redirect();
                } else {
                    Toast.makeText(Registration.this, "Sikertelen regisztráció!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void redirect(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        menuSearch.setVisible(false);
        MenuItem menuLogout = menu.findItem(R.id.logout);
        menuLogout.setVisible(false);
        MenuItem menuSignup = menu.findItem(R.id.signup);
        menuSignup.setVisible(false);
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
            case R.id.login:
                intent = new Intent(this, Login.class);
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
        editor.putString("signupUsername", usernameET.getText().toString());
        editor.putString("signupEmail", emailET.getText().toString());
        editor.putString("signupPassword", passwordET.getText().toString());
        editor.apply();
        super.onPause();
    }
}