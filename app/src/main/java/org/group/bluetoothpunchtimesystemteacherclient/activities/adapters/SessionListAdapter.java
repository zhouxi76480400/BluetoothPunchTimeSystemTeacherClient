package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.CreateSessionPOJO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface SessionListAdapterListener {

        boolean isLastPage();

        void onPressed(int position, SessionListAdapter which);

        void onLoad();

    }

    private SessionListAdapterListener listener;

    public void setSessionListAdapterListener(SessionListAdapterListener l) {
        listener = l;
    }

    private final int VIEW_TYPE_OBJECT = 0;

    private final int VIEW_TYPE_PROGRESS = 1;

    private RecyclerView recyclerView;

    private List<CreateSessionPOJO> list;

    private SimpleDateFormat simpleDateFormat;

    public SessionListAdapter(RecyclerView recyclerView, List<CreateSessionPOJO> dataSource) {
        super();
        this.recyclerView = recyclerView;
        this.list = dataSource;
        simpleDateFormat =
                new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z", Locale.getDefault());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int state;
            private boolean isDown;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                state = newState;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(state == RecyclerView.SCROLL_STATE_IDLE &&
                        layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int total_count = linearLayoutManager.getItemCount();
                    if(position == (total_count - 1) && isDown) {
                        if(listener != null) {
                            listener.onLoad();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    isDown = true;
                }else {
                    isDown = false;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(position < list.size())
            return VIEW_TYPE_OBJECT;
        else
            return VIEW_TYPE_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == VIEW_TYPE_OBJECT) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.adapter_session,viewGroup,false);
            viewHolder = new SessionListViewHolder(view);
        }else if (i == VIEW_TYPE_PROGRESS) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.adapter_progress,viewGroup,false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ProgressViewHolder) {
            ProgressViewHolder progressViewHolder = (ProgressViewHolder) viewHolder;
            if(listener.isLastPage()) {
                progressViewHolder.text_view.setText(
                        progressViewHolder.text_view.getContext().getString(R.string.no_more));
                if(progressViewHolder.progress.getVisibility() != View.GONE) {
                    progressViewHolder.progress.setVisibility(View.GONE);
                }
            }else {
                progressViewHolder.text_view.setText(
                        progressViewHolder.text_view.getContext().
                                getString(R.string.get_student_progress_hint));
                if(progressViewHolder.progress.getVisibility() != View.VISIBLE) {
                    progressViewHolder.progress.setVisibility(View.VISIBLE);
                }
            }
        }else if(viewHolder instanceof SessionListViewHolder) {
            CreateSessionPOJO createSessionPOJO = list.get(i);
            SessionListViewHolder sessionListViewHolder = (SessionListViewHolder) viewHolder;
            sessionListViewHolder.tv_title.setText(createSessionPOJO.name);
            sessionListViewHolder.tv_create_time.setText(simpleDateFormat.
                    format(new Date(createSessionPOJO.create_time)));
            sessionListViewHolder.tv_info.setText(
                    String.format(
                            viewHolder.itemView.getContext().getString(R.string.time_and_frequency),
                            createSessionPOJO.time,createSessionPOJO.frequency));
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = list.size();
        int progressCount = 0;
        if(itemCount > 0) {
            progressCount = 1;
        }
        return itemCount + progressCount;
    }

    class SessionListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout ll_main;

        public TextView tv_title;

        public TextView tv_create_time;

        public TextView tv_info;

        public SessionListViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_main = itemView.findViewById(R.id.ll_main);
            ll_main.setOnClickListener(this);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_create_time = itemView.findViewById(R.id.tv_create_time);
            tv_info = itemView.findViewById(R.id.tv_info);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            if(listener != null) {
                listener.onPressed(position,SessionListAdapter.this);
            }
        }

    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progress;

        public TextView text_view;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            progress = itemView.findViewById(R.id.progress);
            text_view = itemView.findViewById(R.id.text_view);
        }
    }
}
