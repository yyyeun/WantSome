package smu.project_wantsome.activity;

import static smu.project_wantsome.Util.INTENT_PATH;
import static smu.project_wantsome.Util.showToast;
import static smu.project_wantsome.Util.storageUriToName;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import smu.project_wantsome.R;
import smu.project_wantsome.PostInfo;
import smu.project_wantsome.Util;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageView;
    private EditText titleEditText;
    private EditText contentEditText;
    private EditText wantProductEditText;
    private PostInfo postInfo;
    private Util util;
    private int pathCount, successCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        setToolbarTitle("?????????");

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLayout);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        wantProductEditText = findViewById(R.id.wantProductEditText);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.imageDelete).setOnClickListener(onClickListener);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postInit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    parent.addView(linearLayout);

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });

                    Glide.with(this).load(path).override(1000).into(imageView);
                    linearLayout.addView(imageView);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View)selectedImageView.getParent()) - 2, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageView);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartActivity(GalleyActivity.class, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleyActivity.class, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.imageDelete:
                    final View selectedView = (View) selectedImageView.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 2);
                    Log.e("path", ""+path);
                    if(Patterns.WEB_URL.matcher(path).matches() && path.contains("https://firebasestorage.googleapis.com/v0/b/project--wantsome.appspot.com/o/posts")) {
                        StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + storageUriToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(WritePostActivity.this, "????????? ?????????????????????.");
                                pathList.remove(parent.indexOfChild(selectedView) - 2);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(WritePostActivity.this, "????????? ??????????????? ?????????????????????.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 2);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                    }
                } else if(!Patterns.WEB_URL.matcher(pathList.get(pathCount)).matches()){
                    String path = pathList.get(pathCount);
                    successCount++;
                    contentsList.add(path);
                    String[] pathArray = path.split("\\.");

                    final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "."+ pathArray[pathArray.length - 1]);

                    Log.e("mountainImagesRef", " : " + mountainImagesRef);
                    try {
                        InputStream stream = new FileInputStream(new File(path));
                        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size()-1)).build();
                        UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentsList.set(index, uri.toString());
                                        successCount--;
                                        if(successCount == 0){
                                            PostInfo writeInfo = new PostInfo(title, contentsList, user.getUid(), date);
                                            storeUpload(documentReference, writeInfo);
                                        }
                                    }
                                });
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("??????", "??????: " + e.toString());
                    }
                    pathCount++;
                }else if (Patterns.WEB_URL.matcher(pathList.get(pathCount)).matches()) {
                    String path = pathList.get(pathCount);
                    contentsList.add(path);
                    pathCount++;
                }
            }
            if (successCount == 0) {
                storeUpload(documentReference, new PostInfo(title, contentsList, user.getUid(), date));
            }
        } else {
            startToast("????????? ??????????????????.");
        }
    }

    private void storeUpload(DocumentReference documentReference, PostInfo writeInfo){
        documentReference.set(writeInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    loaderLayout.setVisibility(View.GONE);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                    loaderLayout.setVisibility(View.GONE);
                }
            });
    }

    private void postInit() {
        if(postInfo != null) {
            titleEditText.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContents();
            contentEditText.setText(contentsList.get(0));
            wantProductEditText.setText(contentsList.get(1));

            for (int i=2; i<contentsList.size(); i++) {
                String contents = contentsList.get(i);
                pathList.add(contents);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                parent.addView(linearLayout);

                ImageView imageView = new ImageView(WritePostActivity.this);
                imageView.setLayoutParams(layoutParams);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                        selectedImageView = (ImageView) view;
                    }
                });
                Glide.with(this).load(contents).override(1000).into(imageView);
                linearLayout.addView(imageView);
            }

            for (int i=0; i<pathList.size(); i++) {
                Log.e("??????", "pathList : "+pathList.get(i));
            }
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c, int requestCode) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, requestCode);
    }
}
