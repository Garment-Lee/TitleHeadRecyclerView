# TitleHeadRecyclerView
　　仿照网易的有道词典的专栏详情的上拉下来效果。

## 特点

	1. 继承RecyclerView，重写onTouchEvent()事件，根据不同情况执行不同的操作。
  
	2. 使用LayoutParams改变View参数，实现View的大小改变。
  
	3. 使用setX()方法，改变View的坐标，实现View的移动动画。
  
	4. 可把RecyclerView替换为其他类型的View，如ScrollerView、ListView等。

## 实现思路


	1. 实现TitleHead随着手指移动进行高度缩放的效果，需要拦截RecyclerView的滑动事件，由于RecyclerView的滑动操作
    是在onTouchEvent()方法中实现的，所以通过继承RecyclerView，重写onTouchEvent()方法，根据TitleHead的高度判断，
    是执行RecyclerView的滑动操作（调用父类的onTouchEvent()），还是执行TitleHead的高度缩放，这样相当于模拟事件拦截机制。
    
    2. 除了实现TitleHead的高度缩放，还可以添加不同的动画效果，如移动动画，渐变动画等，这时抽象出动画执行接口，
    有Activity实现动画，然后把实现对象传到TitleHeadRecyclerView，执行动画。

## 模拟事件拦截机制

#### 手指下滑时
    TitleHeadRecyclerView的onTouchEvent()中，当RecyclerView最顶端处于可见，且TitleHead的高度小于最大高度时，
    执行TitleHead的高度缩放动画；否则执行super.onTouchEvent()。

#### 手指上滑时
    TitleHeadRecyclerView的onTouchEvent()方法中，当TitleHead的高度小于最大的高度，并且大于最小的高度（原始高度）
    时，执行TitleHead的高度缩放动画，否则执行super.onTouchEvent()。


## 效果如下


## 用法

#### xml如下

``` java
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android";
    android:layout_width="match_parent"
    android:layout_height="match_parent">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:id="@+id/rl_title_recyclerview_head">
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerVertical="true"
                android:text="i am the head title"
                android:textSize="20sp"
                android:id="@+id/tv_title_recyclerview_head_title"
                android:gravity="center"/>
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:id="@+id/tv_title_recyclerview_title_one"
                android:layout_alignParentBottom="true"
                android:layout_centerHorizontal="true"
                android:layout_marginBottom="20dp"
                android:text="i am the test textview one"
                android:textSize="15sp"
                android:visibility="gone"/>
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:id="@+id/tv_title_recyclerview_title_two"
                android:layout_alignParentBottom="true"
                android:layout_centerHorizontal="true"
                android:layout_marginBottom="40dp"
                android:text="i am the test textview two"
                android:textSize="15sp"
                android:visibility="gone"/>
        </RelativeLayout>
        <com.ligf.titleheadrecyclerview.TouchListenerRecyclerView
            android:id="@+id/recyclerview_title_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_below="@id/rl_title_recyclerview_head">
        </com.ligf.titleheadrecyclerview.TouchListenerRecyclerView>
</RelativeLayout>


```

#### activity实现高度缩放动画接口

``` java
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


```


