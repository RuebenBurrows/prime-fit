package ruebenburrowsdavies.info.primefittracking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MyRecyclerAdapterTwo extends RecyclerView.Adapter<MyRecyclerAdapterTwo.CustomViewHolder>{

    private List<FeedItem> feedItemList;
    private Context mContext;



    public MyRecyclerAdapterTwo(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, null);
        view.setOnClickListener(new MyOnClickListener());
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int itemPosition = news_two.mRecyclerView2.getChildPosition(v);
            Log.e("Clicked and Position",String.valueOf(itemPosition));

            FeedItem feedItem = feedItemList.get(itemPosition);
            Log.e("Title", String.valueOf(feedItem.getHtmlLink()));
            final String FinalLink = feedItem.getHtmlLink();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FinalLink));
            mContext.startActivity(browserIntent);
        }
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        // LOAD THE IMAGE INTO FEEDVIEW
        Glide.with(mContext)
                .load(feedItemList.get(i).getPicLink())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.button)
                .into(customViewHolder.imgView);

        //Setting text view title
        customViewHolder.textView.setText(feedItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView;
        protected ImageView imgView;

        public CustomViewHolder(View view) {
            super(view);

            this.textView = (TextView) view.findViewById(R.id.des);
            this.imgView = (ImageView)view.findViewById(R.id.img1);
        }
    }

    public class EmptyRecyclerView extends RecyclerView {
        private View emptyView;
        final private AdapterDataObserver observer = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkIfEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                checkIfEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                checkIfEmpty();
            }
        };

        public EmptyRecyclerView(Context context) {
            super(context);
        }

        public EmptyRecyclerView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        void checkIfEmpty() {
            if (emptyView != null && getAdapter() != null) {
                final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
                emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
                setVisibility(emptyViewVisible ? GONE : VISIBLE);
            }
        }

        @Override
        public void setAdapter(Adapter adapter) {
            final Adapter oldAdapter = getAdapter();
            if (oldAdapter != null) {
                oldAdapter.unregisterAdapterDataObserver(observer);
            }
            super.setAdapter(adapter);
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }

            checkIfEmpty();
        }

        public void setEmptyView(View emptyView) {
            this.emptyView = emptyView;
            checkIfEmpty();
        }
    }



}