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
import youssef.kecheima.topchat_v12.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText oldPassword,newPassword;
    private Button updatePassword;
    private FirebaseUser firebaseUser;
    private AuthCredential authCredential;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        components();
        statusBar_and_actionBar_Tool();
        toolbar.setTitle("Change the Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();


        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                UpdatePassword();
            }
        });
    }

    private void UpdatePassword() {
        String oldPass=oldPassword.getText().toString().trim();
        String newPass=newPassword.getText().toString().trim();
        if(oldPass.isEmpty() || newPass.isEmpty()) {
            oldPassword.setError("Old Password is required !");
            newPassword.setError(" new Password is required");
            oldPassword.requestFocus();
            return;
        }
        progressDialog= new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        authCredential= EmailAuthProvider
                .getCredential(firebaseUser.getEmail(),oldPass);
        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                                finish();
                            }
                            else {

                                Toast.makeText(ChangePasswordActivity.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(ChangePasswordActivity.this, "Old Password Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void components() {
        toolbar=findViewById(R.id.ToolbarChangePassword);
        oldPassword=findViewById(R.id.OldPassword);
        newPassword=findViewById(R.id.New_Password);
        updatePassword=findViewById(R.id.UpdatePassword_Btn);
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= ChangePasswordActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ChangePasswordActivity.this,R.color.purple_700));
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
}