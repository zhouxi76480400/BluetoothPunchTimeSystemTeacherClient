package org.group.bluetoothpunchtimesystemteacherclient.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.view.CollapsibleActionView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class RemoveUserView extends LinearLayout implements CollapsibleActionView {

    public RemoveUserView(Context context) {
        super(context);
        initView();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        Log.e("test",changed+","+l+","+t+","+r+","+b);
        linearLayout.getLayoutParams().width = r - l;
        linearLayout.getLayoutParams().height = b - t;
    }

    LinearLayout linearLayout;

    private void initView() {
        ViewGroup.LayoutParams layoutParams = new
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setBackgroundColor(Color.RED);
        addView(linearLayout);
    }

    @Override
    public void onActionViewExpanded() {
        Log.e("test","onActionViewExpanded");
        if(linearLayout != null) {
            Log.e("test","aaaaaaa");

        }
    }

    @Override
    public void onActionViewCollapsed() {
        Log.e("test","onActionViewCollapsed");

    }
}
