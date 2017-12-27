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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Devil
 */
public class MessageBox {
    public static void Show(String Message,String Title){
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Title);
        stage.setMinWidth(250);
        
        Label lb=new Label(Message);
        
        Button btn=new Button("OK");
        btn.setOnAction(eh->stage.close());
        
        VBox vb=new VBox();
        vb.getChildren().addAll(lb,btn);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10));
        
        Scene scene=new Scene(vb);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
