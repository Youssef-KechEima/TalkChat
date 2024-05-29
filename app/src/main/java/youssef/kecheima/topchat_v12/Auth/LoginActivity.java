package youssef.kecheima.topchat_v12.Auth;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.Set;

import youssef.kecheima.topchat_v12.Main.HomeActivity;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

public class LoginActivity extends AppCompatActivity {
   private TextView forgetPassword;
    private EditText email,password;
    private Button createAccount,login,btn_Google;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 100;

    //MAIN Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        statusBar_and_actionBar_Tool();
        components();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        btn_Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.Id_Client))
                        .requestEmail()
                        .build();
                // Build a GoogleSignInClient with the options
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegesterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=email.getText().toString().trim();
                String pass = password.getText().toString();
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog= new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    firebaseAuth.signInWithEmailAndPassword(mail,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(LoginActivity.this, "pllz verify your email to log in to your Account", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Authentifiation failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google sign-in was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Get the user's ID token and authenticate with Firebase
                String idToken = account.getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog= new ProgressDialog(LoginActivity.this);
                                    progressDialog.show();
                                    progressDialog.setContentView(R.layout.progress_dialog);
                                    Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                                    // Sign-in success, update UI with the signed-in user's information
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String displayName = user.getDisplayName();
                                    String displayEmail = user.getEmail();
                                    String displayID = user.getUid();
                                    String PhotoUrl = String.valueOf(user.getPhotoUrl());
                                    User users = new User(displayID, displayName, PhotoUrl, displayEmail, "", "Hello, I'm Using TalkChat");
                                    firebaseFirestore.collection("Users").document(displayID).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                            finish();
                                        }
                                    });


                                } else {
                                    // Sign-in failed, display a message to the user
                                    Toast.makeText(LoginActivity.this, "Sign-in failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                // Google sign-in failed
                Toast.makeText(LoginActivity.this, "Google Sign-in Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //StatusBAr and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=LoginActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.BlueImages));
    }

    //Cast Components
    private void components(){
        email=findViewById(R.id.email_Login);
        password=findViewById(R.id.password_Login);
        login=findViewById(R.id.Btn_Login);
        forgetPassword=findViewById(R.id.forgrt_Password);
        createAccount=findViewById(R.id.Btn_regester);
        btn_Google = findViewById(R.id.btn_Google);

    }
}