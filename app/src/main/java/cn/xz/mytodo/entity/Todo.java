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

    @Override
    public String toString() {
        return "Todo{" +
                "ifDone=" + ifDone +
                ", ifStar=" + ifStar +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", addDate=" + addDate +
                ", isButton=" + isButton +
                ", isDel=" + isDel +
                '}';
    }

    public Todo() {
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
     * 是否星标
     */
    private boolean ifStar;
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
    private Date addDate;

    /**
     * 过期时间
     */
    private Date expireDate;

    /**
     * 当前是否为按钮
     */
    private boolean isButton;
    /**
     * 是否已删除
     */
    private boolean isDel;
    //======

    public boolean getIfDone() {
        return ifDone;
    }

    public void setIfDone(boolean ifDone) {
        this.ifDone = ifDone;
    }

    public boolean getIfStar() {
        return ifStar;
    }

    public void setIfStar(boolean ifStar) {
        this.ifStar = ifStar;
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

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public boolean getIsButton() {
        return isButton;
    }

    public void setIsButton(boolean isButton) {
        this.isButton = isButton;
    }

    public boolean getIsDel() {
        return isDel;
    }

    public void setIstDel(boolean isDel) {
        this.isDel = isDel;
    }


}
