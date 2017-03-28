package cn.xz.mytodo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.CostTypeActivity;
import cn.xz.mytodo.R;
import cn.xz.mytodo.common.IConst;
import cn.xz.mytodo.util.MDate;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;
import cn.xz.mytodo.util.MVersion;

/**
 * 更多
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.tv_frg_more_about)
    TextView tvAbout;
    @BindView(R.id.tv_frg_more_exp)
    TextView tvExp;
    @BindView(R.id.tv_frg_more_setting)
    TextView tvSetting;
    @BindView(R.id.tv_frg_more_help)
    TextView tvHelp;
    @BindView(R.id.tv_frg_more_exit)
    TextView tvExit;
    @BindView(R.id.tv_frg_more_cost_type)
    TextView tvCostType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_more, container, false);
        MLog.log("加载了：MoreFragment");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvAbout.setOnClickListener(this);
        tvExp.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        tvCostType.setOnClickListener(this);

        MLog.log("更多！");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_frg_more_about: {
                showAboutDialog();
                break;
            }
            case R.id.tv_frg_more_exit: {
                getActivity().finish();
                System.exit(0);
                Process.killProcess(Process.myPid());
                break;
            }
            case R.id.tv_frg_more_setting: {
                showSettingDialog();
                break;
            }
            case R.id.tv_frg_more_exp: {
                showExpDialog();
                break;
            }
            case R.id.tv_frg_more_help: {
                showHelpDialog();
                break;
            }
            case R.id.tv_frg_more_cost_type: {
                goCostTypeActivity();
                break;
            }
            default: {
                break;
            }
        }
    }


    private void showExpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("导出数据？")
                .setMessage("确定导出数据吗？待办数据将会导出到外置存储根路径下。")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dbFilePath = getActivity().getDatabasePath("todo.db").getAbsolutePath();
                        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String suffix = "/todo_db_"
                                + MDate.getDate().replaceAll("-", "")
                                .replaceAll(":", "").replace(" ", "") + ".db";
                        if (!new File(dbFilePath).exists()) {
                            MToast.Show(getActivity(), "db文件路径错误！");
                            return;
                        }
                        MLog.log(sdcardPath + suffix);
                        boolean ret = copyFile(dbFilePath, sdcardPath + suffix);
                        if (ret) {
                            MToast.Show(getActivity(), "导出数据成功！");
                        } else {
                            MToast.Show(getActivity(), "导出数据失败！");
                        }
                    }
                })
                .setCancelable(true)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("关于")
                .setMessage(getActivity().
                        getString(R.string.app_ver,
                                "\n版本:" + MVersion.getAppVersionName(getActivity())))
                .setPositiveButton("确定", null)
                .setCancelable(false)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle("帮助")
                .setMessage(getActivity().getString(R.string.help_content))
                .setPositiveButton("确定", null)
                .setCancelable(false)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    private void showSettingDialog() {
        final SharedPreferences sp = getActivity()
                .getSharedPreferences(IConst.SP_FILE_NAME, Context.MODE_PRIVATE);

        final View view = View.inflate(getActivity(), R.layout.view_config, null);
        final RadioButton rbClock = (RadioButton) view.findViewById(R.id.rb_clock);
        final RadioButton rbTodo = (RadioButton) view.findViewById(R.id.rb_todo);
        final RadioButton rbMoney = (RadioButton) view.findViewById(R.id.rb_money);
        int defaultView = sp.getInt(IConst.SP_KEY_DEFAULT_VIEW,
                IConst.SP_VALUE_DEFAULT_VIEW_CLOCK);
        switch (defaultView) {
            case IConst.SP_VALUE_DEFAULT_VIEW_CLOCK: {
                rbClock.setChecked(true);
                break;
            }
            case IConst.SP_VALUE_DEFAULT_VIEW_MONEY: {
                rbMoney.performClick();
                break;
            }
            case IConst.SP_VALUE_DEFAULT_VIEW_TODO: {
                rbTodo.performClick();
                break;
            }
            default: {
                rbClock.setChecked(true);
                break;
            }
        }

        final EditText etPeriod = (EditText) view.findViewById(R.id.et_config_clock_period);
        etPeriod.setText(getString(R.string.place_holder,
                "" + sp.getInt(IConst.SP_KEY_CLOCK_PERIOD,
                        IConst.SP_DEFAULT_VALUE_CLOCK_PERIOD)));

        TextView btnYes = (TextView) view.findViewById(R.id.btn_yes);
        TextView btnNo = (TextView) view.findViewById(R.id.btn_no);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view, 10, 0, 10, 0)
                .setTitle("系统设置")
                .setCancelable(false);
        final AlertDialog configDialog = builder.create();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period = etPeriod.getText().toString();

                if (TextUtils.isEmpty(period) || 0 > Integer.parseInt(period)) {
                    etPeriod.setText(getString(R.string.place_holder, "" + sp.getInt(IConst.SP_KEY_CLOCK_PERIOD,
                            IConst.SP_DEFAULT_VALUE_CLOCK_PERIOD)));
                    MToast.Show(getActivity(), "番茄钟周期不正确！");
                    etPeriod.requestFocus();
                    return;
                }

                int selected = -1;
                if (rbClock.isChecked()) {
                    selected = IConst.SP_VALUE_DEFAULT_VIEW_CLOCK;
                } else if (rbTodo.isChecked()) {
                    selected = IConst.SP_VALUE_DEFAULT_VIEW_TODO;
                } else if (rbMoney.isChecked()) {
                    selected = IConst.SP_VALUE_DEFAULT_VIEW_MONEY;
                }

                sp.edit()
                        .putInt(IConst.SP_KEY_CLOCK_PERIOD,
                                Integer.parseInt(etPeriod.getText().toString()))
                        .putInt(IConst.SP_KEY_DEFAULT_VIEW, selected)
                        .apply();
                configDialog.dismiss();
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                MToast.Show(getActivity(), "设置成功！");
                //restartApp();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                configDialog.dismiss();
            }
        });
        configDialog.show();
    }

    private void goCostTypeActivity(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), CostTypeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("a", 1);
        intent.putExtras(bundle);
        startActivityForResult(intent, -1);
    }

    private void restartApp() {
        getActivity().finish();
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        System.exit(0);
    }

    public boolean copyFile(String source, String dest) {
        try {
            File fileSrc = new File(source);
            File fileDest = new File(dest);
            InputStream in = new FileInputStream(fileSrc);

            OutputStream out = new FileOutputStream(fileDest);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }
}
