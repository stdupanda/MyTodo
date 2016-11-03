package cn.xz.mytodo.entity;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by gsx on 2016/10/26.
 */
/**
 * http://satyan.github.io/sugar/getting-started.html
 * todo实体类
 * 父类SugarRecord中已经定义了Long型id字段
 */
public class Todo extends SugarRecord {

    public Todo(){
        //
    }
    /**
     * 实体id,不用自己定义,父类SugarRecord中已经定义了Long型id字段
     */
    //private Integer id;
    /**
     * 是否已完成
     */
    private boolean ifDone;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String desc;
    /**
     * 对应添加时间
     */
    private Date date;

    //======
    public boolean getIfDone() {
        return ifDone;
    }

    public void setIfDone(boolean ifDone) {
        this.ifDone = ifDone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
