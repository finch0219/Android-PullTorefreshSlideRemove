package com.ichapter.testlistviewrefresh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CustomListView.IXListViewListener, OnItemClickListener, SlideView.OnSlideListener {

    private CustomListView mListView;

    private MyAdapter adapter;
    private Handler mHandler;
    private List<MessageItem> list;

    private SlideView mLastSlideViewWithStatusOn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<MessageItem>();
        mListView = (CustomListView) findViewById(R.id.techan_xListView);// 这个listview是在这个layout里面
        mListView.setPullLoadEnable(true);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        list = getData();
        adapter = new MyAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setXListViewListener(this);
        mHandler = new Handler();
        mListView.setOnItemClickListener(this);
        getTotalHeightofListView(mListView);
    }

    String data[] = new String[]{"马云", "马化腾", "雷军",
            "罗永浩", "任正非"};
    String data1[] = new String[]{"明天纽约股市开盘，老马你融资不", "老马，看看再说", "明天下午召开内部新品发布预告会",
            "我卖的不是手机，是情怀", "华为"};

    /* private ArrayList<HashMap<String, MessageItem>> getData() {
         for (int i = 0; i < data.length; i++) {
             HashMap<String, MessageItem> map = new HashMap<String, MessageItem>();
             MessageItem item = new MessageItem();
             item.setTitle(data[i]);
             item.setContent(data1[i]);
             map.put("item", item);
             list.add(map);
         }
         return list;
     }*/

    public static void getTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        Log.i("Logcat:", mAdapter + "");
        Log.i("Logcat", "countL"+mAdapter.getCount());
        if (mAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            //mView.measure(0, 0);
            totalHeight += mView.getMeasuredHeight();
            Log.i("Logcat" + i, String.valueOf(totalHeight));
        }
        Log.i("Logcat", "DividerHeight:"+listView.getDividerHeight());
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public List<MessageItem> getData() {
        for (int i = 0; i < data.length; i++) {
            MessageItem item = new MessageItem();
            item.setTitle(data[i]);
            item.setContent(data1[i]);
            //这里是添加到list里 不能重新开辟新的List对象集合来存储
            list.add(item);
        }
        return list;
    }

    /**
     * 停止刷新
     */
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                mListView.setAdapter(adapter);
                onLoad();
            }
        }, 2000);
    }

    // 加载更多
    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreData();
                adapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }

    public List<MessageItem> loadMoreData() {
        for (int i = 0; i < 2; i++) {
            MessageItem item = new MessageItem();
            item.setTitle(data[i]);
            item.setContent(data1[i]);
            list.add(item);
        }
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "您点击了" + list.get(position - 1).getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSlide(View view, int status) {
        int height = view.getHeight();
        //如果滑出的item删除部分不是Null的情况下也就是滑出删除布局了，就禁止滑动其他item出现删除布局
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }
        //状态一致就可以滑出删除滑块
        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }


    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder;
            SlideView slideView = (SlideView) convertView;
            if (convertView == null) {
                View view = mInflater.inflate(R.layout.scenic_item_list, parent, false);

                slideView = new SlideView(MainActivity.this);
                slideView.setContentView(view);
                holder = new ViewHolder(slideView);
                slideView.setOnSlideListener(MainActivity.this);
                slideView.setTag(holder);

            } else {
                holder = (ViewHolder) slideView.getTag();
            }

            final MessageItem item = list.get(position);
            item.slideView = slideView;
            item.slideView.shrink();

            holder.tv_title.setText(item.getTitle());
            holder.tv_content.setText(item.getContent());
            holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    Toast.makeText(mContext, "删除" + item.getTitle() + "成功", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });
            return slideView;
        }
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_content;
        ViewGroup deleteHolder;

        ViewHolder(View view) {
            tv_title = (TextView) view.findViewById(R.id.title);
            tv_content = (TextView) view.findViewById(R.id.content);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }

    public class MessageItem {
        public int iconRes;
        public String title;
        public String content;
        public String time;
        public SlideView slideView;

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
