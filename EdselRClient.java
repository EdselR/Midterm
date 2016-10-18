package Midterm;/**
 * Created by EdselR on 16/10/2016.
 */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class EdselRClient extends Application {

    final int SIZE = 1000;

    Random randomGenerator = new Random();

    int[] randomArray = new int[SIZE];

    private  VBox main = new VBox(5);
    private Button connect = new Button("Connect");
    private TextField ipAddressField = new TextField();
    private Text ipPrompt = new Text();
    private HBox ipAddressLine = new HBox(5);
    private TextArea textArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {


        ipPrompt.setText("Make sure client is in the same network, and enter server IP Address here: ");

        ipAddressLine.getChildren().addAll(ipPrompt, ipAddressField);
        ipAddressLine.setAlignment(Pos.CENTER);


        Text title = new Text();
        title.setText("EdselR Client");
        title.setFont(Font.font("Verdana", 70));
        title.setFill(Color.BLUE);

        textArea.setEditable(false);

        main.getChildren().addAll(title, ipAddressLine, connect, textArea);

        main.setAlignment(Pos.CENTER);


        connect.setOnAction(new ButtonListener());

        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(main,600,400));
        primaryStage.show();

        primaryStage.setResizable(false);

    }

    private class ButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e){
            try {

                Socket socket = new Socket(ipAddressField.getText(), 8000);

                System.out.println("Before Server connection, unsorted:\n");

                textArea.clear();

                InetAddress clientAddress = null;


                    clientAddress = socket.getInetAddress();

                    String servIpAddress = clientAddress.getHostAddress();

                    String servHostName = clientAddress.getHostName();

                    Platform.runLater(()-> {

                        textArea.appendText("Connected to Server,\n" +
                                "Host Name: " + servHostName + "\n"
                                + "Ip Address: " + servIpAddress + "\n\n");

                    });


                Platform.runLater(()-> {

                    textArea.appendText("Sending Unsorted Array: ");

                });

                for (int i = 0; i < SIZE; i++) {
                    int randomInt = randomGenerator.nextInt(100);

                    randomArray[i] = randomInt;

                    System.out.println(randomInt + "\n");

                    if(i == 0)
                    Platform.runLater(()-> {

                        textArea.appendText(Integer.toString(randomInt));

                    });

                    else
                        Platform.runLater(()-> {

                            textArea.appendText(", " + Integer.toString(randomInt));

                        });
                }

                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

                toServer.writeObject(randomArray);

                //
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                int[] fromServerArray = (int[])fromServer.readObject();

                System.out.println("After Server connection, sorted:\n");

                Platform.runLater(()-> {

                    textArea.appendText("\n\nSorted Array received: ");

                });

                for (int i = 0; i < SIZE; i++) {

                    System.out.println(fromServerArray[i] + "\n");

                    int servArrayNum = fromServerArray[i];

                    if(i == 0)
                        Platform.runLater(()-> {

                            textArea.appendText(Integer.toString(servArrayNum));

                        });

                    else
                        Platform.runLater(()-> {

                            textArea.appendText(", " + Integer.toString(servArrayNum));

                        });
                }
            }
            catch(ClassNotFoundException x){
                System.out.println("Class Not Found Exception");
            }
            catch(IOException ex){
                System.out.println("IOException");

                Platform.runLater(()-> {

                    textArea.appendText("Server not found, wrong address entered");
                });

            }
        }
    }
}

