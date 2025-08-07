package ui.model;

import client.command.Command;
import cn.nap.utils.exception.NapCvtEx;


public class GmCommand implements Cloneable {
    private Integer id;
    private String command;
    private Integer gmLevel;
    private String isWork;
    private String commandClassName;
    private String description;
    private Integer gmLevelPrimitive;

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

    public Integer getGmLevel() {
        return gmLevel;
    }

    public void setGmLevel(Integer gmLevel) {
        this.gmLevel = gmLevel;
    }

    public boolean getIsWork() {
        return Boolean.parseBoolean(isWork);
    }

    public void setIsWork(String isWork) {
        this.isWork = isWork;
    }

    public Class<? extends Command> getCommandClass() {
        try {
            //Class<? extends Command> aClass = (Class<? extends Command>) Class.forName("client.command.commands.gm" + gmLevelPrimitive + "." + commandClassName);
            Class<?> aClass = Class.forName("client.command.commands.gm" + gmLevelPrimitive + "." + commandClassName);
            return (Class<? extends Command>) aClass;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void setCommandClassName(String commandClassName) {
        this.commandClassName = commandClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGmLevelPrimitive() {
        return gmLevelPrimitive;
    }

    public void setGmLevelPrimitive(Integer gmLevelPrimitive) {
        this.gmLevelPrimitive = gmLevelPrimitive;
    }

    @Override
    public GmCommand clone() {
        try {
            return (GmCommand) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new NapCvtEx(e.getMessage());
        }
    }
}
