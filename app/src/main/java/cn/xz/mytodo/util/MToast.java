package cn.xz.mytodo.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.xz.mytodo.R;

public class MToast {
    private static Toast toast;

    public static void Show(Context context, String tvString) {
        Show(context, tvString, null);
    }

    public static void Show(Context context, String tvString, ViewGroup root) {
        if (null == toast) {
            toast = new Toast(context);
        }
        View view = View.inflate(context, R.layout.m_toast_layout, root);
        TextView tv = (TextView) view.findViewById(R.id.text);

        tv.setText(tvString);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.show();
    }
}
