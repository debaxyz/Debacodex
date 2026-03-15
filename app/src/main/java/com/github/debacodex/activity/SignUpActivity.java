package com.github.debacodex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.github.debacodex.R;
public class SignUpActivity extends AppCompatActivity {
	
	private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
	private CheckBox cbTerms;
	private Button btnSignUp;
	private TextView tvLogin, tvTermsConditions;
	private ProgressBar progressBar;
	private FirebaseAuth mAuth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		// Initialize Firebase Auth
		mAuth = FirebaseAuth.getInstance();
		
		// Initialize views
		etFullName = findViewById(R.id.etFullName);
		etEmail = findViewById(R.id.etEmail);
		etPassword = findViewById(R.id.etPassword);
		etConfirmPassword = findViewById(R.id.etConfirmPassword);
		cbTerms = findViewById(R.id.cbTerms);
		btnSignUp = findViewById(R.id.btnSignUp);
		tvLogin = findViewById(R.id.tvLogin);
		tvTermsConditions = findViewById(R.id.tvTermsConditions);
		progressBar = findViewById(R.id.progressBar);
		
		// Set click listeners
		btnSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registerUser();
			}
		});
		
		tvLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
				finish();
			}
		});
		
		tvTermsConditions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open Terms & Conditions activity or dialog
			//	startActivity(new Intent(SignupActivity.this, TermsActivity.class));
			}
		});
	}
	
	private void registerUser() {
		// Get input values
		String fullName = etFullName.getText().toString().trim();
		String email = etEmail.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		String confirmPassword = etConfirmPassword.getText().toString().trim();
		
		// Validate inputs
		if (TextUtils.isEmpty(fullName)) {
			etFullName.setError("Full name is required");
			etFullName.requestFocus();
			return;
		}
		
		if (TextUtils.isEmpty(email)) {
			etEmail.setError("Email is required");
			etEmail.requestFocus();
			return;
		}
		
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			etEmail.setError("Please enter a valid email");
			etEmail.requestFocus();
			return;
		}
		
		if (TextUtils.isEmpty(password)) {
			etPassword.setError("Password is required");
			etPassword.requestFocus();
			return;
		}
		
		if (password.length() < 6) {
			etPassword.setError("Password must be at least 6 characters");
			etPassword.requestFocus();
			return;
		}
		
		if (TextUtils.isEmpty(confirmPassword)) {
			etConfirmPassword.setError("Please confirm your password");
			etConfirmPassword.requestFocus();
			return;
		}
		
		if (!password.equals(confirmPassword)) {
			etConfirmPassword.setError("Passwords do not match");
			etConfirmPassword.requestFocus();
			return;
		}
		
		if (!cbTerms.isChecked()) {
			Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
			cbTerms.requestFocus();
			return;
		}
		
		// Show progress bar
		progressBar.setVisibility(View.VISIBLE);
		
		// Create user with email and password
		mAuth.createUserWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					// Sign up success, update UI with the signed-in user's information
					FirebaseUser user = mAuth.getCurrentUser();
					
					// Update user profile with display name
					UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
					.setDisplayName(fullName)
					.build();
					
					user.updateProfile(profileUpdates)
					.addOnCompleteListener(new OnCompleteListener<Void>() {
						@Override
						public void onComplete(@NonNull Task<Void> task) {
							if (task.isSuccessful()) {
								// Profile updated successfully
								sendEmailVerification(user);
							}
						}
					});
					} else {
					// If sign up fails, display a message to the user.
					progressBar.setVisibility(View.GONE);
					Toast.makeText(SignUpActivity.this, "Authentication failed: " + task.getException().getMessage(),
					Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void sendEmailVerification(FirebaseUser user) {
		user.sendEmailVerification()
		.addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				progressBar.setVisibility(View.GONE);
				
				if (task.isSuccessful()) {
					Toast.makeText(SignUpActivity.this,
					"Verification email sent to " + user.getEmail(),
					Toast.LENGTH_SHORT).show();
					
					// Redirect to login activity or verification activity
					startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
					finish();
					} else {
					Toast.makeText(SignUpActivity.this,
					"Failed to send verification email: " + task.getException().getMessage(),
					Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null && currentUser.isEmailVerified()) {
			// User is already logged in and verified, redirect to main activity
			startActivity(new Intent(SignUpActivity.this, MainActivity.class));
			finish();
		}
	}
}