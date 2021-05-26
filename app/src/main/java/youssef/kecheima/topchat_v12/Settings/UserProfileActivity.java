package youssef.kecheima.topchat_v12.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import youssef.kecheima.topchat_v12.Model.Common;
import youssef.kecheima.topchat_v12.R;

public class UserProfileActivity extends AppCompatActivity {
    private TextView txt_Name,txt_Email,Gender,txtDesc,txtAbout;
    private CircleImageView UserImage;
    private FloatingActionButton addImage;
    private Toolbar toolbar;
    private LinearLayout edit,editAbout;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private BottomSheetDialog bottomSheetDialog,bsdEditName,bsdEditAbout;
    private ProgressDialog progressDialog;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageUri;
    private ImageView genderLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        components();
        statusBar_and_actionBar_Tool();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(firebaseUser!=null)
        getData();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPick();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomsheetEditName();
            }
        });

        editAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomsheetEditAbout();
            }
        });

        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserImage.invalidate();
                Drawable dr=UserImage.getDrawable();
                Common.IMAGE_BITMAP=((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this,UserImage,"image");
                Intent intent =new Intent(UserProfileActivity.this,DisplayPictureActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());
            }
        });

    }

    private void showBottomsheetEditAbout() {
        View view=getLayoutInflater().inflate(R.layout.botton_sheet_editabout,null);
        EditText editAbout=view.findViewById(R.id.EditAbout);
        ((View)view.findViewById(R.id.CancelAbout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsdEditAbout.dismiss();
            }
        });
        ((View)view.findViewById(R.id.SaveAbout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(editAbout.getText().toString())){
                    Toast.makeText(UserProfileActivity.this, "Desc can not be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    UpdateAbout(firebaseUser.getUid(),editAbout.getText().toString());
                    bsdEditAbout.dismiss();
                }
            }
        });

        bsdEditAbout=new BottomSheetDialog(this,R.style.BottomSheetStyle);
        bsdEditAbout.setContentView(view);
        bsdEditAbout.getBehavior().setHideable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bsdEditAbout.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bsdEditAbout.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsdEditAbout=null;
            }
        });
        bsdEditAbout.show();

    }

    private void UpdateAbout(String uid, String about) {
        progressDialog= new ProgressDialog(UserProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        firebaseFirestore.collection("Users").document(uid).update("desc",about).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                getData();
            }
        });
    }

    private void showBottomsheetEditName() {
        View view=getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);
        EditText editFirstName=view.findViewById(R.id.EditFirstName);
        EditText editLastName=view.findViewById(R.id.EditLastName);
        ((View) view.findViewById(R.id.Cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsdEditName.dismiss();

            }
        });

        ((View) view.findViewById(R.id.Save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NewFirstname=editFirstName.getText().toString();
                String NewLastName=editLastName.getText().toString();
                if(TextUtils.isEmpty(NewFirstname) || TextUtils.isEmpty(NewLastName)) {
                    Toast.makeText(UserProfileActivity.this, "Name can not be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    UpdateNewUserName(firebaseUser.getUid(), NewFirstname+" "+NewLastName);
                    bsdEditName.dismiss();
                }

            }
        });

        bsdEditName=new BottomSheetDialog(this,R.style.BottomSheetStyle);
        bsdEditName.setContentView(view);
        bsdEditName.getBehavior().setHideable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bsdEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bsdEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsdEditName=null;
            }
        });
        bsdEditName.show();
    }

    private void UpdateNewUserName(String userId,String newName) {
        progressDialog= new ProgressDialog(UserProfileActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        firebaseFirestore.collection("Users").document(userId).update("user_name",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                getData();

            }
        });
    }

    private void showBottomSheetPick() {
        View view=getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);
        ((View) view.findViewById(R.id.Gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();

            }
        });

        ((View) view.findViewById(R.id.Camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfileActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });
        bottomSheetDialog.show();
    }

    private void openGallery() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            UploadToFireBase();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                UserImage.setImageBitmap(bitmap);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadToFireBase() {
        if(imageUri!=null){
            progressDialog= new ProgressDialog(UserProfileActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            StorageReference riverref= FirebaseStorage.getInstance().getReference().child("ImageProfile/"+firebaseUser.getUid()+"/"+System.currentTimeMillis()+"."+GetFileExtension(imageUri));
            riverref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                        Uri downlaodUri=urlTask.getResult();
                        final String sdownload=String.valueOf(downlaodUri);
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("imageUrl",sdownload);
                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();

                            getData();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getData() {
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
              txt_Name.setText(documentSnapshot.get("user_name").toString());
              txt_Email.setText(documentSnapshot.get("email").toString());
              txtDesc.setText(documentSnapshot.get("desc").toString());
              txtAbout.setText(documentSnapshot.get("desc").toString());
              if(documentSnapshot.get("sexe").equals("Male")){
                  Gender.setText(documentSnapshot.get("sexe").toString());
                  genderLogo.setImageResource(R.drawable.male);
              }
              else{
                  Gender.setText(documentSnapshot.get("sexe").toString());
                  genderLogo.setImageResource(R.drawable.female);
              }
              if(documentSnapshot.get("imageUrl").equals("default"))
                  UserImage.setImageResource(R.drawable.empty_user);
              else
                Glide.with(UserProfileActivity.this).load(documentSnapshot.get("imageUrl")).into(UserImage);
            }
        });
    }

    private void components() {
        toolbar=findViewById(R.id.barSatus);
        txt_Name=findViewById(R.id.ProfileUserName);
        txt_Email=findViewById(R.id.ProfileEmail);
        UserImage=findViewById(R.id.UserProfilePicture);
        addImage=findViewById(R.id.UploadPicture);
        edit=findViewById(R.id.btn_editName);
        Gender=findViewById(R.id.profileGender);
        genderLogo=findViewById(R.id.logoGender);
        txtDesc=findViewById(R.id.ProfileAboutBelowName);
        txtAbout=findViewById(R.id.ProfileAbout);
        editAbout=findViewById(R.id.btn_editAbout);
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
    @SuppressLint("NewApi")
    private void statusBar_and_actionBar_Tool() {
        Window window= UserProfileActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(UserProfileActivity.this,R.color.purple_700));
    }
}