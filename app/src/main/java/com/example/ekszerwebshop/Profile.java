package com.example.ekszerwebshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    private static final String LOG_TAG = Profile.class.getName();
    private FrameLayout redCircle;
    private TextView contentText;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference users;
    private DocumentReference ref;

    private TextView currentName;
    private EditText newUsernameET;

    private int cartItems = 0;
    private SharedPreferences sh;
    private static final String PREF_KEY = Profile.class.getPackage().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentName = findViewById(R.id.current_uname);
        newUsernameET = findViewById(R.id.new_uname);

        sh = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        if(sh != null) {
            cartItems = sh.getInt("cnt_in_cart", 0);
            newUsernameET.setText(sh.getString("newName", ""));
        }

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("Users");
        ref = users.document(currentUser.getEmail());

        queryData();
    }

    private void queryData(){
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentName.setText("Üdvözlünk, " + document.get("uname"));
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuSearch = menu.findItem(R.id.search);
        menuSearch.setVisible(false);
        MenuItem menuLogin = menu.findItem(R.id.login);
        menuLogin.setVisible(false);
        MenuItem menuSignup = menu.findItem(R.id.signup);
        menuSignup.setVisible(false);
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
            case R.id.cart:
                intent = new Intent(this, CartList.class);
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

    public void deleteProfile(View view) {
        ref.delete().addOnSuccessListener(success -> {
            currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(LOG_TAG, "User account deleted.");
                            }
                        }
                    });
            Log.i(LOG_TAG, "User deleted");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        })
        .addOnFailureListener(failure -> {
            Toast.makeText(this, "Nem sikerült törölni", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "Error, user not deleted");
        });

    }

    public void updateProfile(View view) {
        ref.update("uname", newUsernameET.getText().toString()).addOnFailureListener(failure -> {
            Toast.makeText(this, "Nem sikerült megváltoztatni a felhasználónevet", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "Error, update failed");
        });
        queryData();
        newUsernameET.setText("");
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = sh.edit();
        editor.putInt("cnt_in_cart", cartItems);
        editor.putString("newName", newUsernameET.getText().toString());
        editor.apply();
        super.onPause();
    }
}