import com.sun.security.ntlm.Client;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ListView<String> lv;
    public TextField txt;
    public Button send;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private Path path;
    private final String clientFilesPath = "./src/main/resources/NIOdir";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            os.write("Hello server".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(clientFilesPath);
        for (String file : dir.list()) {
            lv.getItems().add(file);
        }
    }

    // ./download fileName
    // ./upload fileName
    public void sendCommand(ActionEvent actionEvent) throws IOException {
        String command = txt.getText();
        String [] op = command.split(" ");
        ByteBuffer buffer = ByteBuffer.allocate(80);

        String fileName = op[1];
        if (op[0].equals("./download")) {
            try {
                os.write(command.getBytes());
                String success = "";
                while (is.available() == 0) {
                    ;
                }
                while (is.available() > 0) {
                    int add = is.read();
                    if (add == 10) break;
                    else success += String.valueOf((char) add);
                }
                buffer.flip();
                if (success.equals("OK")) {
                    while (is.available() > 0) {
                        is.read(buffer.array());
                    }

                    Path path = Paths.get(clientFilesPath, fileName);
                    if (!Files.exists(path)) {
                        Files.createFile(path);
                        lv.getItems().add(fileName);
                    }
                    Files.write(path, buffer.array(), StandardOpenOption.APPEND);
                    buffer.clear();
                }
                System.out.println(success);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        } else if (op[0].equals("./upload")) {
            try {
                os.write(command.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
