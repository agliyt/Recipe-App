package com.example.recipeapp.helpers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class ItemTapHandler implements View.OnTouchListener {

    public interface Listener {
        void onTap();
        void onDoubleTap();
    }

    private GestureDetector gestureDetector;
    private Listener mListener;

    public ItemTapHandler(Context c, Listener listener) {
        mListener = listener;
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mListener.onDoubleTap();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mListener.onTap();
            return super.onSingleTapConfirmed(e);
        }
    }
}