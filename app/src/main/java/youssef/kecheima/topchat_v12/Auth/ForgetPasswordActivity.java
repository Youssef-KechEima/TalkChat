package youssef.kecheima.topchat_v12.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import youssef.kecheima.topchat_v12.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText resetPasswordEmail;
    private Button resetPasswordButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        statusBar_and_actionBar_Tool();
        components();
        firebaseAuth=FirebaseAuth.getInstance();
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String mail=resetPasswordEmail.getText().toString().trim();
        if(mail.isEmpty()) {
            resetPasswordEmail.setError("email is required !");
            resetPasswordEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            resetPasswordEmail.setError("Please provide a valid email");
            resetPasswordEmail.requestFocus();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPasswordActivity.this, "Check your Email to reset your password", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ForgetPasswordActivity.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        resetPasswordEmail=findViewById(R.id.Email_ResetPassword);
        resetPasswordButton=findViewById(R.id.ResetPassword_Btn);
    }

    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window=ForgetPasswordActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ForgetPasswordActivity.this,R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.reset_password)+ "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow);
    }
}