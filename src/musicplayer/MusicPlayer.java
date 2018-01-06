/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
/**
 *
 * @author Devil
 */
public class MusicPlayer extends Application{    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Track track=new Track(stage);
        stage.setTitle("Angel Music Player");
        stage.setScene(track.createScene(500,440));
        stage.setMinWidth(300);
        stage.getIcons().add(new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg"));
        stage.show();
    }
    
}