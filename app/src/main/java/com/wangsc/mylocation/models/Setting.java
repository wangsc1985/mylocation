package com.wangsc.mylocation.models;

/**
 * Created by 阿弥陀佛 on 2015/6/30.
 */
public class Setting {

    private String name;
    private String value;
    private int level;

    public Setting(String name, String value, int level){
        this.name = name;
        this.value = value;
        this.level=level;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getString() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }


    public Boolean getBoolean(){
        return Boolean.parseBoolean(value);
    }
    public int getInt(){
        return Integer.parseInt(value);
    }
    public long getLong(){
        return Long.parseLong(value);
    }
    public DateTime getDateTime(){
        return new DateTime(getLong());
    }
    public float getFloat(){
        return Float.parseFloat(value);
    }
    public double getDouble(){
        return Double.parseDouble(value);
    }

    public enum KEYS{
        phone
    }
    //endregion
}
