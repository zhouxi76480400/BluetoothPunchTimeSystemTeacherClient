package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.group.bluetoothpunchtimesystemteacherclient.R;
import org.group.bluetoothpunchtimesystemteacherclient.objects.InformationsClass;

import java.util.List;

public class SessionInformationAdapter extends RecyclerView.Adapter
        <SessionInformationAdapter.SessionInformationViewHolder> {

    private List<InformationsClass> list;

    public SessionInformationAdapter(List<InformationsClass> list) {
        super();
        SessionInformationAdapter.this.list = list;
    }


    @NonNull
    @Override
    public SessionInformationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.layout_session_information_adapter,viewGroup,false);
        return new SessionInformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionInformationViewHolder sessionInformationViewHolder, int i) {
        InformationsClass obj = list.get(i);
        sessionInformationViewHolder.tv_title.setText(String.format("%s %s\n%s",obj.studentInformationObject.last_name,
                obj.studentInformationObject.first_name,obj.studentInformationObject.student_number));
        sessionInformationViewHolder.checkbox.setChecked(obj.isOK);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SessionInformationViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_title;

        public CheckBox checkbox;

        public SessionInformationViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            checkbox = itemView.findViewById(R.id.checkbox);
        }

    }
}
