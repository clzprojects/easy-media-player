/*
 * Copyright (C) 2014 DeveloperBhuwan
 * Developer: Bhuwan Pd. Upadhyay
 */
package com.developerbhuwan.easymediaplayer.controllers;

import com.developerbhuwan.easymediaplayer.MainApp;
import com.developerbhuwan.easymediaplayer.exception.MediaFileEmptyException;
import com.developerbhuwan.easymediaplayer.model.MediaFile;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author DeveloperBhuwan
 */
public class MainViewController implements Initializable {

    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private StackPane playAreaStakPane;
    @FXML
    private ImageView easyPlayerDescripImageView;
    @FXML
    private AnchorPane controlAnchorPane;
    @FXML
    private Label currentPlayTimeLabel;
    @FXML
    private Slider currentProgressSlider;
    @FXML
    private Label totalLengthLabel;
    @FXML
    private Button stopButton;
    @FXML
    private Button listButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button nextButton;
    @FXML
    private Slider volumeSlider;

    private MainApp application;
    private ArrayList<MediaFile> mediaFiles;
    private int currentPosition = -1;
    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private String fileURIString;
    private FileChooser fc;
    private Stage aboutStage;
    private Stage listStage;

    public void setApp(MainApp application) {
        this.application = application;
        setAppAction();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resetMediaFiles();
        initMenusActions();
        initPlayingAreasAction();
        initControllerActions();
        addPlayStyle();
        currentProgressSlider.setDisable(true);
    }

