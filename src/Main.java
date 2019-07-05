import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private File file = null;

    static final String NOFILE = "文件不能为空\n ";
    static final String NOPASS = "口令不能为空\n ";

    @Override
    public void start(Stage stage){
        FileChooser fc = new FileChooser();
        fc.setTitle("请选择文件");

        Button browseButton = new Button("浏览");
        Button encryptButton = new Button("加密");
        Button decryptButton = new Button("解密");

        Label fileLabel = new Label("文件");
        Label passLabel = new Label("口令");
        Label infoLabel = new Label("请选择要加/解密的文件");

        TextField passTextField = new TextField();
        TextField fileTextField = new TextField();
        fileTextField.setDisable(true);

        GridPane gridPane = new GridPane();

        gridPane.add(fileLabel, 0, 0);
        gridPane.add(fileTextField, 1, 0);
        gridPane.add(browseButton, 2, 0);
        gridPane.add(passLabel, 0, 1);
        gridPane.add(passTextField, 1, 1);
        gridPane.add(infoLabel, 0, 2, 3, 1);
        gridPane.add(encryptButton, 0, 3);
        gridPane.add(decryptButton, 1, 3);

        browseButton.setOnAction(e -> {
            file = fc.showOpenDialog(stage);
            if (file == null) {
                fileTextField.setText(null);
            } else {
                fileTextField.setText(file.getName());
                infoLabel.setText("请输入口令");
            }
        });

        encryptButton.setOnAction(e -> {
            if (file == null || !file.exists()) {
                infoLabel.setText(NOFILE);
            } else if (passTextField.getText().equals("")) {
                infoLabel.setText(NOPASS);
            } else {
                Encryption Encryption = new Encryption(file, passTextField.getText());
                try {
                    if (Encryption.encrypt()) {
                        infoLabel.setText("加密成功");
                    } else {
                        infoLabel.setText("加密失败，请检查文件是否有误 ");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        decryptButton.setOnAction(e -> {
            if (file == null || !file.exists()) {
                infoLabel.setText(NOFILE);
            } else if (passTextField.getText().equals("")) {
                infoLabel.setText(NOPASS);
            } else {
                Encryption Encryption = new Encryption(file, passTextField.getText());
                try {
                    if (Encryption.decrypt()) {
                        infoLabel.setText("解密成功");
                    } else {
                        infoLabel.setText("解密失败，请检查口令或文件是否正确\n ");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Scene scene = new Scene(gridPane);
        stage.setTitle("文件加密管理器");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
