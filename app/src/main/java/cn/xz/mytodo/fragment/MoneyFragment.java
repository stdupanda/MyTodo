package cn.xz.mytodo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.R;
import cn.xz.mytodo.entity.Cost;
import cn.xz.mytodo.entity.CostType;
import cn.xz.mytodo.entity.Todo;
import cn.xz.mytodo.util.MDate;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;

public class MoneyFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private static final int TYPE_COST = 0;
    public List<CostType> costTypeList = new ArrayList<>();

    String newLine = System.getProperty("line.separator");
    /**
     * 显示全部
     */
    private static final int WHAT_REFRESH_ALL = 1;

    CostListAdapter costListAdapter;
    List<Cost> costList = new ArrayList<>();

    @BindView(R.id.list_view_cost)
    ListView listView;
    @BindView(R.id.et_add_cost)
    TextView tvAdd;

    @BindView(R.id.swipeRefreshLayout_cost)
    SwipeRefreshLayout mSwipeRefreshLayout;

    /*private <T extends View> T bindView(int viewId) {
        try {
            return (T) getActivity().findViewById(viewId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public MoneyFragment() {
        //
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REFRESH_ALL: {
                    String sql = "select * from cost where 1=1 and is_del=0 /*and if_done=0*/ " +
                            "order by cost_date desc,id desc,cost_type ";
                    costList = Todo.findWithQuery(Cost.class, sql);
                    costListAdapter.notifyDataSetChanged();
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
        View view = inflater.inflate(R.layout.frg_cost, container, false);
        MLog.log("加载了：MoneyFragment");
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

        costListAdapter = new CostListAdapter();

        listView.setAdapter(costListAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = View.inflate(getActivity(), R.layout.view_cost, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true)
                        .setView(view);
                final AlertDialog alertDialog = builder.create();
                TextView tvAdd = (TextView) view.findViewById(R.id.tv_dialog_del);
                tvAdd.setCompoundDrawablePadding(10);

                tvAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = 0;
                        EditText etCount = (EditText) view.findViewById(R.id.et_cost_count);
                        if (TextUtils.isEmpty(etCount.getText())) {//检测是否输入金额
                            MToast.Show(getActivity(),
                                    getString(R.string.please_insert_cost_count));
                            etCount.requestFocus();
                            return;
                        }
                        count = Integer.parseInt(etCount.getText().toString());

                        EditText etTitle = (EditText) view.findViewById(R.id.et_cost_title);
                        String title = "";
                        title = etTitle.getText().toString();

                        Spinner spinner = (Spinner) view.findViewById(R.id.sp_cost_type);
                        String cost_type = spinner.getSelectedItem().toString();

                        //新增 消费 记录
                        Cost cost = new Cost();
                        cost.setCostDate(new Date());//当天日期
                        cost.setCostTitle(title.trim());
                        cost.setCostType(cost_type);
                        cost.setCount(count);
                        cost.setIsDel(false);
                        cost.save();
                        MToast.Show(getActivity(), "添加成功！");
                        alertDialog.dismiss();
                        hideKeyboard();
                        mSwipeRefreshLayout.setRefreshing(true);
                        handler.sendEmptyMessage(MoneyFragment.WHAT_REFRESH_ALL);
                    }
                });
                alertDialog.show();
                Spinner spinner = (Spinner) view.findViewById(R.id.sp_cost_type);
                loadDataToSpinner(spinner, alertDialog);
                EditText etCount = (EditText) view.findViewById(R.id.et_cost_count);
                etCount.requestFocus();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
        handler.sendEmptyMessage(WHAT_REFRESH_ALL);
        MLog.log("MoneyFragment！");
    }

    private void loadDataToSpinner(Spinner spinner, AlertDialog alertDialog) {
        String sql = "select * from cost_type where 1=1 and is_del=0 and type_name is not null" +
                " order by type_name, id desc";
        costTypeList = CostType.findWithQuery(CostType.class, sql);
        if (1 > costTypeList.size()) {
            MToast.Show(getActivity(), "请先在“更多”中创建“账目类型”！");
            alertDialog.dismiss();
            return;
        }
        String[] arr = new String[costTypeList.size()];
        int i = 0;
        for (CostType tmp : costTypeList) {
            arr[i] = tmp.getTypeName();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MLog.log("requestCode> " + requestCode + "; resultCode> " + resultCode);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessageDelayed(WHAT_REFRESH_ALL, 500);
    }

    //ViewHolder静态类
    static class ViewHolderCost {
        public Long id;
        public LinearLayout layout;
        public TextView title;
        public TextView desc;
    }

    public class CostListAdapter extends BaseAdapter {

        /**
         * 返回多少个不同的布局<p>目前是1种
         *
         * @return 多少个不同的布局
         */
        @Override
        public int getViewTypeCount() {
            return 1;
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
            return TYPE_COST;
        }

        @Override
        public int getCount() {
            return costList.size();
        }

        @Override
        public Object getItem(int position) {
            return costList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderCost viewHolderCost;

            Cost cost = costList.get(position);
            int type = getItemViewType(position);

            if (null != convertView) {//已缓存
//                MLog.log("已缓存" + position);
                switch (type) {
                    case TYPE_COST: {
                        viewHolderCost = (ViewHolderCost) convertView.getTag();
                        setCost(viewHolderCost, cost);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {//未缓存，需初始化
//                MLog.log("未缓存" + position);
                viewHolderCost = new ViewHolderCost();
                switch (type) {
                    case TYPE_COST: {
                        convertView = View.inflate(getActivity(),
                                R.layout.lv_cost, null);
                        viewHolderCost.id = 0L;
                        viewHolderCost.title = (TextView) convertView.findViewById(R.id.tv_title);
                        viewHolderCost.desc = (TextView) convertView.findViewById(R.id.tv_desc);
                        viewHolderCost.layout = (LinearLayout) convertView.findViewById(R.id.layout_cost);
                        //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                        convertView.setTag(viewHolderCost);

                        setCost(viewHolderCost, cost);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            return convertView;
        }

        /**
         * 初始化listview中的布局界面控件
         *
         * @param viewHolderCost 对应缓存类
         * @param cost           对应db中存储的实体类
         */
        private void setCost(final ViewHolderCost viewHolderCost, final Cost cost) {
            viewHolderCost.id = cost.getId();
            String costType = cost.getCostType();
            if (TextUtils.isEmpty(costType)) {
                costType = "暂无";
            }
            viewHolderCost.title.setText(costType + "," + cost.getCount());//名称
            Date date = cost.getCostDate();
            String dateStr = "";
            if (null != date) {
                Calendar cld = Calendar.getInstance();
                cld.setTime(cost.getCostDate());

                dateStr = parseDateStr(cld);
                dateStr = " " + dateStr;
            }
            viewHolderCost.desc.setText(cost.getCostTitle() + "," + dateStr);

            viewHolderCost.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String info = "日期：" + MDate.formatDate(cost.getCostDate()) + newLine
                            + "类型：" + cost.getCostType() + newLine
                            + "金额：￥" + cost.getCount() + newLine
                            + "信息：" + cost.getCostTitle();
                    MToast.Show(getActivity(), info);
                    return;
                }
            });

            viewHolderCost.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = View.inflate(getActivity(), R.layout.view_dialog_cost, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true)
                            .setView(view);
                    final AlertDialog alertDialog = builder.create();
                    TextView tvDel = (TextView) view.findViewById(R.id.tv_dialog_del);
                    tvDel.setCompoundDrawablePadding(10);

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
                                            cost.setIsDel(true);
                                            cost.save();
                                            MToast.Show(getActivity(), getString(R.string.del_cost_ok));
                                            mSwipeRefreshLayout.setRefreshing(true);
                                            handler.sendEmptyMessage(MoneyFragment.WHAT_REFRESH_ALL);
                                        }
                                    }).setNegativeButton("否", null);
                            AlertDialog a = b.create();
                            a.show();

                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    return false;
                }
            });
        }
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
}
