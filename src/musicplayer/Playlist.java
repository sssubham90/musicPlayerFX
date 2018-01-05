/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author KIIT
 */
public class Playlist {
    
    private Scene scene;
    private final Stage player;
    private final DirectoryChooser directoryChooser;
    private TrackDetails trackDetails;
    private TableView<TrackDetails> tableView;
    private MediaPlayer mPlayer;
    private File[] files;
    private int counter;
    private ObservableList<TrackDetails> data;
    private Track track;
    private String directoryPath;
    
    public class TrackDetails{
        private String Title="",Album="",Artist="",Year="";
        private String FilePath="";

        public TrackDetails() {
            this.Title = "Title";
            this.Album = "Album";
            this.Artist = "Artist";
            this.Year = "Year";
            this.FilePath = "Path";
        }
        
        public TrackDetails(String Title, String Album, String Artist, String Year, String FilePath) {
            this.Title = Title;
            this.Album = Album;
            this.Artist = Artist;
            this.Year = Year;
            this.FilePath = FilePath;
        }

        private TrackDetails(TrackDetails trackDetails) {
            this.Title = trackDetails.getTitle();
            this.Album = trackDetails.getAlbum();
            this.Artist = trackDetails.getArtist();
            this.Year = trackDetails.getYear();
            this.FilePath = trackDetails.getFilePath();
        }
        
        public void clear(){
            Title="";
            Album="";
            Artist="";
            Year="";
            FilePath="";
        }

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
        }
        
        public void setTitle(String Title) {
            this.Title = Title;
        }

        public void setAlbum(String Album) {
            this.Album = Album;
        }

        public void setArtist(String Artist) {
            this.Artist = Artist;
        }

        public void setYear(String Year) {
            this.Year = Year;
        }

        public String getTitle() {
            return Title;
        }

        public String getAlbum() {
            return Album;
        }

        public String getArtist() {
            return Artist;
        }

        public String getYear() {
            return Year;
        }
        
    }
    
    public Playlist(Stage stage) {
        player=stage;
        directoryPath="";
        directoryChooser = new DirectoryChooser();
        trackDetails = new TrackDetails();
        data = FXCollections.observableArrayList();
        tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.setItems(data);
        TableColumn<TrackDetails, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        TableColumn<TrackDetails, String> colAlbum = new TableColumn<>("Album");
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("Album"));
        TableColumn<TrackDetails, String> colArtist = new TableColumn<>("Artist");
        colArtist.setCellValueFactory(new PropertyValueFactory<>("Artist"));
        TableColumn<TrackDetails, String> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(new PropertyValueFactory<>("Year"));
        tableView.getColumns().addAll(colTitle,colAlbum,colArtist,colYear);
        tableView.setOnMouseClicked( eh -> {
            if(eh.getClickCount()==2){
                track = new Track(stage);
                stage.setScene(track.createScene(scene.getWidth(),scene.getHeight()));
                track.openFile(new File(tableView.getSelectionModel().getSelectedItem().getFilePath()));
            }
        });
        tableView.setOnKeyPressed(eh -> {
            if(eh.getCode().equals(KeyCode.ENTER)){
                track = new Track(stage);
                stage.setScene(track.createScene(scene.getWidth(),scene.getHeight()));
                track.openFile(new File(tableView.getSelectionModel().getSelectedItem().getFilePath()));
            }
        });
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("config.txt")))) {
            directoryPath=reader.readLine();
            reader.close();
        }
        catch(FileNotFoundException e){
        }
        catch(IOException e){
        }
    }
        
    public Scene createScene(double width,double height){
        BorderPane parent=new BorderPane();
        parent.setTop(menuMaker());
        parent.setCenter(tableView);
        scene =new Scene(parent,width,height);
        resizeListener();
        if(!directoryPath.isEmpty()){
            files=new File(directoryPath).listFiles();
            counter=0;
            fetch();
        }
        return scene;
    }
    
    public void fetch(){
        try{
            mPlayer=new MediaPlayer(new Media(files[counter++].toURI().toString()));
        }
        catch(MediaException e){
            fetch();
        }
        catch(ArrayIndexOutOfBoundsException e){
            mPlayer.dispose();
            return;
        }
        trackDetails.clear();
        trackDetails.setTitle(files[counter-1].getName());
        mPlayer.setOnReady(() -> {
            try{
                trackDetails.setAlbum(mPlayer.getMedia().getMetadata().get("album").toString());
                trackDetails.setArtist(mPlayer.getMedia().getMetadata().get("artist").toString());
                trackDetails.setTitle(mPlayer.getMedia().getMetadata().get("title").toString());
                trackDetails.setYear(mPlayer.getMedia().getMetadata().get("year").toString());
                trackDetails.setFilePath(files[counter-1].getPath());
            }
            catch(NullPointerException e){}
            addToList(trackDetails);
            mPlayer.dispose();
            fetch();
        });
        System.gc();
    }
    
    public void addToList(TrackDetails td){
        data.add(new TrackDetails(td));
    }
    
    public MenuBar menuMaker(){
        MenuItem chooser=new MenuItem("_Choose Directory");
        MenuItem exit=new MenuItem("_Exit");
        chooser.addEventHandler(EventType.ROOT, eh->{
            File directory = directoryChooser.showDialog(player);
            files=directory.listFiles();
            counter=0;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("config.txt")))) {
                writer.write(directory.getAbsolutePath());
                writer.close();
            }
            catch(FileNotFoundException e){
            }
            catch(IOException e){
            }
            data.clear();
            fetch();
        });
        exit.addEventHandler(EventType.ROOT, eh->{if(exit()) player.close();});
        Menu file=new Menu("File");
        file.getItems().addAll(chooser,exit);
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(file);
        menuBar.setPadding(new Insets(2));
        return menuBar;
    }
    
    public void resizeListener(){
        scene.widthProperty().addListener(cl -> {
            tableView.getColumns().get(0).setMinWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(0).setMaxWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(1).setMinWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(1).setMaxWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(2).setMinWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(2).setMaxWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(3).setMinWidth(scene.getWidth()*0.25);
            tableView.getColumns().get(3).setMaxWidth(scene.getWidth()*0.25);
        });
    }

    public boolean exit(){
        return ConfirmationBox.show("Are you sure you want to exit MusicPlayer?", "Exit!", "Yes", "No");
    }
    
}