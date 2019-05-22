package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface StudentAdapterListener {

        void onLoad();

    }

    private StudentAdapterListener listener;

    public void setStudentAdapterListener(StudentAdapterListener l) {
        listener = l;
    }

    private final int TYPE_STUDENT = 0;

    private final int TYPE_PROGRESS = 1;

    public boolean isShowCheckbox;

    private Context ctx;

    private RecyclerView recyclerView;

    private List<StudentInformationObject> dataSource;

    private Map<Integer,Boolean> selectedMap;

    public void cleanMap() {
        selectedMap.clear();
    }

    public Map<Integer, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public StudentAdapter(Context ctx, RecyclerView recyclerView,
                          List<StudentInformationObject> data) {
        super();
        this.recyclerView = recyclerView;
        this.ctx = ctx;
        this.dataSource = data;
        init();
    }

    private void init() {
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
        selectedMap = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        @LayoutRes int res_id = 0;
        if(i == TYPE_PROGRESS) {
            res_id = R.layout.adapter_progress;
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(res_id,viewGroup,false);
            viewHolder = new ProgressViewHolder(view);
        }else if(i == TYPE_STUDENT){
            res_id = R.layout.adapter_student;
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(res_id,viewGroup,false);
            viewHolder = new StudentViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == dataSource.size()) {
            return TYPE_PROGRESS;
        }else {
            return TYPE_STUDENT;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof StudentViewHolder) {
            StudentViewHolder studentViewHolder = (StudentViewHolder) viewHolder;
            if(isShowCheckbox) {
                if(studentViewHolder.checkBox.getVisibility() != View.VISIBLE)
                    studentViewHolder.checkBox.setVisibility(View.VISIBLE);
            }else {
                if(studentViewHolder.checkBox.getVisibility() != View.GONE)
                studentViewHolder.checkBox.setVisibility(View.GONE);
            }
            StudentInformationObject studentInformationObject = dataSource.get(i);
            if(studentInformationObject != null) {
                studentViewHolder.tv_title.setText(String.format("%s:%s %s:%s",
                        viewHolder.itemView.getContext().getString(R.string.last_name),
                        studentInformationObject.last_name,
                        viewHolder.itemView.getContext().getString(R.string.first_name),
                        studentInformationObject.first_name));
                studentViewHolder.tv_sn.setText(String.format("%s:%s",
                        viewHolder.itemView.getContext().getString(R.string.student_number),
                        studentInformationObject.student_number));
                studentViewHolder.tv_mac.setText(String.format("%s:%s",
                        viewHolder.itemView.getContext().getString(R.string.mac_address),
                        studentInformationObject.mac_address));
            }
        }else if (viewHolder instanceof ProgressViewHolder) {


        }
    }

    @Override
    public int getItemCount() {
        int returnCount = 0;
        if(dataSource != null) {
            returnCount = dataSource.size();
        }
        if(returnCount != 0) {
            returnCount ++;
        }
        return returnCount;
    }

    class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            CheckBox.OnCheckedChangeListener {

        public AppCompatCheckBox checkBox;

        public TextView tv_title;

        public TextView tv_sn;

        public TextView tv_mac;

        public LinearLayout ll_main;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(this);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_sn = itemView.findViewById(R.id.tv_sn);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            ll_main = itemView.findViewById(R.id.ll_main);
            ll_main.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(isShowCheckbox) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getLayoutPosition();
            selectedMap.put(position,isChecked);
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
