package youssef.kecheima.topchat_v12.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import youssef.kecheima.topchat_v12.Settings.UserProfileActivity;

public class FirebaseServices {
    private Context context;
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseServices(Context context) {
        this.context = context;
    }

    public void UploadImagetoFirebaseStorage(Uri uri,OnCallBack onCallBack){
        StorageReference riverref= FirebaseStorage.getInstance().getReference().child("sentImages/"+firebaseUser.getUid()+"/"+System.currentTimeMillis()+"."+GetFileExtension(uri));
        riverref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downlaodUri=urlTask.getResult();
                final String sdownload=String.valueOf(downlaodUri);
               onCallBack.OnUploadSuccess(sdownload);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.OnUploadFailed(e);
            }
        });
    }
    private String GetFileExtension(Uri uri){
        ContentResolver contentResolver=context.getContentResolver();
        MimeTypeMap mimeTypeMap =MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public interface OnCallBack{
        void OnUploadSuccess(String imagesUrl);
        void OnUploadFailed(Exception e);
    }
}
