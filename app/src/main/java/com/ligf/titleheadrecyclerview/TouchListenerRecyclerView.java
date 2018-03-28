package com.ligf.titleheadrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ligf on 2018/3/23.
 * <p>
 * RecyclerView事件拦截机制：
 * RecyclerView拖动时，会拦截事件。在onTouchEvent()方法中完成拖动的动作，
 */

public class TouchListenerRecyclerView extends RecyclerView {

    private static final String TAG = "TouchRecyclerView";

    private OnTitleHeadHeightChangedListener onTitleHeadHeightChangedListener;

    private int headTitleHeight;

    public TouchListenerRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Log.i(TAG, "#### onInterceptTouchEvent ACTION_DOWN....");

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "#### onInterceptTouchEvent ACTION_MOVE....");
                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "#### onInterceptTouchEvent ACTION_UP....");

                break;
        }
        return super.onInterceptTouchEvent(e);
//        return true;
    }

    private float initY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "#### onTouchEvent ACTION_DOWN....");
                initY = e.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "#### onTouchEvent ACTION_MOVE....");
                float currentY = e.getRawY();
                float offset = currentY - initY;
                Log.i(TAG, "#### onTouchEvent ACTION_MOVE offset:." + offset + ",getY():" + getY());

                //手指上滑
                if (offset < 0 && getY() > headTitleHeight) {
                    if (onTitleHeadHeightChangedListener != null) {
                        onTitleHeadHeightChangedListener.onTitleLengthDecrease(offset);
                    }
                    initY = currentY;
                    return false;
                }
                //手指下滑
                if (offset > 0 && isRecyclerViewOnTop() && getY() < 600) {
                    Log.i(TAG, "onTitleLengthIncrease total getY:" + getY());

                    if (onTitleHeadHeightChangedListener != null) {
                        onTitleHeadHeightChangedListener.onTitleLengthIncrease(offset);
                    }
                    initY = currentY;
                    return false;
                }
                initY = currentY;

                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "#### onTouchEvent ACTION_UP....");

                break;
        }
        return super.onTouchEvent(e);
    }

    public void setOnTitleLengthChangeListener(OnTitleHeadHeightChangedListener onTitleHeadHeightChangedListener) {
        this.onTitleHeadHeightChangedListener = onTitleHeadHeightChangedListener;
    }

    public void setHeadTitleHeight(int headTitleHeight) {
        this.headTitleHeight = headTitleHeight;
    }

    private boolean isRecyclerViewOnTop() {
        View firstChild = getChildAt(0);
        boolean shouldIntercept;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition == 0 && firstChild.getTop() == 0) {
            shouldIntercept = true;
        } else {
            shouldIntercept = false;
        }
        return shouldIntercept;
    }
}
