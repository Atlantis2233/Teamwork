package com.zrt.pvz.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.PVZApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.texture;

/**
 * @author 曾瑞庭
 * @Description: 更改用户的对话框
 * @date 2023/12/14 17:14
 */
public class ChangeUserDialog extends SubScene {
    private ListView<String> listView;
    private ObservableList<String> observableDataList;
    public ChangeUserDialog(List<String> users) {
        ImageView iv = new ImageView(FXGL.image("ui/changeUserDialog/dialog.png",408,492));
        iv.setFitWidth(408);
        iv.setFitHeight(492);
        //四个按钮
        ImageButton rename=new ImageButton("changeUserDialog/rename", 166, 50,
                this::Rename);
        ImageButton delete=new ImageButton("changeUserDialog/delete", 164, 50,
                this::Delete);
        ImageButton ok=new ImageButton("changeUserDialog/ok", 166, 50,
                this::ReturnMainMenu);
        ImageButton cancel=new ImageButton("changeUserDialog/cancel", 164, 50,
                this::ReturnMainMenu);
        iv.setTranslateX((FXGL.getAppWidth()-iv.getFitWidth())/2);
        iv.setTranslateY((FXGL.getAppHeight()-iv.getFitHeight())/2);
        //调整位置，可调
        rename.setLayoutX(250);
        rename.setLayoutY(350);
        delete.setLayoutX(400);
        delete.setLayoutY(350);
        ok.setLayoutX(250);
        ok.setLayoutY(400);
        cancel.setLayoutX(400);
        cancel.setLayoutY(400);

        //用户信息，这里只显示文字，设置按钮并不能弹窗改变，后续再写
        observableDataList = FXCollections.observableArrayList(users);
        VBox usersVBox = new VBox(10);
        usersVBox.setStyle("-fx-background-color: black;");

        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-text-fill: #FFFF99;");

        // 将数据加载到 ListView 中
        listView.setItems(observableDataList);

        // 添加按钮到列表中的每一项
        listView.setCellFactory(param -> new CustomListCell());

        listView.setOnMouseClicked(event -> {
            String selectedUser = listView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                if(selectedUser.equals("(建立一位新的用户)")){
                    CreateNewUser();
                }
                System.out.println("用户点击了：" + selectedUser);
                // 在这里执行你的逻辑操作
            }
        });
        // 添加 "(建立一位新的用户)" 到列表末尾
        observableDataList.add("(建立一位新的用户)");

        usersVBox.getChildren().add(listView);
        //位置，可调
        usersVBox.setPrefHeight(150);
        usersVBox.setLayoutY(150);
        usersVBox.setLayoutX(200);

        getContentRoot().getChildren().setAll(iv,rename,delete,ok,cancel,usersVBox);
    }

    //重命名对话框，记得修改（还没写）
    public void Rename(){
        System.out.println("Rename");
    }

    //删除用户，也没写对话框，但可以删除
    public void Delete(){
        if (!observableDataList.isEmpty()) {
            observableDataList.remove(0);
            PVZApp pvzApp=(PVZApp) FXGL.getApp();
            pvzApp.deleteUser(0);
        }
    }

    public void ReturnMainMenu(){
        FXGL.getSceneService().popSubScene();
    }

    //创建新用户
    public void CreateNewUser(){
        FXGL.getSceneService().pushSubScene(new CreateNewUser());
    }

    // 自定义 ListCell 以放置按钮
    private static class CustomListCell extends javafx.scene.control.ListCell<String> {
        private final StackPane cellPane;
        private final Button button;
        private final Rectangle selectionRect;
        public CustomListCell() {
            super();
            this.cellPane = new StackPane();
            this.button = new Button("Button");
            this.selectionRect = new Rectangle(200, 20);
            this.selectionRect.setFill(Color.TRANSPARENT);
            this.selectionRect.setStroke(Color.GREEN);
            this.selectionRect.setStrokeWidth(2);
            this.button.setOnAction(event -> {
                // 处理按钮点击事件
                System.out.println("Button clicked for: " + getItem());
            });
            // 将按钮放在文字后面
            cellPane.getChildren().addAll(selectionRect, button);
            setGraphic(cellPane);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);

                // 将矩形包裹文字
                selectionRect.setWidth(getBoundsInLocal().getWidth());
                selectionRect.setHeight(getBoundsInLocal().getHeight());
                cellPane.getChildren().setAll(selectionRect, button);

                // 根据条件设置矩形是否显示
                if (getIndex() == 0 && isSelected()) {
                    selectionRect.setFill(Color.GREEN);
                } else {
                    selectionRect.setFill(Color.TRANSPARENT);
                }
            }
        }
    }
}
