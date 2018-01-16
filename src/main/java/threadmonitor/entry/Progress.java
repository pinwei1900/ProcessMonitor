/*
 * Copyright (c) 2018年01月10日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.entry;

import java.util.Date;
import javafx.beans.property.SimpleStringProperty;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/10
 * @Version 1.0.0
 */
public class Progress {

    private SimpleStringProperty userColumn = new SimpleStringProperty();
    private SimpleStringProperty pidColumn = new SimpleStringProperty();
    private SimpleStringProperty cpuColumn = new SimpleStringProperty();
    private SimpleStringProperty memColumn = new SimpleStringProperty();
    private SimpleStringProperty vszColumn = new SimpleStringProperty();
    private SimpleStringProperty rssColumn = new SimpleStringProperty();
    private SimpleStringProperty ttyColumn = new SimpleStringProperty();
    private SimpleStringProperty statColumn = new SimpleStringProperty();
    private SimpleStringProperty startColumn = new SimpleStringProperty();
    private SimpleStringProperty timeColumn = new SimpleStringProperty();
    private SimpleStringProperty commandColumn = new SimpleStringProperty();
    private Date date;


    public Progress(){

    }

    public Progress(String hans, String muster) {
        userColumn = new SimpleStringProperty(hans);
        pidColumn = new SimpleStringProperty(muster);
    }

    public String getUserColumn() {
        return userColumn.get();
    }

    public SimpleStringProperty userColumnProperty() {
        return userColumn;
    }

    public void setUserColumn(String userColumn) {
        this.userColumn.set(userColumn);
    }

    public String getPidColumn() {
        return pidColumn.get();
    }

    public SimpleStringProperty pidColumnProperty() {
        return pidColumn;
    }

    public void setPidColumn(String pidColumn) {
        this.pidColumn.set(pidColumn);
    }

    public String getCpuColumn() {
        return cpuColumn.get();
    }

    public SimpleStringProperty cpuColumnProperty() {
        return cpuColumn;
    }

    public void setCpuColumn(String cpuColumn) {
        this.cpuColumn.set(cpuColumn);
    }

    public String getMemColumn() {
        return memColumn.get();
    }

    public SimpleStringProperty memColumnProperty() {
        return memColumn;
    }

    public void setMemColumn(String memColumn) {
        this.memColumn.set(memColumn);
    }

    public String getVszColumn() {
        return vszColumn.get();
    }

    public SimpleStringProperty vszColumnProperty() {
        return vszColumn;
    }

    public void setVszColumn(String vszColumn) {
        this.vszColumn.set(vszColumn);
    }

    public String getRssColumn() {
        return rssColumn.get();
    }

    public SimpleStringProperty rssColumnProperty() {
        return rssColumn;
    }

    public void setRssColumn(String rssColumn) {
        this.rssColumn.set(rssColumn);
    }

    public String getStatColumn() {
        return statColumn.get();
    }

    public SimpleStringProperty statColumnProperty() {
        return statColumn;
    }

    public void setStatColumn(String statColumn) {
        this.statColumn.set(statColumn);
    }

    public String getStartColumn() {
        return startColumn.get();
    }

    public SimpleStringProperty startColumnProperty() {
        return startColumn;
    }

    public void setStartColumn(String startColumn) {
        this.startColumn.set(startColumn);
    }

    public String getTimeColumn() {
        return timeColumn.get();
    }

    public SimpleStringProperty timeColumnProperty() {
        return timeColumn;
    }

    public void setTimeColumn(String timeColumn) {
        this.timeColumn.set(timeColumn);
    }

    public String getCommandColumn() {
        return commandColumn.get();
    }

    public SimpleStringProperty commandColumnProperty() {
        return commandColumn;
    }

    public void setCommandColumn(String commandColumn) {
        this.commandColumn.set(commandColumn);
    }

    public String getTtyColumn() {
        return ttyColumn.get();
    }

    public SimpleStringProperty ttyColumnProperty() {
        return ttyColumn;
    }

    public void setTtyColumn(String ttyColumn) {
        this.ttyColumn.set(ttyColumn);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
