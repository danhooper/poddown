package com.danhooper.poddown;

import java.text.DateFormat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FeedAdapter extends BaseAdapter {
    Context context;
    FeedList feedList;
    private static final String TAG = "PodDownFeedAdapter";

    public FeedAdapter(Context context) {
        this.context = context;
        feedList = new FeedList(context);
    }

    public int getCount() {
        return feedList.getFeeds().size();
    }

    public Feed getItem(int position) {
        return feedList.getFeeds().get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v(TAG, "In getView " + position + " of " + getCount());
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.feed_row, null);
        }

        Feed f = getItem(position);

        if (f != null) {
            TextView podcastName = (TextView) v.findViewById(R.id.podcastName);
            TextView podcastExtra = (TextView) v
                    .findViewById(R.id.podcastExtra);

            if (podcastName != null) {
                podcastName.setText(f.name);
            }
            if (podcastExtra != null) {
                String lastChecked = "Last Checked: ";
                if (f.lastChecked != null) {

                    Log.v(TAG, "last checked is not null");
                    DateFormat dateFormat = DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM, DateFormat.SHORT);
                    lastChecked += dateFormat.format(f.lastChecked);
                } else {
                    Log.v(TAG, "last checked is null");
                    lastChecked += "never";
                }
                podcastExtra.setText("URL: " + f.url + " " + lastChecked);
            }
        }
        return v;

    }

    @Override
    public void notifyDataSetChanged() {
        feedList = new FeedList(context);
        super.notifyDataSetChanged();
    }

}
