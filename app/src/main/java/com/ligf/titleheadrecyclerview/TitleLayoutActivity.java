package com.ligf.titleheadrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligf on 2018/3/23.
 */

public class TitleLayoutActivity extends Activity implements OnTitleHeadHeightChangedListener {

    private static final String TAG = "TitleLayoutActivity";

    private TouchListenerRecyclerView mDataRecyclerView;
    private List<String> mDatas = new ArrayList<>();
    private DataAdapter mDataAdapter;

    private RelativeLayout mTitleHeadView;
    private ViewGroup.LayoutParams mTitleHeadLayoutParams;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    private TextView mTitleHeadTV;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mHeadTitleInitHeight;
    private float mHeadTitleTextInitX;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_recyclerview);

        initData();
        initViews();

        getScreenWidthHeight();
    }

    private void initViews(){
        mDataRecyclerView = (TouchListenerRecyclerView) findViewById(R.id.recyclerview_title_layout);
        mDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDataAdapter = new DataAdapter();
        mDataRecyclerView.setAdapter(mDataAdapter);
        //设置回调监听
        mDataRecyclerView.setOnTitleLengthChangeListener(this);

        mTitleHeadView = (RelativeLayout) findViewById(R.id.rl_title_recyclerview_head);
        //获取titleHead的高度
        mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeadTitleInitHeight = mTitleHeadView.getHeight();
                mDataRecyclerView.setHeadTitleHeight(mHeadTitleInitHeight);

                mHeadTitleTextInitX = mTitleHeadTV.getX();
                mTitleHeadView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            }
        };
        mTitleHeadView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        mTitleHeadTV = (TextView) findViewById(R.id.tv_title_recyclerview_head_title);
    }

    private void initData(){
        mDatas.clear();
        for (int i = 0; i < 20; i ++){
            mDatas.add(i + "");
        }
    }

    float totaloffset;
    float totalXoffset;


    @Override
    public void onTitleLengthIncrease(float offset) {
        //手指下滑操作
        //注意：offset都要强制转换为int型(为了保证得到的变化值都是一样的)

        //mTitleHeadView高度随手指下滑而增大
        mTitleHeadLayoutParams = mTitleHeadView.getLayoutParams();
        mTitleHeadLayoutParams.height = mTitleHeadLayoutParams.height + (int)offset;
        mTitleHeadView.setLayoutParams(mTitleHeadLayoutParams);

        Log.i(TAG, "onTitleLengthIncrease mTitleHeadLayoutParams.height:" + mTitleHeadLayoutParams.height);

        //mTitleHeadTV随手指的下滑慢慢移动到布局的中间
        //mTitleHeadTV的x方向的位移与mTitleHeadView在y方向的增大成比例，比例值就是x方向需要移动的总位移/y方向需要增大的总大小
        float delX = (mScreenWidth / 2 - (mTitleHeadTV.getWidth() / 2 + mHeadTitleTextInitX));
        float xOffset = delX / (600 - mHeadTitleInitHeight) * (int)offset;
        mTitleHeadTV.setX(mTitleHeadTV.getX() + xOffset);


        totalXoffset = totalXoffset + xOffset;
        totaloffset = totaloffset + (int)offset;
        Log.i(TAG, "onTitleLengthIncrease total totalXoffset:" + totalXoffset);
        Log.i(TAG, "onTitleLengthIncrease total totaloffset:" + totaloffset);

        //刷新布局
        mTitleHeadView.invalidate();
    }

    @Override
    public void onTitleLengthDecrease(float offset) {
        //手指上滑操作
        //注意：offset都要强制转换为int型

        //mTitleHeadView高度随手指上滑而减小
        mTitleHeadLayoutParams = mTitleHeadView.getLayoutParams();
        mTitleHeadLayoutParams.height = mTitleHeadLayoutParams.height + (int)offset;
        mTitleHeadView.setLayoutParams(mTitleHeadLayoutParams);

        //mTitleHeadTV随手指的上滑慢慢从布局中间移动到原始的地方
        //mTitleHeadTV的x方向的位移与mTitleHeadView在y方向的减小成比例，比例值就是x方向需要移动的总位移/y方向需要减小的总大小
        float delX = (mScreenWidth / 2 - (mTitleHeadTV.getWidth() / 2 + mHeadTitleTextInitX));
        float xOffset = delX / (600 - mHeadTitleInitHeight) * (int)offset;
        mTitleHeadTV.setX(mTitleHeadTV.getX() + xOffset);

        //刷新布局
        mTitleHeadView.invalidate();
    }

    private class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>{

        @Override
        public DataAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list_item_layout, parent, false);
            DataViewHolder dataViewHolder = new DataViewHolder(layout);
            return dataViewHolder;
        }

        @Override
        public void onBindViewHolder(DataAdapter.DataViewHolder holder, int position) {
            Log.i(TAG, "onBindViewHolder position:" + position);
            Log.i(TAG, "onBindViewHolder getItemCount:" + getItemCount());

            holder.contentTV.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas == null ? 0 : mDatas.size();
        }

        class DataViewHolder extends RecyclerView.ViewHolder{

            TextView contentTV;

            public DataViewHolder(View itemView) {
                super(itemView);
                contentTV = (TextView) itemView.findViewById(R.id.tv_data_list_item_content);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "#### onTouchEvent ACTION_DOWN");
//                return true;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "#### onTouchEvent ACTION_MOVE");

                break;

            case MotionEvent.ACTION_UP:
                Log.i(TAG, "#### onTouchEvent ACTION_UP");

                break;
        }
        return super.onTouchEvent(event);
    }

    private void getScreenWidthHeight(){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }



}
