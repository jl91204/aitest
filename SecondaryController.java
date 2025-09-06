package com.ai.aitest.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import com.ai.aitest.*;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        MyApp.setRoot("primary");
    }
}