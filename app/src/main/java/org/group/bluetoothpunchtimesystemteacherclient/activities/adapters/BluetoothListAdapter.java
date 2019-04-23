package org.group.bluetoothpunchtimesystemteacherclient.activities.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.group.bluetoothpunchtimesystemteacherclient.R;

import java.util.List;

public class BluetoothListAdapter extends
        RecyclerView.Adapter<BluetoothListAdapter.BluetoothListAdapterViewHolder> {

    public interface OnBluetoothDeviceSelectedListener {

        void onBluetoothDeviceSelected(BluetoothListAdapter which,BluetoothDevice bluetoothDevice,
                                       int position);

    }

    private Context context;

    private List<BluetoothDevice> list;

    private OnBluetoothDeviceSelectedListener listener;

    public void setBluetoothDeviceSelectedListener(OnBluetoothDeviceSelectedListener newListener) {
        this.listener = newListener;
    }

    public BluetoothListAdapter(Context context,List<BluetoothDevice> dataSource) {
        super();
        this.context = context;
        this.list = dataSource;
    }

    @NonNull
    @Override
    public BluetoothListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.adapter_bluetooth_list,viewGroup,false);
        return new BluetoothListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BluetoothListAdapterViewHolder bluetoothListAdapterViewholder, int i) {
        BluetoothDevice bluetoothDevice = list.get(i);
        String name = bluetoothDevice.getName();
        if(name == null) {
            name = context.getString(R.string.no_name);
        }
        bluetoothListAdapterViewholder.tv_name.setText(name);
        String mac_address = bluetoothDevice.getAddress();
        bluetoothListAdapterViewholder.tv_mac.setText(mac_address);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class BluetoothListAdapterViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_name;

        public TextView tv_mac;

        public LinearLayout linear_layout;

        BluetoothListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            linear_layout = itemView.findViewById(R.id.linear_layout);
            linear_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null) {
                int position = getLayoutPosition();
                BluetoothDevice bluetoothDevice = list.get(position);
                listener.onBluetoothDeviceSelected(BluetoothListAdapter.this, bluetoothDevice,
                        position);
            }
        }
    }

}
