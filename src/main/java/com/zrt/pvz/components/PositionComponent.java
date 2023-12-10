package com.zrt.pvz.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/6 16:04
 */
public class PositionComponent extends Component {
    private int row;// 记录行号，可以更改
    private int column; //记录初始列号，后期可能有点用

    public PositionComponent(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public void onAdded() {

    }
}
