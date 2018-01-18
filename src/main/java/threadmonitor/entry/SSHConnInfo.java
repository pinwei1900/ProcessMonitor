/*
 * Copyright (c) 2018年01月18日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.entry;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/18
 * @Version 1.0.0
 */
public class SSHConnInfo {

    private Integer id;
    private String connectIp;
    private String connectUser;
    private String connectPwd;
    private String connectDesc;

    public SSHConnInfo(String connectIp, String connectUser, String connectPwd,
            String connectDesc) {
        this.connectIp = connectIp;
        this.connectUser = connectUser;
        this.connectPwd = connectPwd;
        this.connectDesc = connectDesc;
    }

    public SSHConnInfo(Integer id ,String connectIp, String connectUser, String connectPwd,
            String connectDesc) {
        this.id = id;
        this.connectIp = connectIp;
        this.connectUser = connectUser;
        this.connectPwd = connectPwd;
        this.connectDesc = connectDesc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConnectUser() {
        return connectUser;
    }

    public void setConnectUser(String connectUser) {
        this.connectUser = connectUser;
    }

    public String getConnectPwd() {
        return connectPwd;
    }

    public void setConnectPwd(String connectPwd) {
        this.connectPwd = connectPwd;
    }

    public String getConnectDesc() {
        return connectDesc;
    }

    public void setConnectDesc(String connectDesc) {
        this.connectDesc = connectDesc;
    }

    public String getConnectIp() {
        return connectIp;
    }

    public void setConnectIp(String connectIp) {
        this.connectIp = connectIp;
    }
}
