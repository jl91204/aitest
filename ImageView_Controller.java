package com.ai.aitest.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

public class ImageView_Controller {

    @FXML
    private ImageView imageView;
    
    
    public void initialize(URL location, ResourceBundle resources) {
    	
    	// Bind ImageView's width and height to its parent
    //	imageView.fitWidthProperty().bind(imageView.getParent().layoutBoundsProperty().get().);
    //	imageView.fitHeightProperty().bind(imageView.getParent().layoutBoundsProperty().get().heightProperty());
    }
    
}
