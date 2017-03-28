package cn.xz.mytodo.entity;

import com.orm.SugarRecord;

/**
 * http://satyan.github.io/sugar/getting-started.html
 * todo实体类
 * 父类SugarRecord中已经定义了Long型id字段
 */
public class CostType extends SugarRecord {
    public CostType() {
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
    private String typeName;

    @Override
    public String toString() {
        return "CostType{" +
                "isDel=" + isDel +
                ", typeName='" + typeName + '\'' +
                '}';
    }

    public boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(boolean del) {
        isDel = del;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
