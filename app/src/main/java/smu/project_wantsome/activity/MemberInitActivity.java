package smu.project_wantsome.activity;

import static smu.project_wantsome.Util.showToast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import smu.project_wantsome.MemberInfo;
import smu.project_wantsome.R;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private RelativeLayout loaderLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        loaderLayout = findViewById(R.id.loaderLayout);
        Button checkButton = findViewById(R.id.checkButton);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageUploader();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void storageUploader() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
        final String chat = ((EditText) findViewById(R.id.chatEditText)).getText().toString();

        if (name.length() > 0 && address.length() > 0 && chat.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            user = FirebaseAuth.getInstance().getCurrentUser();

            MemberInfo memberInfo = new MemberInfo(name, address, chat);
            storeUploader(memberInfo);
        } else {
            showToast(MemberInitActivity.this, "회원정보를 보내는데 실패했습니다.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showToast(MemberInitActivity.this, "회원 정보 등록을 성공하였습니다.");
                    loaderLayout.setVisibility(View.GONE);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(MemberInitActivity.this, "회원 정보 등록에 실패하였습니다.");
                    loaderLayout.setVisibility(View.GONE);
                    Log.w(TAG, "Error writing document", e);
                }
            });
    }
}