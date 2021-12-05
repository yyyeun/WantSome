package smu.project_wantsome.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.Image;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import smu.project_wantsome.PostInfo;
import smu.project_wantsome.R;
import smu.project_wantsome.activity.PostActivity;
import smu.project_wantsome.listener.OnPostListener;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private ArrayList<PostInfo> mDataSet;
    private Activity activity;
    private OnPostListener onPostListener;

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataSet) {
        this.mDataSet = myDataSet;
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;

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

        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataSet.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
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

        TextView addressTextView = cardView.findViewById(R.id.addressTextView);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(mDataSet.get(position).getPublisher());
        documentReference.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            addressTextView.setText(document.getData().get("address").toString());
        });

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ArrayList<String> contentsList = mDataSet.get(position).getContents();

        String wpContents = contentsList.get(1);
        if(wpContents != null) {
            TextView wantproductMainTextView = cardView.findViewById(R.id.wantproductMainTextView);
            wantproductMainTextView.setText(wpContents);
        }

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)) {
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();

            if(contentsList.size() > 2) {
                contentsLayout.setVisibility(View.VISIBLE);
                String contents = contentsList.get(2);
                //if(Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/project--wantsome.appspot.com/o/posts"))
                //{
                ImageView imageView = new ImageView(activity);
                imageView.setAdjustViewBounds(true);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                contentsLayout.addView(imageView);
                Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
            } else {
                contentsLayout.setVisibility(View.GONE);
            }
            //}
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
                String id = mDataSet.get(position).getId();

                switch (menuItem.getItemId()) {
                    case R.id.mainModify:
                        onPostListener.onModify(position);
                        return true;
                    case R.id.mainDelete:
                        onPostListener.onDelete(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post_main, popup.getMenu());
        popup.show();
    }

}

