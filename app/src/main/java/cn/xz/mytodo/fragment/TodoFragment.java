package cn.xz.mytodo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.R;
import cn.xz.mytodo.entity.Todo;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;

public class TodoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    TodoListAdapter todoListAdapter;
    List<Todo> todoList = new ArrayList();

    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.et_add)
    EditText editTextAdd;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /*private <T extends View> T bindView(int viewId) {
        try {
            return (T) getActivity().findViewById(viewId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public TodoFragment() {
        Todo todo;
        for (int i = 0; i < 5; i++) {
            todo = new Todo();
            int num = i + 1;
            boolean flag = (num % 2 == 0);
            todo.setDesc("内容" + num);
            todo.setIfDone(flag);
            todo.setTitle("标题" + num);
            todo.setId((long) num);
            todo.save();
        }
        String sql = "select * from todo order by if_done,id";
        todoList = Todo.findWithQuery(Todo.class, sql);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Todo todo;
            int now = todoList.size();
            for (int i = 0; i < 5; i++) {
                todo = new Todo();
                int num = i + 1 + now;
                boolean flag = (num % 2 == 0);
                todo.setDesc("内容" + num);
                todo.setIfDone(flag);
                todo.setTitle("标题" + num);
                todo.setId((long) num);
                todo.setDate(new Date());
                todo.save();
            }
            String sql = "select * from todo order by if_done,id";
            todoList = Todo.findWithQuery(Todo.class, sql);
            MLog.log(todoList.size() + "xx" + todoList);
            todoListAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_todo, container, false);
        MLog.log("加载了：TodoFragment");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        //mSwipeRefreshLayout.setBackgroundColor(Color.RED);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),
                R.color.tomato));

        todoListAdapter = new TodoListAdapter();

        listView.setAdapter(todoListAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        editTextAdd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //do something;
                    MToast.Show(getActivity(), "执行完成操作！");
                    return true;
                }
                return false;
            }
        });
        MLog.log("TodoFragment！");
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessageDelayed(1, 500);
    }

    //ViewHolder静态类
    static class ViewHolder {
        public Long todoId;
        public TextView tvTitle;
        public TextView tvDesc;
        public CheckBox cbIfDone;
        public LinearLayout layout;
    }

    public class TodoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return todoList.size();
        }

        @Override
        public Object getItem(int position) {
            return todoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity().getApplicationContext(),
                        R.layout.lv_todo, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                viewHolder.cbIfDone = (CheckBox) convertView.findViewById(R.id.cbIfDone);
                viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
                convertView.setTag(viewHolder);//将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //set data
            Todo todo = todoList.get(position);
            viewHolder.tvTitle.setText(todo.getTitle());//设备名称
            viewHolder.tvDesc.setText(todo.getDesc());//设备编号
            viewHolder.todoId = todo.getId();

            if (todo.getIfDone()) {
                viewHolder.cbIfDone.setChecked(true);
            } else {
                viewHolder.cbIfDone.setChecked(false);
            }

            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MToast.Show(getActivity(), "" + SystemClock.currentThreadTimeMillis());
                }
            });

            return convertView;
        }
    }
}
