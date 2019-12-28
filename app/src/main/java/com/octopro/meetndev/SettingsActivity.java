package com.octopro.meetndev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

	private EditText nameEditText, emailEditText, phoneEditText;
	private Button saveButton, backButton;
	private ImageView profileImage;
	private Constants constants;
	private DatabaseReference userDatabase;

	private String userId, name, email, phone, profileImageUri;

	private Uri resultUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		String userGender = getIntent().getExtras().getString("userGender");

		constants = new Constants();
		constants.auth = FirebaseAuth.getInstance();

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		phoneEditText = (EditText) findViewById(R.id.phoneEditText);
		saveButton = (Button) findViewById(R.id.saveBtn);
		backButton = (Button) findViewById(R.id.backBtn);
		profileImage = (ImageView) findViewById(R.id.profileImage);

		userId = constants.auth.getUid();
		userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userGender).child(userId);

		getUserInfo();

		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, 1);
			}
		});

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveUserInfo(nameEditText.getText().toString(), emailEditText.getText().toString(), phoneEditText.getText().toString());
			}
		});
	}

	private void getUserInfo() {
		userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
					Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
					if(map.get("name") != null || map.get("email") != null || map.get("phone") != null || map.get("profileImageurl") != null){
						name = map.get("name").toString();
						nameEditText.setText(name);
					}
					else{
						nameEditText.setText("");
					}

					if(map.get("email") != null){
						email = map.get("email").toString();
						emailEditText.setText(email);
					}
					else{
						emailEditText.setText("");
					}

					if(map.get("phone") != null){
						phone = map.get("phone").toString();
						phoneEditText.setText(phone);
					}
					else{
						phoneEditText.setText("");
					}

					Glide.with(getApplication()).clear(profileImage);
					if(map.get("profileImageUrl") != null){
						profileImageUri = map.get("profileImageUrl").toString();
						Log.w("LIST", "profile Image Url: " + profileImageUri);
						Glide.with(getApplication()).load(profileImageUri).into(profileImage);
					}
					else{
						profileImageUri = "";
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	private void saveUserInfo(String thisName, String thisEmail, String thisPhone) {
		name = thisName;
		email = thisEmail;
		phone = thisPhone;

		Map userInfo = new HashMap();
		userInfo.put("name", name);
		userInfo.put("email", email);
		userInfo.put("phone", phone);

		userDatabase.updateChildren(userInfo);

		if(resultUri != null){
			StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
			byte[] data = baos.toByteArray();

			UploadTask uploadTask = filepath.putBytes(data);
			uploadTask.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					finish();
				}
			}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					Uri downloadUrl = Uri.parse(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
					taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							Log.w("LIST", "download url: " + uri.toString());

							Map userInfo = new HashMap();
							userInfo.put("profileImageUrl", uri.toString());
							userDatabase.updateChildren(userInfo);

							finish();
						}
					});
					return;
				}
			}).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
					if(task.isSuccessful()){
						Uri downloadUrl = Uri.parse(task.getResult().toString());
						Log.i("LIST", "download url: " + downloadUrl.toString());
					}
				}
			});

		}
		else{
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1 && resultCode == Activity.RESULT_OK){
			final Uri imageUri = data.getData();
			resultUri = imageUri;
			profileImage.setImageURI(resultUri);
		}
	}
}
