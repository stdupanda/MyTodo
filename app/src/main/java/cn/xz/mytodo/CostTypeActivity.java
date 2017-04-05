package cn.xz.mytodo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xz.mytodo.entity.CostType;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;

public class CostTypeActivity extends Activity {

    private static final int TYPE = 0;
    public List<CostType> costTypeList = new ArrayList<>();
    public CostTypeListAdapter costTypeListAdapter = null;

    @BindView(R.id.lv_cost_type)
    ListView lvCostType;

    @OnClick(R.id.tv_add_cost_type)
    public void onAddCostType() {
        View view = View.inflate(this, R.layout.view_cost_type, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setView(view);
        final AlertDialog alertDialog = builder.create();
        final EditText etTypeName = (EditText) view.findViewById(R.id.et_cost_type_name);
        TextView tvYes = (TextView) view.findViewById(R.id.tv_dialog_cost_type_yes);
        tvYes.setCompoundDrawablePadding(10);

        tvYes.setOnClickListener(new View.OnClickListener() {//删除
            @Override
            public void onClick(View v) {
                String typeName = etTypeName.getText().toString();
                if (TextUtils.isEmpty(typeName) || TextUtils.isEmpty(typeName.trim())) {
                    MToast.Show(CostTypeActivity.this, "请先输入消费类型！");
                    etTypeName.requestFocus();
                    return;
                }
                CostType costType = new CostType();
                costType.setIsDel(false);
                costType.setTypeName(typeName.trim());
                costType.save();

                costTypeListAdapter.loadListData();
                alertDialog.dismiss();
                hideKeyboard();
            }
        });
        alertDialog.show();
        etTypeName.requestFocus();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnClick(R.id.tv_cost_layout_back)
    public void onBack() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost_type);
        ButterKnife.bind(this);
        //MToast.Show(this, "" + lvCostType.getWidth() + "xxx");

        costTypeListAdapter = new CostTypeListAdapter();

        lvCostType.setAdapter(costTypeListAdapter);
        lvCostType.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        costTypeListAdapter.loadListData();
    }

    //ViewHolder静态类
    static class ViewHolder {
        public Long costTypeId;
        public TextView tvType;
        public LinearLayout layout;
    }

    public class CostTypeListAdapter extends BaseAdapter {

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
            return 0;
        }

        @Override
        public int getCount() {
            return costTypeList.size();
        }

        @Override
        public Object getItem(int position) {
            return costTypeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            CostType costType = costTypeList.get(position);
            int type = getItemViewType(position);

            if (null != convertView) {//已缓存
                //MLog.log("已缓存" + position);
                switch (type) {
                    case TYPE: {
                        viewHolder = (ViewHolder) convertView.getTag();
                        configCostTypeLv(viewHolder, costType);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {//未缓存，需初始化
                //MLog.log("未缓存" + position);
                viewHolder = new ViewHolder();
                switch (type) {
                    case TYPE: {
                        convertView = View.inflate(CostTypeActivity.this,
                                R.layout.lv_cost_type, null);
                        viewHolder.costTypeId = 0L;
                        viewHolder.tvType = (TextView) convertView.findViewById(R.id.tv_cost_type);
                        viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout_cost_type);
                        convertView.setTag(viewHolder);//将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag

                        configCostTypeLv(viewHolder, costType);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            return convertView;
        }

        private void loadListData() {
            String sql = "select * from cost_type where 1=1 and is_del=0 " +
                    "order by type_name, id desc";
            costTypeList = CostType.findWithQuery(CostType.class, sql);
            MLog.log(costTypeList);
            costTypeListAdapter.notifyDataSetChanged();
        }

        private void configCostTypeLv(final ViewHolder viewHolder, final CostType costType) {
            viewHolder.costTypeId = costType.getId();
            viewHolder.tvType.setText(costType.getTypeName());//名称

            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MToast.Show(CostTypeActivity.this, costType.getTypeName());
                    return;
                }
            });

            viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = View.inflate(CostTypeActivity.this,
                            R.layout.view_dialog_cost_type, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CostTypeActivity.this);
                    builder.setCancelable(true)
                            .setView(view);
                    final AlertDialog alertDialog = builder.create();
                    TextView tvDel = (TextView) view.findViewById(R.id.tv_dialog_del_cost_type);
                    tvDel.setCompoundDrawablePadding(10);

                    tvDel.setOnClickListener(new View.OnClickListener() {//删除
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder b = new AlertDialog.Builder(CostTypeActivity.this);
                            b.setCancelable(false)
                                    .setMessage("确定要删除吗？")
                                    .setTitle("提示")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            costType.setIsDel(true);
                                            costType.save();
                                            MToast.Show(CostTypeActivity.this,
                                                    getString(R.string.del_cost_type_ok));
                                            loadListData();
                                        }
                                    }).setNegativeButton("否", null);
                            AlertDialog a = b.create();
                            a.show();
                            loadListData();
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                    return false;
                }
            });
        }
    }
}
