package com.zrt.pvz.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 21:48
 */
public class PowerBeatRecharge extends SubScene {
    public PowerBeatRecharge() {
        // 创建文字
        Text text = new Text("想要获得更多能量豆吗！充值变得更强！");
        text.setFont(Font.font("Arial", 24));
        text.setFill(Color.ORANGERED);

        // 创建箭头
        Polygon arrow1 = new Polygon(0, 0, 10, 0, 5, 15);
        Polygon arrow2 = new Polygon(0, 0, 10, 0, 5, 15);
        Polygon arrow3 = new Polygon(0, 0, 10, 0, 5, 15);
        arrow1.setFill(Color.RED);
        arrow2.setFill(Color.RED);
        arrow3.setFill(Color.RED);

        // 创建二维码图片
        Image qrCodeImage = FXGL.image("ui/Recharge.png");
        ImageView qrCodeView = new ImageView(qrCodeImage);
        qrCodeView.setFitWidth(300);
        qrCodeView.setFitHeight(300);

        // 创建关闭按钮
        Text closeBtn = new Text("X");
        closeBtn.setFont(Font.font("Arial", 35));
        closeBtn.setFill(Color.ORANGERED);
        closeBtn.setOnMouseClicked(event -> FXGL.getSceneService().popSubScene());

        // 创建布局
        VBox layout = new VBox(20);
        layout.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setAlignment(Pos.CENTER);

        // 创建边框
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK, // 边框颜色
                BorderStrokeStyle.SOLID, // 边框样式
                CornerRadii.EMPTY, // 圆角半径
                BorderWidths.DEFAULT // 边框宽度
        );

        Border border = new Border(borderStroke);
        layout.setBorder(border); // 将边框应用到 VBox
        layout.getChildren().addAll(text, arrow1, arrow2, arrow3, qrCodeView, closeBtn);
        layout.setLayoutX(140);
        layout.setLayoutY(20);

        getContentRoot().getChildren().add(layout);
    }
}
