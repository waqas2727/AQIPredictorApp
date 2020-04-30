package com.example.aqipredictor;

import com.google.android.gms.maps.SupportMapFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;


public class WorkaroundMapFragment extends SupportMapFragment {

    public OnTouchListener mListener;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);
        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup) layout).addView(frameLayout, new LayoutParams(-1, -1));
        return layout;
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        public boolean dispatchTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (action == 0) {
                WorkaroundMapFragment.this.mListener.onTouch();
            } else if (action == 1) {
                WorkaroundMapFragment.this.mListener.onTouch();
            }
            return super.dispatchTouchEvent(event);
        }
    }

    public void setListener(OnTouchListener listener) {
        this.mListener = listener;
    }
}
