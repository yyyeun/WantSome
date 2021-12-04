package smu.project_wantsome.adapter;

import static smu.project_wantsome.Util.INTENT_PATH;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import smu.project_wantsome.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private ArrayList<String> mDataSet;
    private Activity activity;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public GalleryAdapter(Activity activity, ArrayList<String> myDataSet) {
        mDataSet = myDataSet;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery, parent, false);

        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(INTENT_PATH, mDataSet.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder holder, int position) {
        ImageView imageView = holder.cardView.findViewById(R.id.imageView);
        Glide.with(activity).load(mDataSet.get(position)).centerCrop().override(500).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

