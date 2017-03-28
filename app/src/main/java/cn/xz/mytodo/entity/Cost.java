package cn.xz.mytodo.entity;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * http://satyan.github.io/sugar/getting-started.html
 * todo实体类
 * 父类SugarRecord中已经定义了Long型id字段
 */
public class Cost extends SugarRecord {
    public Cost(){
        //
    }

    /**
     * 实体id,不用自己定义,父类SugarRecord中已经定义了Long型id字段
     */
    //private Integer id;
    /**
     * 是否已删除
     */
    private boolean isDel;
    private Integer count;
    private String costTitle;
    private String costType;
    private Date costDate;

    @Override
    public String toString() {
        return "Cost{" +
                "isDel=" + isDel +
                ", count=" + count +
                ", costTitle='" + costTitle + '\'' +
                ", costType='" + costType + '\'' +
                ", costDate=" + costDate +
                '}';
    }

    public boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(boolean del) {
        isDel = del;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCostTitle() {
        return costTitle;
    }

    public void setCostTitle(String costTitle) {
        this.costTitle = costTitle;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public Date getCostDate() {
        return costDate;
    }

    public void setCostDate(Date costDate) {
        this.costDate = costDate;
    }
}
