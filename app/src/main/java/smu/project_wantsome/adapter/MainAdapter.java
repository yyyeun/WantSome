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
import smu.project_wantsome.listener.OnPostListener;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private ArrayList<PostInfo> mDataSet;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private OnPostListener onPostListener;

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataSet) {
        mDataSet = myDataSet;
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataSet.get(position).getContents();

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)) {
            Log.e("로그", "태그");
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            if(contentsList.size() > 0) {
                for (int i=0; i<contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if(Patterns.WEB_URL.matcher(contents).matches()){
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        contentsLayout.addView(imageView);
                        Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                    } else {
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(contents);
                        contentsLayout.addView(textView);
                    }
                }
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
                String id = mDataSet.get(position).getId();

                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        onPostListener.onModify(id);
                        return true;
                    case R.id.delete:
                        onPostListener.onDelete(id);
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

