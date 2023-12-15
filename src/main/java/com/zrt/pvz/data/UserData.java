package com.zrt.pvz.data;

import java.io.Serializable;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/13 11:41
 */
public class UserData implements Serializable {
    private String name;
    private int level;

    public UserData(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
