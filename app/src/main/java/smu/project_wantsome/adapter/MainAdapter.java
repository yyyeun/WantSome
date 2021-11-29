package smu.project_wantsome.adapter;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import smu.project_wantsome.PostInfo;
import smu.project_wantsome.R;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private ArrayList<PostInfo> mDataSet;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MainViewHolder(Activity activity, CardView v, PostInfo postInfo) {
            super(v);
            cardView = v;

            LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<String> contentsList = postInfo.getContents();

        if(contentsLayout.getChildCount() == 0) {
            for (int i=0; i<contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if(Patterns.WEB_URL.matcher(contents).matches()){
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                } else {
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    contentsLayout.addView(textView);
                }
            }
        }
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataSet) {
        mDataSet = myDataSet;
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        final MainViewHolder mainViewHolder = new MainViewHolder(activity, cardView, mDataSet.get(viewType));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataSet.get(position).getTitle());

        TextView createdAtTextView = cardView.findViewById(R.id.createdAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataSet.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ArrayList<String> contentsList = mDataSet.get(position).getContents();

        for (int i=0; i<contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if(Patterns.WEB_URL.matcher(contents).matches()){
                Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into((ImageView)contentsLayout.getChildAt(i));
            } else {
                ((TextView)contentsLayout.getChildAt(i)).setText(contents);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        return true;
                    case R.id.delete:
                        firebaseFirestore.collection("posts").document(mDataSet.get(position).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(activity, "게시글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "게시글을 삭제하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

}

