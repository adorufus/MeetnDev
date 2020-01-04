package com.octopro.meetndev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.octopro.meetndev.Utils.Permissions;

public class LoginRegisterPrompter extends AppCompatActivity {

    private Button loginBtn, registerBtn;


    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Permissions permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register_prompter);



//        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);
	    permissions = new Permissions(this, this, getSharedPreferences("MnDSharedPreferences", Context.MODE_PRIVATE), LocationServices.getFusedLocationProviderClient(this));
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(LoginRegisterPrompter.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginRegisterPrompter.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginRegisterPrompter.this, RegisterActivity.class);
                startActivity(intent);
                return;
            }
        });

//        getLastLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == 44){
			if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//				getLastLocation();

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		getLastLocation();
	}
}
