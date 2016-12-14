package cn.xz.mytodo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xz.mytodo.entity.Todo;
import cn.xz.mytodo.fragment.TodoFragment;
import cn.xz.mytodo.util.MDate;
import cn.xz.mytodo.util.MToast;

public class TodoActivity extends Activity {

    @BindView(R.id.tv_ok)
    TextView tvOk;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_todo_title)
    EditText etTodoTitle;
    @BindView(R.id.et_todo_content)
    EditText etTodoContent;
    @BindView(R.id.et_todo_expire_date)
    EditText etTodoExpireDate;
    @BindView(R.id.layout_back)
    LinearLayout layoutBack;

    Intent intent;

    int operation;
    long todoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        ButterKnife.bind(this);

        etTodoExpireDate.setFocusable(false);
        etTodoContent.setNextFocusForwardId(R.id.et_todo_expire_date);

        intent = getIntent();
        operation = intent.getIntExtra(TodoFragment.INTENT_EXTRA_NAME_OPERATION, 0);
        todoId = intent.getLongExtra(TodoFragment.INTENT_EXTRA_NAME_TODO_ID, 0);
        if (TodoFragment.INTENT_EXTRA_VALUE_ADD == operation) {
            tvTitle.setText(getString(R.string.todo_add));
        } else {//查看或编辑
            Todo t = Todo.findById(Todo.class, todoId);//先获取此todo实体
            if (null == t) {
                MToast.Show(this, getString(R.string.error_access));
                finish();
            }
            if (TodoFragment.INTENT_EXTRA_VALUE_VIEW == operation) {
                tvTitle.setText(getString(R.string.todo_view));
                tvOk.setText(getString(R.string.title_ok));
                etTodoTitle.setText(t.getTitle());
                etTodoContent.setText(t.getDesc());
                etTodoExpireDate.setText(MDate.formatDate(t.getExpireDate()));

                etTodoTitle.setEnabled(false);
                etTodoContent.setEnabled(false);
                etTodoExpireDate.setEnabled(false);
            }
            if (TodoFragment.INTENT_EXTRA_VALUE_EDIT == operation) {
                tvOk.setText(getString(R.string.title_submit));
                tvTitle.setText(getString(R.string.todo_edit));
                etTodoTitle.setText(t.getTitle());
                etTodoContent.setText(t.getDesc());
                etTodoExpireDate.setText(MDate.formatDate(t.getExpireDate()));
            }
        }
    }

    @OnClick(R.id.tv_ok)
    void tvOk() {
        if (TextUtils.isEmpty(etTodoTitle.getText())) {
            MToast.Show(this, getString(R.string.err_title));
            etTodoTitle.requestFocus();
            return;
        }
        if (TodoFragment.INTENT_EXTRA_VALUE_ADD == operation) {
            Todo todo = new Todo();
            todo.setAddDate(new Date());
            todo.setTitle(etTodoTitle.getText().toString().trim());
            todo.setDesc(etTodoContent.getText().toString().trim());
            if (StringUtils.isNoneBlank(etTodoExpireDate.getText().toString())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date ret = sdf.parse(etTodoExpireDate.getText().toString());
                    todo.setExpireDate(ret);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            todo.save();
            setResult(TodoFragment.RESULT_ADD_OK, intent);
        } else if (TodoFragment.INTENT_EXTRA_VALUE_EDIT == operation) {
            Todo todo = Todo.findById(Todo.class, todoId);
            todo.setAddDate(new Date());
            todo.setTitle(etTodoTitle.getText().toString().trim());
            todo.setDesc(etTodoContent.getText().toString().trim());
            if (StringUtils.isNoneBlank(etTodoExpireDate.getText().toString())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date ret = sdf.parse(etTodoExpireDate.getText().toString());
                    todo.setExpireDate(ret);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            todo.save();
            setResult(TodoFragment.RESULT_EDIT_OK, intent);
        } else if (TodoFragment.INTENT_EXTRA_VALUE_VIEW == operation) {
            setResult(TodoFragment.RESULT_OPERATE_NONE, intent);
        }
        finish();
        //overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @OnClick(R.id.et_todo_expire_date)
    void showExpireDate() {
        String now = etTodoExpireDate.getText().toString();
        if (!StringUtils.isBlank(now)) {
            ContextCompat.getColor(this, R.color.white);
        }
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String yearStr = StringUtils.leftPad("" + year, 4, "0");
                String monthStr = StringUtils.leftPad("" + (monthOfYear + 1), 2, "0");
                String dayStr = StringUtils.leftPad("" + dayOfMonth, 2, "0");
                etTodoExpireDate.setText(yearStr + "-" + monthStr + "-" + dayStr);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.layout_back)
    void layoutBack() {
        setResult(TodoFragment.RESULT_OPERATE_NONE, intent);
        finish();
        //overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
