package com.github.debacodex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.github.debacodex.R;
public class LoginActivity extends AppCompatActivity {
	
	// UI Components
	private TextInputLayout tilEmail, tilPassword;
	private TextInputEditText etEmail, etPassword;
	private Button btnLogin;
	private TextView tvForgotPassword, tvSignUp;
	
	// Firebase Auth
	private FirebaseAuth mAuth;
	
	// Progress Dialog or Loading State
	private boolean isLoading = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
		
		// Initialize Firebase Auth
		mAuth = FirebaseAuth.getInstance();
		
		// Initialize Views
		initViews();
		
		// Set Click Listeners
		setClickListeners();
		
		// Check if user is already logged in
		checkCurrentUser();
	
	
		
		
	etEmail.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        validateEmail();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
});

etPassword.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        validatePassword();
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
});
}
	private void initViews() {
		tilEmail = findViewById(R.id.tilEmail);
		tilPassword = findViewById(R.id.tilPassword);
		etEmail = findViewById(R.id.etEmail);
		etPassword = findViewById(R.id.etPassword);
		btnLogin = findViewById(R.id.btnLogin);
		tvForgotPassword = findViewById(R.id.tvForgotPassword);
		tvSignUp = findViewById(R.id.tvSignUp);
	}
	
	private void setClickListeners() {
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isLoading) {
					loginUser();
				}
			}
		});
		
		tvForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleForgotPassword();
			}
		});
		
		tvSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Navigate to Sign Up Activity
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void checkCurrentUser() {
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null) {
			// User is already logged in, redirect to main activity
			navigateToMainActivity();
		}
	}
	
	private void loginUser() {
		String email = etEmail.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		
		// Clear previous errors
		clearErrors();
		
		// Validate inputs
		if (!validateInputs(email, password)) {
			return;
		}
		
		// Show loading state
		setLoadingState(true);
		
		// Authenticate with Firebase
		mAuth.signInWithEmailAndPassword(email, password)
		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				setLoadingState(false);
				
				if (task.isSuccessful()) {
					// Login successful
					FirebaseUser user = mAuth.getCurrentUser();
					
					if (user != null) {
						if (user.isEmailVerified()) {
							// Email is verified, proceed to main activity
							Toast.makeText(LoginActivity.this, "Login successful!",
							Toast.LENGTH_SHORT).show();
							navigateToMainActivity();
							} else {
							// Email not verified
							Toast.makeText(LoginActivity.this,
							"Please verify your email address first.",
							Toast.LENGTH_LONG).show();
							mAuth.signOut();
						}
					}
					} else {
					// Login failed
					handleLoginError(task.getException());
				}
			}
		});
	}
	
	

private void validateEmail() {
    String email = etEmail.getText().toString().trim();
    if (!isValidEmail(email)) {
        tilEmail.setError("Please enter a valid email address");
    } else {
        tilEmail.setError(null);
    }
}

private void validatePassword() {
    String password = etPassword.getText().toString().trim();
    if (password.length() < 6 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
        tilPassword.setError("Password must be at least 6 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
    } else {
        tilPassword.setError(null);
    }
}
	private boolean validateInputs(String email, String password) {
		boolean isValid = true;
		
		// Email validation
		if (TextUtils.isEmpty(email)) {
			tilEmail.setError("Email is required");
			isValid = false;
			} else if (!isValidEmail(email)) {
			tilEmail.setError("Please enter a valid email address");
			isValid = false;
		}
		
		// Password validation
		if (TextUtils.isEmpty(password)) {
			tilPassword.setError("Password is required");
			isValid = false;
			} else if (password.length() < 6) {
			tilPassword.setError("Password must be at least 6 characters");
			isValid = false;
			} else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
			tilPassword.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
			isValid = false;
		}
		
		return isValid;
	}
	
	private boolean isValidEmail(String email) {
		return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	private void clearErrors() {
		tilEmail.setError(null);
		tilPassword.setError(null);
	}
	
	private void setLoadingState(boolean loading) {
		isLoading = loading;
		btnLogin.setEnabled(!loading);
		btnLogin.setText(loading ? "SIGNING IN..." : "LOGIN");
		
		// Optionally disable other clickable elements during loading
		tvForgotPassword.setEnabled(!loading);
		tvSignUp.setEnabled(!loading);
		etEmail.setEnabled(!loading);
		etPassword.setEnabled(!loading);
	}
	
	private void handleLoginError(Exception exception) {
		String errorMessage = "Login failed. Please try again.";
		
		if (exception != null) {
			String exceptionMessage = exception.getMessage();
			
			if (exceptionMessage != null) {
				if (exceptionMessage.contains("user-not-found")) {
					errorMessage = "No account found with this email address.";
					tilEmail.setError("Account not found");
					} else if (exceptionMessage.contains("wrong-password")) {
					errorMessage = "Incorrect password.";
					tilPassword.setError("Incorrect password");
					} else if (exceptionMessage.contains("invalid-email")) {
					errorMessage = "Invalid email address format.";
					tilEmail.setError("Invalid email format");
					} else if (exceptionMessage.contains("user-disabled")) {
					errorMessage = "This account has been disabled.";
					} else if (exceptionMessage.contains("too-many-requests")) {
					errorMessage = "Too many failed attempts. Please try again later.";
					} else if (exceptionMessage.contains("network-request-failed")) {
					errorMessage = "Network error. Please check your connection.";
				}
			}
		}
		
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
	}
	
	private void handleForgotPassword() {
		String email = etEmail.getText().toString().trim();
		
		if (TextUtils.isEmpty(email)) {
			tilEmail.setError("Please enter your email address first");
			etEmail.requestFocus();
			return;
		}
		
		if (!isValidEmail(email)) {
			tilEmail.setError("Please enter a valid email address");
			etEmail.requestFocus();
			return;
		}
		
		// Clear any existing errors
		tilEmail.setError(null);
		
		// Show loading state
		setLoadingState(true);
		
		mAuth.sendPasswordResetEmail(email)
		.addOnCompleteListener(new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				setLoadingState(false);
				
				if (task.isSuccessful()) {
					Toast.makeText(LoginActivity.this,
					"Password reset email sent to " + email,
					Toast.LENGTH_LONG).show();
					} else {
					String errorMessage = "Failed to send reset email. Please try again.";
					if (task.getException() != null &&
					task.getException().getMessage() != null) {
						String exceptionMessage = task.getException().getMessage();
						if (exceptionMessage.contains("user-not-found")) {
							errorMessage = "No account found with this email address.";
						}
					}
					Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void navigateToMainActivity() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Check if user is signed in and update UI accordingly
		FirebaseUser currentUser = mAuth.getCurrentUser();
		if (currentUser != null && currentUser.isEmailVerified()) {
			navigateToMainActivity();
		}
	}
	
	// Optional: Handle back press to prevent going back to login after successful login
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finishAffinity(); // Close all activities in the task
	}
}
