package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.group.bluetoothpunchtimesystemteacherclient.MyApplication;
import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.StudentInformationObject;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context ctx;

    private RecyclerView recyclerView;

    public StudentAdapter(Context ctx, RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.adapter_student,viewGroup,false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder studentViewholder, int i) {

    }

    @Override
    public int getItemCount() {
        List<StudentInformationObject> list;
        if((list = getDataSource()) != null) {
            return list.size();
        }
        return 0;
    }

    private List<StudentInformationObject> getDataSource() {
        return MyApplication.getInstance().getStudentInformations();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
