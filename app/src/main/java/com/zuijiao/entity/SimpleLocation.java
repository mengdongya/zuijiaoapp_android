package com.zuijiao.entity;

/**
 * Created by xiaqibo on 2015/4/27.
 */
public class SimpleLocation {


    private int id = 0;
    private String name = "";
    private int p_id = 0;
    private int type = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
