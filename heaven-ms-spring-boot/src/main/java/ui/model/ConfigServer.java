package ui.model;

import cn.nap.utils.exception.NapCvtEx;

public class ConfigServer implements Cloneable {
    private Integer id;
    private String configWorld;
    private String configType;
    private String configName;
    private String configData;
    private String configDataType;
    private String configDesc;
    private String popular;
    private String simpleName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigWorld() {
        return configWorld;
    }

    public void setConfigWorld(String configWorld) {
        this.configWorld = configWorld;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public String getConfigDataType() {
        return configDataType;
    }

    public void setConfigDataType(String configDataType) {
        this.configDataType = configDataType;
    }

    public String getConfigDesc() {
        return configDesc;
    }

    public void setConfigDesc(String configDesc) {
        this.configDesc = configDesc;
    }

    public String getPopular() {
        return popular;
    }

    public void setPopular(String popular) {
        this.popular = popular;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    @Override
    public ConfigServer clone() {
        try {
            return (ConfigServer) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new NapCvtEx(e.getMessage());
        }
    }
}
