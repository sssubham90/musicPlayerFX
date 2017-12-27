/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Devil
 */
public class ConfirmationBox {
    static boolean btnYClicked;
    public static boolean show(String Message,String Title, String textYes,String textNo){
        Image image=new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg");
        Stage stage= new Stage();
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(250);
        Region spacer=new Region();
        Label lb=new Label(Message);
        Button btnY = new Button(textYes);
        btnY.setOnAction(eh->{btnYClicked=true;stage.close();});
        Button btnN = new Button(textNo);
        btnN.setOnAction(eh->{btnYClicked=false;stage.close();});
        HBox hb=new HBox();
        HBox.setHgrow(spacer,Priority.ALWAYS);
        hb.getChildren().addAll(btnY,spacer,btnN);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(10));
        VBox vb=new VBox();
        vb.getChildren().addAll(lb,hb);
        vb.setAlignment(Pos.CENTER);
        Scene scene=new Scene(vb);
        stage.setScene(scene);
        stage.setTitle(Title);
        stage.showAndWait();
        return btnYClicked;
    }
    
}
