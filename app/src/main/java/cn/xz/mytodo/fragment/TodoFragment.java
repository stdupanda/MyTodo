package cn.xz.mytodo.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xz.mytodo.R;
import cn.xz.mytodo.TodoActivity;
import cn.xz.mytodo.entity.Todo;
import cn.xz.mytodo.util.MDate;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;

public class TodoFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final int RESULT_ADD_OK = 1;
    public static final int RESULT_OPERATE_NONE = 2;
    public static final int RESULT_EDIT_OK = 3;

    public static final int REQUEST_CODE_ADD_TODO = 1;
    public static final int REQUEST_CODE_VIEW_TODO = 2;
    public static final int REQUEST_CODE_EDIT_TODO = 2;

    public static final String INTENT_EXTRA_NAME_OPERATION = "INTENT_EXTRA_NAME_OPERATION";
    public static final String INTENT_EXTRA_NAME_TODO_ID = "INTENT_EXTRA_NAME_TODO_ID";
    public static final int INTENT_EXTRA_VALUE_ADD = 1;
    public static final int INTENT_EXTRA_VALUE_VIEW = 2;
    public static final int INTENT_EXTRA_VALUE_EDIT = 3;

    private static final int TYPE_TODO = 0;
    private static final int TYPE_BUTTON = 1;

    /**
     * 显示未完成
     */
    private static final int WHAT_REFRESH = 0;
    /**
     * 显示全部
     */
    private static final int WHAT_REFRESH_ALL = 1;

    public static int buttonState = WHAT_REFRESH_ALL;//默认全部

    TodoListAdapter todoListAdapter;
    List<Todo> todoList = new ArrayList<>();

    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.et_add)
    TextView editTextAdd;

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
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REFRESH: {
                    Todo t = new Todo();
                    t.setIsButton(true);
                    String sql = "select * from todo where 1=1 and is_del=0 and if_done=0 " +
                            "order by if_done asc,expire_date desc,id desc ";
                    todoList = Todo.findWithQuery(Todo.class, sql);
                    todoList.add(t);
                    todoListAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                }
                case WHAT_REFRESH_ALL: {
                    Todo t = new Todo();
                    t.setIsButton(true);
                    String sql = "select * from todo where 1=1 and is_del=0 " +
                            "order by if_done asc,expire_date desc,id desc ";
                    todoList = Todo.findWithQuery(Todo.class, sql);
                    for (int i = 0; i < todoList.size(); i++) {
                        if (todoList.get(0).getIfDone()) {//第一个就是已完成的
                            todoList.add(0, t);
                            break;
                        }
                        if (i < todoList.size() - 1) {
                            Todo tmp = todoList.get(i + 1);//当前是“未完成”
                            if (tmp.getIfDone()) {//list中下一个元素是“已完成”，则插入一个button
                                todoList.add(i + 1, t);
                                break;
                            }
                        }
                        if ((todoList.size() - 1) == i) {//当前是最后一个
                            if (!todoList.get(i).getIfDone()) {//且当前是“未完成”
                                todoList.add(t);
                                break;
                            }
                        }
                    }
                    //MLog.log(todo_list.size() + "xx" + todo_list);
                    todoListAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                }
                default: {
                    break;
                }
            }
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
        /*mSwipeRefreshLayout.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.flat_default));*/
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(),
                R.color.tomato));

        todoListAdapter = new TodoListAdapter();

        listView.setAdapter(todoListAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        editTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTodoActivity(INTENT_EXTRA_VALUE_ADD, 0L, REQUEST_CODE_ADD_TODO);
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        handler.sendEmptyMessage(buttonState);
        MLog.log("TodoFragment！");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.log("requestCode> " + requestCode + "; resultCode> " + resultCode);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (RESULT_OPERATE_NONE == resultCode) {
            //MLog.log("没有操作todo，直接返回");
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_ADD_TODO: {
                if (RESULT_ADD_OK == resultCode) {
                    MToast.Show(getActivity(), getString(R.string.add_ok));
                    mSwipeRefreshLayout.setRefreshing(true);
                    handler.sendEmptyMessage(TodoFragment.buttonState);
                }
                break;
            }
            case REQUEST_CODE_EDIT_TODO: {
                if (RESULT_EDIT_OK == resultCode) {
                    MToast.Show(getActivity(), getString(R.string.edit_ok));
                    mSwipeRefreshLayout.setRefreshing(true);
                    handler.sendEmptyMessage(TodoFragment.buttonState);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessageDelayed(buttonState, 500);
    }

    //ViewHolder静态类
    static class ViewHolderTodo {
        public Long todoId;
        public TextView tvTitle;
        public TextView tvDesc;
        public CheckBox cbIfDone;
        public CheckBox cbIfStar;
        public LinearLayout layout;
    }

    //ViewHolder静态类
    static class ViewHolderButton {
        public Long btnId;
        public TextView tvText;
        public LinearLayout layout;
    }

    public class TodoListAdapter extends BaseAdapter {

        /**
         * 返回多少个不同的布局<p>目前是两种，一种是lv_todo，一种是lv_todo_btn</p>
         *
         * @return 多少个不同的布局
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * 根据position返回相应的Item<p>
         * type的值必须从0开始 </p>
         *
         * @param position
         * @return 布局类型
         */
        @Override
        public int getItemViewType(int position) {
            Todo todo = todoList.get(position);
            if (todo.getIsButton()) {
                return TYPE_BUTTON;
            } else {
                return TYPE_TODO;
            }
        }

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderTodo viewHolderTodo;
            ViewHolderButton viewHolderButton;

            Todo todo = todoList.get(position);
            int type = getItemViewType(position);

            if (null != convertView) {//已缓存
                //MLog.log("已缓存" + position);
                switch (type) {
                    case TYPE_TODO: {
                        viewHolderTodo = (ViewHolderTodo) convertView.getTag();
                        setTodo(viewHolderTodo, todo);
                        break;
                    }
                    case TYPE_BUTTON: {
                        viewHolderButton = (ViewHolderButton) convertView.getTag();
                        setButton(position, viewHolderButton);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {//未缓存，需初始化
                //MLog.log("未缓存" + position);
                viewHolderTodo = new ViewHolderTodo();
                viewHolderButton = new ViewHolderButton();
                switch (type) {
                    case TYPE_TODO: {
                        convertView = View.inflate(getActivity(),
                                R.layout.lv_todo, null);
                        viewHolderTodo.todoId = 0L;
                        viewHolderTodo.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                        viewHolderTodo.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                        viewHolderTodo.cbIfDone = (CheckBox) convertView.findViewById(R.id.cb_if_done);
                        viewHolderTodo.cbIfStar = (CheckBox) convertView.findViewById(R.id.cb_if_star);
                        viewHolderTodo.layout = (LinearLayout) convertView.findViewById(R.id.layout);
                        convertView.setTag(viewHolderTodo);//将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag

                        setTodo(viewHolderTodo, todo);
                        break;
                    }
                    case TYPE_BUTTON: {
                        convertView = View.inflate(getActivity(),
                                R.layout.lv_todo_btn, null);
                        viewHolderButton.tvText = (TextView) convertView.findViewById(R.id.tv_if_show);
                        viewHolderButton.layout = (LinearLayout) convertView.findViewById(R.id.lv_show_finished);
                        convertView.setTag(viewHolderButton);//将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag

                        convertView.setBackgroundColor(Color.TRANSPARENT);
                        setButton(position, viewHolderButton);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            return convertView;
        }

        private void setButton(final int position, final ViewHolderButton viewHolderButton) {
            if (position == (todoList.size() - 1)) {//正好是最后一个
                viewHolderButton.tvText.setText(getString(R.string.show_finished));
            } else {
                viewHolderButton.tvText.setText(getString(R.string.hide_finished));
            }

            //viewHolderButton.layout.setBackgroundColor(Color.RED);
            viewHolderButton.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefreshLayout.setRefreshing(true);

                    if (getString(R.string.hide_finished).equals(
                            viewHolderButton.tvText.getText())) {
                        handler.sendEmptyMessage(WHAT_REFRESH);
                        buttonState = WHAT_REFRESH;
                    }
                    if (getString(R.string.show_finished).equals(
                            viewHolderButton.tvText.getText())) {
                        handler.sendEmptyMessage(WHAT_REFRESH_ALL);
                        buttonState = WHAT_REFRESH_ALL;
                    }
                }
            });
        }

        private void setTodo(final ViewHolderTodo viewHolderTodo, final Todo todo) {
            viewHolderTodo.todoId = todo.getId();
            viewHolderTodo.tvTitle.setText(todo.getTitle());//名称
            Date expireDate = todo.getExpireDate();
            String dateStr = "";
            if (null != expireDate) {
                Calendar cld = Calendar.getInstance();
                cld.setTime(todo.getExpireDate());

                dateStr = MDate.formatDate(expireDate);
                //MLog.log("before>>>" + dateStr);
                dateStr = parseDateStr(cld);
                dateStr = " " + dateStr;
                //MLog.log("after>>>" + dateStr);
            }

            String desc = todo.getDesc().replace("\n", "");
            //MLog.log(desc);
            desc = desc.length() < 10 ? desc : desc.substring(0, 10) + "...";
            viewHolderTodo.tvDesc.setText(desc + " " + dateStr);//编号
            viewHolderTodo.cbIfDone.setChecked(todo.getIfDone());
            viewHolderTodo.cbIfStar.setChecked(todo.getIfStar());

            viewHolderTodo.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goTodoActivity(INTENT_EXTRA_VALUE_VIEW, todo.getId(), REQUEST_CODE_VIEW_TODO);
                }
            });

            viewHolderTodo.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = View.inflate(getActivity(), R.layout.view_dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true)
                            .setView(view);
                    final AlertDialog alertDialog = builder.create();
                    TextView tvDel = (TextView) view.findViewById(R.id.tv_dialog_del);
                    TextView tvEdit = (TextView) view.findViewById(R.id.tv_dialog_edit);

                    tvDel.setOnClickListener(new View.OnClickListener() {//删除
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                            b.setCancelable(false)
                                    .setMessage("确定要删除吗？")
                                    .setTitle("提示")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            todo.setIstDel(true);
                                            todo.save();
                                            MToast.Show(getActivity(), getString(R.string.del_todo_ok));
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            handler.sendEmptyMessage(TodoFragment.buttonState);
                                        }
                                    }).setNegativeButton("否", null);
                            AlertDialog a = b.create();
                            a.show();

                            alertDialog.dismiss();
                        }
                    });

                    tvEdit.setOnClickListener(new View.OnClickListener() {//跳转到编辑界面
                        @Override
                        public void onClick(View v) {
                            goTodoActivity(INTENT_EXTRA_VALUE_EDIT, todo.getId(), REQUEST_CODE_EDIT_TODO);
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    return false;
                }
            });

            viewHolderTodo.cbIfDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MLog.log(">>>>>>>>>>>>>>>>" + viewHolderTodo.cbIfDone.isChecked());
                    mSwipeRefreshLayout.setRefreshing(true);
                    todo.setIfDone(viewHolderTodo.cbIfDone.isChecked());
                    todo.save();
                    //SystemClock.sleep(500);
                    if (WHAT_REFRESH == buttonState) {
                        handler.sendEmptyMessage(WHAT_REFRESH);
                    } else if (WHAT_REFRESH_ALL == buttonState) {
                        handler.sendEmptyMessage(WHAT_REFRESH_ALL);
                    }
                }
            });

            viewHolderTodo.cbIfStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    todo.setIfStar(viewHolderTodo.cbIfStar.isChecked());
                    todo.save();
                    if (WHAT_REFRESH == buttonState) {
                        handler.sendEmptyMessage(WHAT_REFRESH);
                    } else if (WHAT_REFRESH_ALL == buttonState) {
                        handler.sendEmptyMessage(WHAT_REFRESH_ALL);
                    }
                }
            });
        }
    }

    private void goTodoActivity(int intentNameOperation, long todoId, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), TodoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_EXTRA_NAME_OPERATION, intentNameOperation);
        bundle.putLong(INTENT_EXTRA_NAME_TODO_ID, todoId);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
        //getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private String parseDateStr(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(calendar.getTime());

        Calendar cldQt = Calendar.getInstance();
        Calendar cldZt = Calendar.getInstance();
        Calendar cldJt = Calendar.getInstance();
        Calendar cldMt = Calendar.getInstance();
        Calendar cldHt = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        cldQt.set(Calendar.HOUR_OF_DAY, 0);
        cldQt.set(Calendar.MINUTE, 0);
        cldQt.set(Calendar.SECOND, 0);
        cldZt.set(Calendar.HOUR_OF_DAY, 0);
        cldZt.set(Calendar.MINUTE, 0);
        cldZt.set(Calendar.SECOND, 0);
        cldJt.set(Calendar.HOUR_OF_DAY, 0);
        cldJt.set(Calendar.MINUTE, 0);
        cldJt.set(Calendar.SECOND, 0);
        cldMt.set(Calendar.HOUR_OF_DAY, 0);
        cldMt.set(Calendar.MINUTE, 0);
        cldMt.set(Calendar.SECOND, 0);
        cldHt.set(Calendar.HOUR_OF_DAY, 0);
        cldHt.set(Calendar.MINUTE, 0);
        cldHt.set(Calendar.SECOND, 0);

        cldQt.add(Calendar.DAY_OF_MONTH, -2);//前天
        cldZt.add(Calendar.DAY_OF_MONTH, -1);//昨天
        cldMt.add(Calendar.DAY_OF_MONTH, 1);//明天
        cldHt.add(Calendar.DAY_OF_MONTH, 2);//后天

        //int qt = calendar.compareTo(cldQt);//此处不可以，即便是前天也返回-1，而不是-2

        if (sdf.format(calendar.getTime()).equals(sdf.format(cldQt.getTime()))) {
            dateStr = "前天";
        } else if (sdf.format(calendar.getTime()).equals(sdf.format(cldZt.getTime()))) {
            dateStr = "昨天";
        } else if (sdf.format(calendar.getTime()).equals(sdf.format(cldJt.getTime()))) {
            dateStr = "今天";
        } else if (sdf.format(calendar.getTime()).equals(sdf.format(cldMt.getTime()))) {
            dateStr = "明天";
        } else if (sdf.format(calendar.getTime()).equals(sdf.format(cldHt.getTime()))) {
            dateStr = "后天";
        }
        return dateStr;
    }

    @OnClick(R.id.btn_test)
    void test() {
        String ss = getActivity().getFilesDir().getAbsolutePath();
        String ss2 = getActivity().getDatabasePath("tod9o.db").getAbsolutePath();
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        MLog.log(ss);
        MLog.log(ss2);
        MLog.log(sdcardPath);
        MToast.Show(getActivity(), ss2);
        File file = new File(ss2);
        if (file.exists()) {
            MLog.log(file.getAbsoluteFile() + " 存在。");
        } else {
            MLog.log(file.getAbsoluteFile() + " 不存在。");
        }

        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
