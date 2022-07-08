package com.usertracker.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.usertracker.locationtracker.databinding.ActivityLoginMainBinding;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginMainBinding binding;
    Constant constant  =  Constant.getInstance();
    private ProgressBar loadingProgressBar;
    CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        constant.sharedPreferences = getSharedPreferences("com.android.location",MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layout =  binding.layout;
        loadingProgressBar = (ProgressBar)findViewById(R.id.loadingBar);
        if(constant.sharedPreferences.getString(Constant.TOKEN,null) != null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;




        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                try {
                    Boolean onclick  =  onClickOfLogin(usernameEditText.getText().toString(),passwordEditText.getText().toString());
                    //loadingProgressBar.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                   // loadingProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    Boolean onClickOfLogin(String userName ,String password) throws JSONException {
//        loadingProgressBar.setVisibility(View.VISIBLE);
        if(userName.isEmpty() && password.isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(layout, "Field Cannot be empty", Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
        final JSONObject body = new JSONObject();
        body.put("username", userName);
        body.put("password", password);
        AndroidNetworking.initialize(this);
        AndroidNetworking.post(" https://yashcoder.pythonanywhere.com/login/")
                .addJSONObjectBody(body)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            SharedPreferences.Editor editor =  constant.sharedPreferences.edit();
                            editor.putString(Constant.TOKEN,obj.getString("token"));
                            editor.apply();
                            loadingProgressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loadingProgressBar.setVisibility(View.GONE);
                        Log.d( "onError",anError.getErrorBody());
                        Snackbar snackbar = Snackbar
                                .make(layout, "Wrong Credential !", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                });
        return  false;
//        loadingProgressBar.setVisibility();
    }
}