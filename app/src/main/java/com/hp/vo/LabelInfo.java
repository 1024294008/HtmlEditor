package com.hp.vo;

import java.io.Serializable;

public class LabelInfo implements Serializable{
    private Integer labelno;//标签id
    private String labelname;//标签名
    private String labelType;//标签类型
    private String labelAttributes;//属性列表
    private String labelVersion;//版本标识
    private String subLabels;//子标签列表

    public Integer getLabelno() {
        return labelno;
    }

    public void setLabelno(Integer labelno) {
        this.labelno = labelno;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public String getLabelAttributes() {
        return labelAttributes;
    }

    public void setLabelAttributes(String labelAttributes) {
        this.labelAttributes = labelAttributes;
    }

    public String getLabelVersion() {
        return labelVersion;
    }

    public void setLabelVersion(String labelVersion) {
        this.labelVersion = labelVersion;
    }

    public String getSubLabels() {
        return subLabels;
    }

    public void setSubLabels(String subLabels) {
        this.subLabels = subLabels;
    }
}
