package com.hp.vo;

import java.io.Serializable;
import java.util.Date;

public class HtmlFile implements Serializable{
    private Integer htmlno;//html的id
    private String htmlname;//html的文件名
    private Date createdate;//创建日期
    private Date modifieddate;//修改日期
    private String storagepath;//html的存放路径

    //构造方法
    public HtmlFile(){
    }
    public HtmlFile(String htmlname, Date createdate, String storagepath){
        this.htmlname = htmlname;
        this.createdate = createdate;
        this.storagepath = storagepath;
    }
    public HtmlFile(Integer htmlno, String htmlname, Date createdate, String storagepath){
        this.htmlno = htmlno;
        this.htmlname = htmlname;
        this.createdate = createdate;
        this.storagepath = storagepath;
    }

    //setter方法和getter方法


    public Integer getHtmlno() {
        return htmlno;
    }

    public void setHtmlno(Integer htmlno) {
        this.htmlno = htmlno;
    }

    public String getHtmlname() {
        return htmlname;
    }

    public void setHtmlname(String htmlname) {
        this.htmlname = htmlname;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(Date modifieddate) {
        this.modifieddate = modifieddate;
    }

    public String getStoragepath() {
        return storagepath;
    }

    public void setStoragepath(String storagepath) {
        this.storagepath = storagepath;
    }
}
