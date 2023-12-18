package com.zrt.pvz.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.zrt.pvz.PVZApp;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author 曾瑞庭
 * @Description: 重命名的场景
 * @date 2023/12/15 0:43
 */
public class ReNameDialog extends SubScene {
    public ReNameDialog(String oldName) {
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: lightgray; -fx-padding: 20px;");

        TextField inputField = new TextField();
        System.out.println(oldName);
        inputField.setPromptText(oldName);

        Button submitButton = new Button("提交");

        submitButton.setOnAction(event -> {
            String userInput = inputField.getText();
            PVZApp pvzApp=(PVZApp) FXGL.getApp();
            pvzApp.Rename(oldName,userInput);
            FXGL.getSceneService().popSubScene();
            ChangeUserDialog changeUserDialog= (ChangeUserDialog) FXGL.getSceneService().getCurrentScene();
            changeUserDialog.Rename(userInput);
            System.out.println("用户输入的内容是: " + userInput);
        });

        root.getChildren().addAll(inputField, submitButton);
        root.setLayoutX(300);
        root.setLayoutY(300);
        getContentRoot().getChildren().setAll(root);
    }
}
