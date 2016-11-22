package cn.xz.mytodo.common;

public interface IConst {
    String LOG_TAG = "xz";
    /**
     * 配置文件名字
     */
    String SP_FILE_NAME = "config";
    /**
     * 默认intent跳转执行间隔，毫秒
     */
    Integer DEFAULT_JUMP_PERIOD = 1500;
    /**
     * sp_key，番茄钟周期，单位：分钟
     */
    String SP_KEY_CLOCK_PERIOD = "clock_period";
    /**
     * sp_default_value，番茄钟周期，单位：分钟
     */
    Integer SP_DEFAULT_VALUE_CLOCK_PERIOD = 25;

    String SP_KEY_DEFAULT_VIEW = "default_view";
    int SP_VALUE_DEFAULT_VIEW_CLOCK = 1;
    int SP_VALUE_DEFAULT_VIEW_TODO = 2;
}