    private void initMenusActions() {
        openFileMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File selectedFile = openFile();
                if (selectedFile != null) {
                    addMediaFiles(selectedFile);
                    try {
                        setCurrentPlayMediaPosition(mediaFiles.size() - 1);
                        playMediaFiles();
                    } catch (MediaFileEmptyException | URISyntaxException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        exitMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                closeApp();

            }
        });
        aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/AboutView.fxml"));
                    aboutStage = new Stage();
                    Scene aboutScene = new Scene(root, 600, 360);
                    aboutStage.setScene(aboutScene);
                    aboutStage.setTitle("About");
                    aboutStage.getIcons().add(new Image("/images/easy_media_player_logo.png"));
                    aboutStage.centerOnScreen();
                    aboutStage.setAlwaysOnTop(true);
                    aboutStage.show();
                    aboutStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                        @Override
                        public void handle(WindowEvent event) {
                            aboutStage = null;
                        }
                    });
                } catch (IOException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void initPlayingAreasAction() {
        playAreaStakPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                } else {
                    dragEvent.consume();
                }
            }
        });

        playAreaStakPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Dragboard db = dragEvent.getDragboard();
                boolean sucess = false;
                if (db.hasFiles()) {
                    sucess = true;
                    int count = 0;
                    for (File file : db.getFiles()) {
                        if (isAudioFile(file.getName()) || isVideoFile(file.getName())) {
                            addMediaFiles(file);
                            if (count == 0) {
                                setCurrentPlayMediaPosition(mediaFiles.size() - 1);
                            }
                            count++;
                        }
                    }
                    try {
                        playMediaFiles();
                    } catch (MediaFileEmptyException | URISyntaxException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dragEvent.setDropCompleted(sucess);
                dragEvent.consume();
            }
        });

        playAreaStakPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    if (application.getStage().isFullScreen()) {

                        application.getStage().setFullScreen(false);
                    } else {
                        application.getStage().setFullScreen(true);
                        application.getStage().setFullScreenExitHint(null);
                    }
                }
            }
        });
    }

    private void initControllerActions() {
        currentProgressSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (currentProgressSlider.isValueChanging() && mediaPlayer != null) {
                    updateProgress();
                }
            }

        });
        currentProgressSlider.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (mediaPlayer != null) {
                    updateProgress();
                }
            }
        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (volumeSlider.isValueChanging() && mediaPlayer != null) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            }
        });

        volumeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                stopMedia();
            }
        });
        listButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                showPlayList();
            }
        });
        prevButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (mediaPlayer != null) {
                    try {
                        if (currentPosition > 0) {
                            setCurrentPlayMediaPosition(--currentPosition);
                        }
                        playMediaFiles();
                    } catch (MediaFileEmptyException | URISyntaxException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    event.consume();
                }

            }
        });

        playPauseButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                        addPlayStyle();
                    } else if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                        try {
                            setCurrentPlayMediaPosition(0);
                            playMediaFiles();
                        } catch (MediaFileEmptyException | URISyntaxException ex) {
                            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                        mediaPlayer.play();
                        addPauseStyle();
                    }
                } else {
                    event.consume();
                }

            }
        });
        nextButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (mediaPlayer != null) {
                    try {
                        if (currentPosition > -1 && currentPosition < mediaFiles.size() - 1) {
                            setCurrentPlayMediaPosition(++currentPosition);
                        }
                        playMediaFiles();

                    } catch (MediaFileEmptyException | URISyntaxException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    event.consume();
                }
            }
        });
    }

    private void setCurrentPlayMediaPosition(int i) {
        this.currentPosition = i;
    }

    private File openFile() {
        fc = new FileChooser();
        fc.setTitle("Open Media File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Audio Files", "*.mp3", "*.wav"),
                new ExtensionFilter("Video Files", "*.mp4", "*.flv")
        );
        return fc.showOpenDialog(application.getStage());
    }

    private void resetMediaFiles() {
        mediaFiles = new ArrayList<>();
    }

    private void addMediaFiles(File f) {
        MediaFile mf = new MediaFile();
        mf.setFileName(f.getName());
        mf.setFilePath(f.getAbsolutePath());
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        mf.setFileSize(Double.valueOf(twoDForm.format((double) f.getTotalSpace() / (1024 * 1024))));
        mf.setFileType(getFileType(f.getName()));
        mediaFiles.add(mf);
    }

    private void playMediaFiles() throws MediaFileEmptyException, URISyntaxException {
        if (currentPosition > -1 && mediaFiles.size() > 0 && currentPosition < mediaFiles.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            MediaFile mf = mediaFiles.get(currentPosition);
            fileURIString = new File(mf.getFilePath()).toURI().toString();
            media = new Media(fileURIString);
            mediaPlayer = new MediaPlayer(media);

            if (mf.getFileType().equalsIgnoreCase("audio")) {
                if (mediaView != null) {
                    playAreaStakPane.getChildren().remove(mediaView);
                }
                easyPlayerDescripImageView.setVisible(true);
                playMedia();
            } else if (mf.getFileType().equalsIgnoreCase("video")) {
                if (mediaView != null) {
                    playAreaStakPane.getChildren().remove(mediaView);
                }
                easyPlayerDescripImageView.setVisible(false);
                mediaView = new MediaView(mediaPlayer);
                playAreaStakPane.getChildren().add(mediaView);
                playMedia();
            }
        } else {
            throw new MediaFileEmptyException("Empty");
        }
    }

    private String getFileType(String name) {
        if (isAudioFile(name)) {
            return "Audio";
        } else if (isVideoFile(name)) {
            return "Video";
        }
        return "Unknown";
    }

    private boolean isAudioFile(String name) {
        return name.endsWith(".mp3") || name.endsWith(".MP3") || name.endsWith(".wav") || name.endsWith(".WAV");
    }

    private boolean isVideoFile(String name) {
        return name.endsWith(".mp4") || name.endsWith(".MP4") || name.endsWith(".flv") || name.endsWith(".FLV");
    }

    private void playMedia() {
        mediaPlayer.setOnReady(new Runnable() {

            @Override
            public void run() {
                currentProgressSlider.setMin(0);
                currentProgressSlider.setValue(0);
                Duration total = mediaPlayer.getTotalDuration();
                currentProgressSlider.setMax(total.toSeconds());
                totalLengthLabel.setText(formatDuration(total));
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                mediaPlayer.play();
                addPauseStyle();
                currentProgressSlider.setDisable(false);
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                currentProgressSlider.setValue(newValue.toSeconds());
                if (formatDuration(newValue).equalsIgnoreCase("00:00")) {
                    currentPlayTimeLabel.setText("--:--");
                } else {
                    currentPlayTimeLabel.setText(formatDuration(newValue));
                }
                if (mediaView != null) {
                    mediaView.setFitWidth(playAreaStakPane.getWidth());
                    mediaView.setFitHeight(playAreaStakPane.getHeight());
                }
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {
                System.out.println(mediaFiles.size() - 1 + ":" + currentPosition);
                if (mediaFiles.size() - 1 == currentPosition) {
                    totalLengthLabel.setText("--:--");
                    currentPlayTimeLabel.setText("--:--");
                    stopMedia();
                } else {
                    currentPosition++;
                    try {
                        playMediaFiles();
                    } catch (MediaFileEmptyException | URISyntaxException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    private String formatDuration(Duration duration) {
        double millis = duration.toMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60));
        int hours = (int) (millis / (1000 * 60 * 60));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {

            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private void updateProgress() {
        mediaPlayer.seek(Duration.seconds(currentProgressSlider.getValue()));
        Duration current = mediaPlayer.getCurrentTime();
        currentProgressSlider.setValue(current.toSeconds());
        currentPlayTimeLabel.setText(formatDuration(current));
        if (formatDuration(current).equals("00:00")) {
            currentPlayTimeLabel.setText("--:--");
        } else {
            currentPlayTimeLabel.setText(formatDuration(current));
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            playAreaStakPane.getChildren().remove(mediaView);
            easyPlayerDescripImageView.setVisible(true);
            currentProgressSlider.setDisable(true);
            mediaPlayer.stop();
            mediaPlayer.setOnPaused(null);
            mediaPlayer.setOnPlaying(null);
            mediaPlayer.setOnReady(null);
            addPlayStyle();
            totalLengthLabel.setText("--:--");
            currentPlayTimeLabel.setText("--:--");
        }
    }

    private void showPlayList() {
        if (mediaFiles != null) {
            final ListView<String> list = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList();
            for (MediaFile file : mediaFiles) {
                items.add(file.getFileName());
            }
            list.setItems(items);
            list.setPrefWidth(100);
            list.setPrefHeight(70);
            list.setOrientation(Orientation.VERTICAL);
            list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            listStage = new Stage();
            listStage.setTitle("Playlist");
            Scene sc = new Scene(list, 300, 400);
            listStage.setScene(sc);
            listStage.getIcons().add(new Image("/images/easy_media_player_logo.png"));
            listStage.setOpacity(0.7);
            listStage.setX(application.getStage().getX() + 10);
            listStage.setY(application.getStage().getY() + 65);
            listStage.setAlwaysOnTop(true);
            list.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() > 1 && list.getSelectionModel().getSelectedIndex() > -1) {
                        try {
                            setCurrentPlayMediaPosition(list.getSelectionModel().getSelectedIndex());
                            playMediaFiles();
                        } catch (MediaFileEmptyException ex) {
                            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            });
            listButton.setDisable(true);
            listStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    listButton.setDisable(false);
                    listStage = null;
                }
            });
            listStage.getIcons().add(new Image("/images/easy_media_player_logo.png"));
            listStage.show();
        }
    }

    private void addPauseStyle() {
        playPauseButton.getStyleClass().remove("playButton");
        playPauseButton.getStyleClass().add("pauseButton");
    }

    private void addPlayStyle() {
        playPauseButton.getStyleClass().remove("pauseButton");
        playPauseButton.getStyleClass().add("playButton");
    }

    private void setAppAction() {
        application.getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                closeApp();
            }

        });
     
    }

    private void closeApp() {
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.dispose();
        }
        application.getStage().close();
        if (aboutStage != null) {
            aboutStage.close();
        }
        if (listStage != null) {
            listStage.close();
        }
    }
}
