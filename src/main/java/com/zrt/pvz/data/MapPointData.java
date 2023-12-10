package com.zrt.pvz.data;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/6 16:15
 */
public class MapPointData {
    private int startPointX;
    private int startPointY;
    private int width;
    private int height;
    private int intervalX;
    private int intervalY;
    private int numsX;
    private int numsY;
    private int row;
    private int column;

    public int getStartPointX() {
        return startPointX;
    }

    public int getStartPointY() {
        return startPointY;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getIntervalX() {
        return intervalX;
    }

    public int getIntervalY() {
        return intervalY;
    }

    public int getNumsX() {
        return numsX;
    }

    public int getNumsY() {
        return numsY;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
