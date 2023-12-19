package com.zrt.pvz.components;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.zrt.pvz.PVZApp;
import com.zrt.pvz.data.PlantData;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 18:15
 */
public class ChooseButtonComponent extends Component {
    public final ToggleButton btn;
    private double height=60,weight=47;
    private PlantData plantData;
    private Rectangle rectangle;
    private String name;
    public ChooseButtonComponent() {
        btn = new ToggleButton();
        btn.setToggleGroup(FXGL.<PVZApp>getAppCast().getPlantBtnGroup());
        btn.setPrefSize(weight, height);
    }
    @Override
    public void onAdded(){
        this.plantData = entity.getObject("plantData");
        this.name = plantData.name();
        btn.setStyle("-fx-background-color: #0000;");
        entity.getViewComponent().addChild(btn);
        btn.selectedProperty().addListener((ob, ov, nv) ->{
            if(!nv){
                nv=true;
            }
            FXGL.set("choosePlantName", nv ? plantData.name() : "");
        });
    }
    public void darken(){
        Color transparentBlack = Color.web("#00000090");
        rectangle=new Rectangle(weight,height,transparentBlack);
        rectangle.setVisible(true);
        entity.getViewComponent().addChild(rectangle);
    }
    public void lighten(){
        if (rectangle != null) {
            entity.getViewComponent().removeChild(rectangle);
            rectangle = null;
        }
    }
    public String getName(){
        return this.name;
    }
}
