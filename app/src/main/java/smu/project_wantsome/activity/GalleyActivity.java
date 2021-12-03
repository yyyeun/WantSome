package smu.project_wantsome.activity;

import static smu.project_wantsome.Util.showToast;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import smu.project_wantsome.R;
import smu.project_wantsome.adapter.GalleryAdapter;

public class GalleyActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (ContextCompat.checkSelfPermission(GalleyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleyActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleyActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                showToast(GalleyActivity.this, "권한을 허용해 주세요.");
            }
        }else{
            recyclerInit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                } else {
                    finish();
                    showToast(GalleyActivity.this, "권한을 허용해 주세요.");
                }
            }
        }
    }

    private void recyclerInit() {
        final int numberOfColumns = 3;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;

        // 비디오 추가 -> 11번 강의 6분 ~
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
