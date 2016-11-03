package cn.xz.mytodo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xz.mytodo.R;
import cn.xz.mytodo.common.IConst;
import cn.xz.mytodo.util.MLog;
import cn.xz.mytodo.util.MToast;
import cn.xz.mytodo.util.MVersion;

/**
 * 更多
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.tv_frg_more_about)
    TextView tvAbout;
    @BindView(R.id.tv_frg_more_setting)
    TextView tvSetting;
    @BindView(R.id.tv_frg_more_help)
    TextView tvHelp;
    @BindView(R.id.tv_frg_more_exit)
    TextView tvExit;

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
        tvSetting.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvExit.setOnClickListener(this);

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
            case R.id.tv_frg_more_help: {
                showHelpDialog();
                break;
            }
            default: {
                break;
            }
        }
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
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String period = etPeriod.getText().toString();

                if (TextUtils.isEmpty(period) || 0 > Integer.parseInt(period)) {
                    etPeriod.setText(sp.getString(IConst.SP_KEY_CLOCK_PERIOD,
                            "" + IConst.SP_DEFAULT_VALUE_CLOCK_PERIOD));
                    MToast.Show(getActivity(), "番茄钟周期不正确！");
                    etPeriod.requestFocus();
                    return;
                }
                sp.edit()
                        .putInt(IConst.SP_KEY_CLOCK_PERIOD,
                                Integer.parseInt(etPeriod.getText().toString()))
                        .apply();
                restartApp();
            }
        });
        final AlertDialog configDialog = builder.create();
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configDialog.dismiss();
            }
        });
        configDialog.show();
    }

    private void restartApp() {
        getActivity().finish();
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        System.exit(0);
    }
}
