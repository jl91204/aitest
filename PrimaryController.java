package com.ai.aitest.controller;

import com.ai.aitest.*;
import javafx.fxml.FXML;
import java.io.IOException;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        MyApp.setRoot("secondary");
    }
}
