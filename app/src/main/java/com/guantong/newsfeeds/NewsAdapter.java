package com.guantong.newsfeeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alien on 4/24/2015.
 */
public class NewsAdapter extends ArrayAdapter<NewsEntity> {

    private Context context;
    private ArrayList<NewsEntity> mResults;

    public NewsAdapter(Context context, ArrayList<NewsEntity> results) {
        super(context, R.layout.activity_news, results);
        this.context = context;
        this.mResults = results;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // Create the view layout if not available
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.new_list_items, null);
        }

        NewsEntity newsEntity = mResults.get(position);

        // Find the image view and assign the image
        TextView title = (TextView)v.findViewById(R.id.titleListTextView);
        TextView desc = (TextView)v.findViewById(R.id.descriptionListTextView);
        ImageView imageViewUrl = (ImageView)v.findViewById(R.id.imageListImageView);

        title.setText(newsEntity.getArticleTitle());
        desc.setText(newsEntity.getArticleDesc());
        imageViewUrl.setImageBitmap(newsEntity.getArticleImageUrl().getBitmap());
//        SquareImageView imageView = (SquareImageView)v.findViewById(R.id.imageView);
//        imageView.setImageBitmap(result.getMedia().getBitmap());

        return v;
    }
}
