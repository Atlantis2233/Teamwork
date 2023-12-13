package com.zrt.pvz.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.data.PlantData;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 * @author zrt
 *
 * 建造指示器:
 *      一个圆圈,中心有一个炮塔图标;
 *      如果炮塔图标移动到了可以建造的范围,那么绿色表示,如果是不能建造的范围, 红色表示
 *
 *  会频繁使用到, 所以只创建了一次(可以改为单例);
 *  当选择的炮塔变了, 调用updateIndicator修改建造指示器的数据
 *
 *
 */
public class BuildIndicatorComponent extends Component {

    private Texture texture;

    public BuildIndicatorComponent() {

    }

    @Override
    public void onAdded() {
        //图片
        texture = FXGL.texture("plant/SunFlower/SunFlower.png");
        //entity.getBoundingBoxComponent().addHitBox(
        //        new HitBox(BoundingShape.box(texture.getWidth(), texture.getHeight())));
        entity.getViewComponent().addChild(texture);
    }

    public void canBuild(boolean canBuild) {
        texture.setVisible(canBuild);
    }

    private String lastPlantName;
    public void updateIndicator(String plantName) {
        //为了性能,先判断下
        if (plantName.equals(lastPlantName)) {
            return;
        }
        lastPlantName = plantName;
        if(plantName.equals("shovel")){
            texture.setImage(FXGL.image("shovel/shovel.png"));
        }
        else{
            texture.setImage(FXGL.image("plant/"+plantName+"/"+plantName+".png"));
        }
        //entity.getBoundingBoxComponent().clearHitBoxes();
        //entity.getBoundingBoxComponent().addHitBox(
        //        new HitBox(BoundingShape.box(texture.getWidth(), texture.getHeight())));
    }

}