package com.octopro.meetndev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTextEdit;
    private EditText passwordTextEdit;
    private Button registerButton;
    private RadioGroup redioGenderGroup;
    private EditText name;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        //initialize view components

        emailTextEdit = (EditText) findViewById(R.id.emailForm);
        passwordTextEdit = (EditText) findViewById(R.id.passwordForm);
        registerButton = (Button) findViewById(R.id.registerBtn);
        redioGenderGroup = (RadioGroup) findViewById(R.id.radioGenderGroup);
        name = (EditText) findViewById(R.id.nameForm);

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                int selectedRadioId = redioGenderGroup.getCheckedRadioButtonId();

                final RadioButton genderRadio = (RadioButton) findViewById(selectedRadioId);

                if(genderRadio.getText() == null){
                    return;
                }

                //do Register with firebase
                final String email = emailTextEdit.getText().toString();
                final String password = passwordTextEdit.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please input your email address!", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please setup your password!", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Sign up error", Toast.LENGTH_SHORT).show();
                                }else{
                                    String userId = auth.getUid();
                                    DatabaseReference fullname = FirebaseDatabase.getInstance().getReference().child("Users").child(genderRadio.getText().toString()).child(userId).child("name");
                                    DatabaseReference email = FirebaseDatabase.getInstance().getReference().child("Users").child(genderRadio.getText().toString()).child(userId).child("email");
                                    fullname.setValue(name.getText().toString());
                                    email.setValue(emailTextEdit.getText().toString());
                                }
                            }
                        });
                    }
                    catch (Exception error){
                        Toast.makeText(RegisterActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
}
