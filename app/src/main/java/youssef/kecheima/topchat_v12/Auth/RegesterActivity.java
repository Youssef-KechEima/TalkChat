package youssef.kecheima.topchat_v12.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import youssef.kecheima.topchat_v12.Main.HomeActivity;
import youssef.kecheima.topchat_v12.Model.User;
import youssef.kecheima.topchat_v12.R;

public class RegesterActivity extends AppCompatActivity {
    private EditText firstName,lastName,email,password;
    private TextView termes;
    private Button regester;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private RadioButton selectedRadioButton;
    private DatabaseReference statusRef;

    //MAIN Methode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regester);
        statusBar_and_actionBar_Tool();
        components();
        setUpHyperLink();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        statusRef= FirebaseDatabase.getInstance().getReference("UserStatus");
        regester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName=firstName.getText().toString().trim();
                String lName=lastName.getText().toString().trim();
                String mail=email.getText().toString().trim();
                String pass=password.getText().toString();
                if((fName.equals("") || fName.length()<2)|| (lName.equals("") || lName.length()<2)|| (mail.equals("") || mail.length()<2) || pass.equals(""))
                {
                    Toast.makeText(RegesterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<8){
                    Toast.makeText(RegesterActivity.this, "Password must contains a least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else{

                    regester(fName,lName,mail,pass);
                }
            }
        });
    }


    //Regester Methode
    private void regester(String fName,String lName,String mail,String pass){
        firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   progressDialog= new ProgressDialog(RegesterActivity.this);
                   progressDialog.show();
                   progressDialog.setContentView(R.layout.progress_dialog);
                   progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();
                    if(firebaseUser!=null) {
                        String userId = firebaseUser.getUid();
                        User users=new User(userId,fName+" "+lName,"default",mail,"","Hello there i'm using TalkChat");
                        statusRef.child(firebaseUser.getUid()).child("status").setValue("online");
                        firebaseFirestore.collection("Users").document(userId)
                                .set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                           finish();
                                        }
                                        else {
                                            Toast.makeText(RegesterActivity.this, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }


                }
                else{
                    Toast.makeText(RegesterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //Make TextView to HyperLink
    private void setUpHyperLink(){
        termes.setMovementMethod(LinkMovementMethod.getInstance());
}
    //Cast Components
    private void components() {
        firstName=findViewById(R.id.FristName);
        lastName=findViewById(R.id.LastName);
        email=findViewById(R.id.Email_Regester);
        password=findViewById(R.id.Password_Regester);
        termes=findViewById(R.id.Termes);
        regester=findViewById(R.id.Regester_Btn);
    }
    // Back Methode
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to stop creating your account");
        builder.setMessage("if you stop now, you'll lose any progress you've made.");
        builder.setPositiveButton(Html.fromHtml("<font color=\"purple\">Stop Creating Account</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (item.getItemId()){
                    case android.R.id.home:
                        finish();
                        break;
                }
            }
        });
        builder.setNegativeButton(Html.fromHtml("<font color=\"black\">Continue Creating Account</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
        return super.onOptionsItemSelected(item);
    }
    // StatusBar and ActionBar
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool(){
        Window window=RegesterActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(RegesterActivity.this,R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.Create_New_Account)+ "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backarrow);
    }

}