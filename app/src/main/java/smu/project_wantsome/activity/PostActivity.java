package smu.project_wantsome.activity;

import static smu.project_wantsome.Util.showToast;
import static smu.project_wantsome.Util.storageUriToName;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import smu.project_wantsome.PostInfo;
import smu.project_wantsome.R;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private int successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        TextView wantProductTextView = (TextView) findViewById(R.id.wantProductTextView);
        TextView createdAtTextView = (TextView) findViewById(R.id.createdAtTextView);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(postInfo.getPublisher());
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            String text = (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt())) + " | " + document.getData().get("address").toString();
            createdAtTextView.setText(text);
        });

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = postInfo.getContents();

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)) {
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();

            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (i == 0) { // 내용
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextSize(18);
                    textView.setPadding(0, 20, 0, 20);
                    contentsLayout.addView(textView);
                } else if(i == 1) { // 원하는 물품
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("원하는 물건 : " + contents);
                    textView.setPadding(0, 20, 0, 20);
                    wantProductTextView.setText(contents);
                    contentsLayout.addView(textView);
                } else { // 이미지
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                }
            }
        }

        Button buttonOpenChat = (Button) findViewById(R.id.buttonOpenChat);

        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            buttonOpenChat.setText("작성자 오픈채팅방으로 이동하기");
            buttonOpenChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(document.getData().get("chat").toString()));
                    startActivity(intent);
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.delete:
                storageDelete(postInfo);
                //firebaseHelper.storageDelete(postInfo);
                finish();
                return true;

            case R.id.modify:
                myStartActivity(WritePostActivity.class, postInfo);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void storageDelete(PostInfo postInfo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();
        for (int i=2; i<contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if(Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/project--wantsome.appspot.com/o/posts")) {
                successCount++;
                StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUriToName(contents));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(PostActivity.this, "ERROR");
                    }
                });
            }
        }
        storeDelete(id);
    }

    private void storeDelete(String id) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if(successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(PostActivity.this, "게시글을 삭제하였습니다.");
                            //onPostListener.onDelete();
                            //postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(PostActivity.this, "게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }
}
