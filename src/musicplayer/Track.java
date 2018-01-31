/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import java.io.File;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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
    private Playlist playList;
    private ProgressBar progress;
    private Button pause;
    private Button play;
    private Button stop;
    private Button fastfwd;
    private Button repeat;

    public Track(Stage stage) {
        player=stage;
        fileChooser = new FileChooser();
    }
        
    public Scene createScene(double width,double height){
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
        scene =new Scene(parent,width,height);
        progress.setMinWidth(scene.getWidth()-80);
        progress.setMaxWidth(scene.getWidth()-80);
        resizeListener();
        return scene;
    }

    public void resizeListener(){
        scene.widthProperty().addListener(cl->{
            progress.setMinWidth(scene.getWidth()-80);
            progress.setMaxWidth(scene.getWidth()-80);
            display.setFitWidth(scene.getWidth());
        });
        scene.heightProperty().addListener(cl->{
            display.setFitHeight(scene.getHeight()-190);
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
        play=new Button("Play"); play.setStyle("-fx-background-color:#ffffff");
        pause=new Button("Pause"); pause.setStyle("-fx-background-color:#ffffff");
        stop=new Button("Stop"); stop.setStyle("-fx-background-color:#ffffff");
        fastfwd=new Button("FF"); fastfwd.setStyle("-fx-background-color:#ffffff");
        repeat=new Button("Repeat"); repeat.setStyle("-fx-background-color:#ffffff");
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
            }
        });
        pause.setOnAction(eh->{
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.UNKNOWN||status == MediaPlayer.Status.HALTED||status == MediaPlayer.Status.PAUSED){
                    return;
            }
            if(mediaPlayer!=null){
                mediaPlayer.pause();
            }
        });
        stop.setOnAction(eh->{
            if(mediaPlayer!=null){
                mediaPlayer.seek(Duration.ZERO);
                pos.setValue(0);
                beg.setText("00:00/00:00");
                mediaPlayer.pause();
            }
        });
        fastfwd.setOnAction(eh->{
            if(mediaPlayer.getRate()!=8)fastfwd.setStyle("-fx-background-color:#ffff00");
            else fastfwd.setStyle("-fx-background-color:#ffffff");
            mediaPlayer.setRate(mediaPlayer.getRate()!=8?mediaPlayer.getRate()*2:1.0);
        });
        repeat.setOnAction(eh->{
            Repeat=!Repeat;
            if(Repeat) repeat.setStyle("-fx-background-color:#ffff00");
            else repeat.setStyle("-fx-background-color:#ffffff");
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
            mediaPlayer.dispose();
            display.setImage(new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg"));
            title.setText("Song");
            album.setText("");
            artist.setText("");
            year.setText("");
            beg.setText("00:00/00:00");
            pos.setValue(0);
            progress.setProgress(0);
            play.setStyle("-fx-background-color:#ffffff");
            pause.setStyle("-fx-background-color:#ffffff");
            stop.setStyle("-fx-background-color:#ffffff");
            fastfwd.setStyle("-fx-background-color:#ffffff");
            repeat.setStyle("-fx-background-color:#ffffff");
            System.gc();
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
        Button playlist = new Button("Playlist");
        playlist.setStyle("-fx-background-color:#ff7000;-fx-text-color:#ffffff;-fx-text-fill:#ffffff");
        playlist.setOnAction(eh->{
            playList = new Playlist(player);
            player.setScene(playList.createScene(scene.getWidth(),scene.getHeight()));
        });
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox switcher = new HBox(spacer,playlist);
        switcher.setPadding(new Insets(2,4,2,4));
        display=new ImageView(new Image(new File("C:\\Users\\KIIT\\Documents\\NetBeansProjects\\MusicPlayer\\img\\1.jpg").toURI().toString()));
        display.setFitHeight(250);
        display.setFitWidth(500);
        display.setPreserveRatio(false);
        progress=new ProgressBar(0);
        pos=new Slider();
        pos.setMax(600000);
        pos.valueProperty().addListener((Observable ov) -> {
            if (pos.isValueChanging()) {
                try{
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(pos.getValue() / 600000.0));
                }
                catch(NullPointerException e){}
                progress.setProgress(pos.getValue()/pos.getMax());
            }
        });
        progress.setMaxHeight(5);
        progress.setMinHeight(5);
        StackPane progressSlider = new StackPane(progress,pos);
        HBox.setHgrow(pos, Priority.ALWAYS);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);
        beg=new Text("00:00/00:00");
        HBox scroller=new HBox(beg,progressSlider);
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
        track.getStylesheets().add("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/style.css");
        track.getChildren().addAll(switcher,display,scroller,title,album,artist,year);
        return track;
    }

    public void openFile(File file){
        System.gc();
        media=new Media(file.toURI().toString());
        setMediaPlayer(file);
        mediaPlayer.play();
    }

    public void setMediaPlayer(File file){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            beg.setText("");
            display.setImage(new Image("file:///C:/Users/KIIT/Documents/NetBeansProjects/MusicPlayer/img/1.jpg"));
            title.setText("Song");
            mediaPlayer.dispose();
        }
        mediaPlayer=new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        Repeat=Boolean.FALSE;
        fastfwd.setStyle("-fx-background-color:#ffffff");
        repeat.setStyle("-fx-background-color:#ffffff");
        mediaPlayer.setVolume(s.getValue()/100);
        mediaPlayer.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if(!Repeat){
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.pause();
                pause.setStyle("");
                atEndOfMedia=true;
            }
        });
        mediaPlayer.setOnReady(()->{
            updateValues();
            title.setText(file.getName().substring(0,file.getName().length()-4));
            try{
                album.setText(media.getMetadata().get("album").toString());
                artist.setText(media.getMetadata().get("artist").toString());
                title.setText(media.getMetadata().get("title").toString());
                year.setText(media.getMetadata().get("year").toString());
            }
            catch(NullPointerException e){}
            if(media.getMetadata().get("image")!=null)
                display.setImage((Image)media.getMetadata().get("image"));
        });
        mediaPlayer.setOnPlaying(() -> {
            play.setStyle("-fx-background-color:#ffff00");
            pause.setStyle("-fx-background-color:#ffffff");
            stop.setStyle("-fx-background-color:#ffffff");
        });
        mediaPlayer.setOnPaused(() -> {
            updateValues();
            fastfwd.setStyle("-fx-background-color:#ffffff");
            pause.setStyle("-fx-background-color:#ffff00");
            play.setStyle("-fx-background-color:#ffffff");
            stop.setStyle("-fx-background-color:#ffffff");
        });
        mediaPlayer.setOnStopped(() -> {
            stop.setStyle("-fx-background-color:#ffff00");
            play.setStyle("-fx-background-color:#ffffff");
            pause.setStyle("-fx-background-color:#ffffff");
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
                    progress.setProgress(pos.getValue()/pos.getMax());
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