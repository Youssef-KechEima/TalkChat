package youssef.kecheima.topchat_v12.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import youssef.kecheima.topchat_v12.Auth.LoginActivity;
import youssef.kecheima.topchat_v12.Main.HomeActivity;
import youssef.kecheima.topchat_v12.R;

public class ChangeEmailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText newEmail,passwordForEmail;
    private Button updateEmail;
    private FirebaseUser firebaseUser;
    private AuthCredential authCredential;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        components();
        statusBar_and_actionBar_Tool();
        toolbar.setTitle("Modify Email Address");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                updateUserEmail();
            }
        });
    }

    private void updateUserEmail() {
        String mail=newEmail.getText().toString().trim();
        String pass=passwordForEmail.getText().toString().trim();
        if(mail.isEmpty() || pass.isEmpty()) {
            newEmail.setError("email is required !");
            passwordForEmail.setError("Password is required");
            newEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            newEmail.setError("Please provide a valid email");
            newEmail.requestFocus();
            return;
        }
        progressDialog= new ProgressDialog(ChangeEmailActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        authCredential= EmailAuthProvider
                .getCredential(firebaseUser.getEmail(),pass);

        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                firebaseUser.updateEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if (task.isSuccessful()){
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(ChangeEmailActivity.this, "You email has been Updated pllz verify Your Email", Toast.LENGTH_SHORT).show();
                                        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update("email",mail);
                                        firebaseAuth.signOut();
                                        startActivity(new Intent(ChangeEmailActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(ChangeEmailActivity.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(ChangeEmailActivity.this, "Email is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= ChangeEmailActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.purple_700));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void components() {
        toolbar=findViewById(R.id.ToolbarEmailChange);
        newEmail=findViewById(R.id.New_Email);
        passwordForEmail=findViewById(R.id.PasswordforNewEmail);
        updateEmail=findViewById(R.id.UpdateEmail_Btn);
    }
}