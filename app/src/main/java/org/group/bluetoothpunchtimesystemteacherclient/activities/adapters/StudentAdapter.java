package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public boolean isShowCheckbox;

    private Context ctx;

    private RecyclerView recyclerView;

    private List<StudentInformationObject> dataSource;

    public StudentAdapter(Context ctx, RecyclerView recyclerView,
                          List<StudentInformationObject> data) {
        super();
        this.recyclerView = recyclerView;
        this.ctx = ctx;
        this.dataSource = data;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.adapter_student,viewGroup,false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder viewHolder, int i) {
        if(viewHolder instanceof StudentViewHolder) {
            StudentViewHolder studentViewHolder = viewHolder;
            if(isShowCheckbox) {
                if(studentViewHolder.checkBox.getVisibility() != View.VISIBLE)
                    studentViewHolder.checkBox.setVisibility(View.VISIBLE);
            }else {
                if(studentViewHolder.checkBox.getVisibility() != View.GONE)
                studentViewHolder.checkBox.setVisibility(View.GONE);
            }
            StudentInformationObject studentInformationObject = dataSource.get(i);
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
    }

    @Override
    public int getItemCount() {
        int returnCount = 0;
        if(dataSource != null) {
            returnCount = dataSource.size();
        }
        return returnCount;
    }

    class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatCheckBox checkBox;

        public TextView tv_title;

        public TextView tv_sn;

        public TextView tv_mac;

        public LinearLayout ll_main;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_sn = itemView.findViewById(R.id.tv_sn);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            ll_main = itemView.findViewById(R.id.ll_main);
            ll_main.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
