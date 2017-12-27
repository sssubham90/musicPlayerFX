/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import java.io.File;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Devil
 */
public class Track{
    private final Stage player;
    private Scene scene;
    private ImageView display;
    private Media media;
    private MediaPlayer mediaPlayer;
    private Text beg;
    private Slider s,pos;
    private Label title,album,artist,year;
    private boolean atEndOfMedia;
    private boolean Repeat=false;
    private final FileChooser fileChooser;

    public Track(Stage stage) {
        player=stage;
        fileChooser = new FileChooser();
    }
        
    public Scene createScene(){
        BorderPane parent=new BorderPane();
        parent.setTop(menuMaker());
        parent.setCenter(trackMaker());
        parent.setBottom(controlMaker());
        Region left=new Region();
        Region right=new Region();
        Text t=new Text("SWITCH");
        t.setFont(new Font(20));
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        scene =new Scene(parent);
        resizeListener();
        return scene;
    }

    public void resizeListener(){

        scene.widthProperty().addListener(cl->{
            display.setFitWidth(scene.getWidth());
        });
        scene.heightProperty().addListener(cl->{
            display.setFitHeight(scene.getHeight()-175);
        });
    }

    public boolean exit(){
        return ConfirmationBox.show("Are you sure you want to exit MusicPlayer?", "Exit!", "Yes", "No");
    }

