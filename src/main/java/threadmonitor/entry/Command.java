/*
 * Copyright (c) 2018年01月16日 by XuanWu Wireless Technology Co.Ltd. 
 *             All rights reserved                         
 */
package threadmonitor.entry;

/**
 * @Description
 * @Author <a href="mailto:haosonglin@wxchina.com">songlin.Hao</a>
 * @Date 2018/1/16
 * @Version 1.0.0
 */
public class Command {
    private Integer id;
    private String command;
    private String command_desc;

    public Command(Integer id, String command, String desc) {
        this.id = id;
        this.command = command;
        this.command_desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand_desc() {
        return command_desc;
    }

    public void setCommand_desc(String command_desc) {
        this.command_desc = command_desc;
    }
}
