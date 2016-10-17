package Midterm;/**
 * Created by EdselR on 17/10/2016.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;


public class ERServerJavaFx extends Application {



    @Override
    public void start(Stage primaryStage) {


        VBox main = new VBox(10);


        Text title = new Text();
        title.setText("EdselR Server");
        title.setFont(Font.font("Verdana", 70));
        title.setFill(Color.BLUE);

        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        main.getChildren().addAll(title, textArea);

        main.setAlignment(Pos.CENTER);



        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(main,600,400));
        primaryStage.show();

        primaryStage.setResizable(false);

        new Thread(()-> {

            try{

                ObjectInputStream inputFromClient;
                ObjectOutputStream outputToClient;

                ServerSocket serverSocket = new ServerSocket(8000);
                System.out.println("Server Started");

                Platform.runLater(()-> {

                    textArea.appendText("Server Started at " + new Date() + " \n\n");

                });

                InetAddress servAddress = null;

                try{
                    servAddress = InetAddress.getLocalHost();

                    String servIpAddress = servAddress.getHostAddress();

                    String servHostName = servAddress.getHostName();

                    Platform.runLater(()-> {

                        textArea.appendText(
                                "Host Name: " + servHostName + "\n"
                                + "Ip Address: " + servIpAddress + "\n\n");

                    });
                }catch(UnknownHostException ex){
                    String unknown = "Unknown Host";

                    Platform.runLater(()-> {

                        textArea.appendText(unknown);
                    });
                }


                while(true){

                    Socket socket = serverSocket.accept();

                    InetAddress clientAddress = socket.getInetAddress();

                    String clientName = clientAddress.getHostName();
                    String clientIP = clientAddress.getHostAddress();

                    inputFromClient = new ObjectInputStream(socket.getInputStream());

                    int[] arrayFromClient = (int[])inputFromClient.readObject();

                    Platform.runLater(()-> {

                        textArea.appendText("Established Connection with Client\n"
                        + "Client Name: " + clientName + "\n"
                        + "Client IP: " + clientIP + " \n\n");

                    });


                    mergeSort(arrayFromClient);

                    outputToClient = new ObjectOutputStream(socket.getOutputStream());
                    outputToClient.writeObject(arrayFromClient);

                    System.out.println("Wrote to Client");

                    Platform.runLater(()-> {

                        textArea.appendText("Wrote to Client");

                    });
                }

            }
            catch(ClassNotFoundException x){
                System.out.println("Class Not Found Exception");
            }
            catch(IOException ex){
                System.out.println("IOException ex");

                Platform.runLater(()-> {

                    textArea.appendText("Server is already running");

                });

            }


        }).start();

    }

    public static void mergeSort(int[] randArray){


        if(randArray.length <= 1)
            return;

        int subArray = 1;

        int left, right;

        while(subArray < randArray.length){

            left = 0;
            right = subArray;

            while(right+subArray <= randArray.length){

                mergeArrays(randArray,left,left + subArray,right, right+subArray);

                left = right + subArray;
                right = left + subArray;
            }

            if(right < randArray.length){
                mergeArrays(randArray, left, left+subArray, right, randArray.length);
            }

            subArray *=2;
        }

    }

    public static void mergeArrays( int[] randArray, int startLeft, int stopLeft, int startRight, int stopRight){



        int[] right = new int[stopRight-startRight + 1];
        int[] left = new int[stopLeft-startLeft + 1];

        for(int i = 0, x=startRight; i < (right.length-1); i++, x++){

            right[i] = randArray[x];
        }

        for(int i = 0, x = startLeft; i < (left.length-1); i++, x++){

            left[i] = randArray[x];
        }

        right[right.length - 1] = Integer.MAX_VALUE;
        left[left.length - 1] = Integer.MAX_VALUE;


        for(int i = startLeft, l = 0, r =0; i < stopRight; i++){

            if(left[l] <= right[r]){

                randArray[i] = left[l];

                l++;
            }

            else{
                randArray[i] = right[r];

                r++;
            }
        }


    }

}