    public void configureFileChooser(final FileChooser fileChooser) {      
        fileChooser.setTitle("View Music");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("MP3", "*.mp3")
        );
    }

    public HBox controlMaker(){
        Button play=new Button("Play");
        play.setOnAction(eh->{
            if(mediaPlayer!=null){
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.UNKNOWN||status == MediaPlayer.Status.HALTED){
                    return;
                }
                if (status == MediaPlayer.Status.PAUSED||status == MediaPlayer.Status.READY||status == MediaPlayer.Status.STOPPED){
                    if (atEndOfMedia) {
                        mediaPlayer.seek(Duration.ZERO);
                        atEndOfMedia = false;
                    }
                    mediaPlayer.setRate(1.0);
                    mediaPlayer.play();
                } 
                else{
                    mediaPlayer.pause();
                }
            }
        });
        Button pause=new Button("Pause");
        pause.setOnAction(eh->{
            if(mediaPlayer!=null)
                mediaPlayer.pause();
        });
        Button stop=new Button("Stop");
        stop.setOnAction(eh->{
            if(mediaPlayer!=null){
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.pause();
            }
        });
        Button fastfwd=new Button("FF");
        fastfwd.setOnAction(eh->mediaPlayer.setRate(mediaPlayer.getRate()!=8?mediaPlayer.getRate()*2:1.0));
        Button repeat=new Button("Repeat");
        repeat.setOnAction(eh->{
            Repeat=!Repeat;
            if(mediaPlayer!=null)
                mediaPlayer.setCycleCount(Repeat ? MediaPlayer.INDEFINITE : 1);        
        });
        Text t=new Text();
        t.setFont(new Font(8));
        s=new Slider();
        s.setPrefWidth(100);
        s.setShowTickMarks(true);
        s.setMajorTickUnit(10);
        s.setMinorTickCount(1);
        s.setShowTickLabels(false);
        s.valueProperty().addListener((observable, oldValue, newValue) -> {
            int i=newValue.intValue();
            t.setText(Integer.toString(i));
            if(mediaPlayer!=null)
                mediaPlayer.setVolume(i/100.0);
        });
        s.setValue(25);
        Button volUp=new Button("+");
        volUp.setOnAction(eh->{s.setValue(s.getValue()+1);});
        Button volDown=new Button("-");
        volDown.setOnAction(eh->{s.setValue(s.getValue()-1);});
        Region spacer=new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox controls=new HBox();
        VBox volume=new VBox(s,t);
        volume.setAlignment(Pos.CENTER);
        controls.setSpacing(5);
        controls.setPadding(new Insets(5,5,5,5));
        controls.getChildren().addAll(play,pause,stop,fastfwd,repeat,spacer,volDown,volume,volUp);
        controls.setStyle("-fx-background-color: #ff7000;");
        return controls;
    }

    public MenuBar menuMaker(){
        MenuItem open=new MenuItem("_Open");
        MenuItem close=new MenuItem("_Close");
        MenuItem exit=new MenuItem("_Exit");
        open.addEventHandler(EventType.ROOT, eh->{
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(player);
                if (file != null) {
                    openFile(file);
                }
        });
        close.addEventHandler(EventType.ROOT, eh->{
            mediaPlayer.stop();
            beg.setText("");
            display.setImage(new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg"));
            title.setText("Song");
            album.setText("");
            artist.setText("");
            year.setText("");
            mediaPlayer.dispose();
        });
        exit.addEventHandler(EventType.ROOT, eh->{if(exit()) player.close();});
        Menu file=new Menu("File");
        file.getItems().addAll(open,close,exit);
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(file);
        menuBar.setPadding(new Insets(2));
        return menuBar;
    }

    public VBox trackMaker(){
        display=new ImageView(new Image(new File("C:\\Users\\KIIT\\Documents\\NetBeansProjects\\MusicPlayer\\img\\1.jpg").toURI().toString()));
        display.setFitHeight(250);
        display.setFitWidth(500);
        display.setPreserveRatio(false);
        pos=new Slider();
        pos.setMax(600000);
        pos.valueProperty().addListener((Observable ov) -> {
            if (pos.isValueChanging()) {
               mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(pos.getValue() / 600000.0));
           }
        });
        HBox.setHgrow(pos, Priority.ALWAYS);
        beg=new Text();
        HBox scroller=new HBox(beg,pos);
        scroller.setPadding(new Insets(0, 5, 0, 5));
        title=new Label("Song");
        album=new Label();
        artist=new Label();
        year=new Label();
        title.setPadding(new Insets(0,0,0,5));
        album.setPadding(new Insets(0,0,0,5));
        artist.setPadding(new Insets(0,0,0,5));
        year.setPadding(new Insets(0,0,0,5));
        Font f=new Font(12);
        title.setFont(new Font(16));
        album.setFont(f);
        artist.setFont(f);
        year.setFont(f);
        VBox track=new VBox();
        track.setStyle("-fx-background-color: #ffff00;");
        track.getChildren().addAll(display,scroller,title,album,artist,year);
        return track;
    }

    public void openFile(File file) {
        media=new Media(file.toURI().toString());
        ObservableMap<String,Object> meta_data=media.getMetadata();

        meta_data.addListener((MapChangeListener.Change<? extends String, ? extends Object> ch) -> {
            if(ch.wasAdded()){
                String key=ch.getKey();
                Object value=ch.getValueAdded();
                switch(key){
                    case "album":
                        album.setText("Album: "+value.toString());
                        break;
                    case "artist":
                        artist.setText("Artist: "+value.toString());
                        break;
                    case "title":
                        title.setText(value.toString());
                        break;
                    case "year":
                        year.setText("Year: "+value.toString());
                        break;
                    case "image":
                        display.setImage((Image)value);
                        break;
                }
            }
        });
        setMediaPlayer();
        mediaPlayer.play();
    }

    public void setMediaPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            beg.setText("");
            display.setImage(new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg"));
            title.setText("Song");
            mediaPlayer.dispose();
        }
        mediaPlayer=new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        mediaPlayer.setVolume(s.getValue()/100);
        mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });
        mediaPlayer.setOnReady(() -> {
            updateValues();
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if(!Repeat){
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.pause();
                atEndOfMedia=true;
            }
        });
    }

    public void updateValues() {
        if (pos != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                Duration duration=mediaPlayer.getMedia().getDuration();
                beg.setText(formatTime(currentTime,duration));
                pos.setDisable(duration.isUnknown());
                if (!pos.isDisabled()&&duration.greaterThan(Duration.ZERO)&&!pos.isValueChanging()) {
                    pos.setValue(currentTime.divide(duration).toMillis() * 600000.0);
                }
            });
        }
    }

    private String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) 
            intElapsed -= elapsedHours * 60 * 60;
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) 
                intDuration -= durationHours * 60 * 60;
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) 
                return String.format("%d:%02d:%02d/%d:%02d:%02d",elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds); 
            else 
                return String.format("%02d:%02d/%02d:%02d",elapsedMinutes, elapsedSeconds,durationMinutes,durationSeconds);
        } 
        else {
            if (elapsedHours > 0) 
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            else 
                return String.format("%02d:%02d",elapsedMinutes,elapsedSeconds);
            }
        }
}