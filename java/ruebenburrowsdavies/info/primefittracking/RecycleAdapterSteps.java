package ruebenburrowsdavies.info.primefittracking;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecycleAdapterSteps extends RecyclerView.Adapter<RecycleAdapterSteps.CustomViewHolder>{

    private List<FeedItem> feedItemList;
    private Context mContext;

    public RecycleAdapterSteps(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_step_view, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        //Setting text view title
        customViewHolder.textView.setText(feedItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        public CustomViewHolder(View view) {
            super(view);

            this.textView = (TextView) view.findViewById(R.id.des2);
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
