package Client;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class StartView {
    public TextArea content;
    public TextField commandLine;

    public void sendCommand(ActionEvent actionEvent) {
        content.appendText(commandLine.getText()+"\n\r");
        commandLine.setText("");
    }
}
