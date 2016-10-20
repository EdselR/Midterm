package Midterm;

/*
Programmer: Edsel Rudy

Date: 19/10/16

Course: CIT-289-01

Description: This is the client program for the midterm

Problem Analysis:
Create a client-server program where the client should send an array of 1000 randomly generated numbers. The server would then use
a parallel or multithreaded merge sort that is non recursive, to sort the array. The server should then send the client back the numbers.
Do not assume that the client and server are in the same computer.

Inputs/Outputs:

(Inputs)
-Server's IP Adress in the client program

(Outputs)
Client
-The unsorted and sorted array
-Whether the server connection is succesful or unsuccessful

Server
-Whether the server is online
-Whether the client has connected to the server
-Server's ip adress

Rules/Guidelines:
-Use javafx for both programs
-Program needs to allow the user to enter the server's ip address
-Use parallel or multithreaded merge sort that is not recursive
-Client and server could or could not be in the same computer
-array needs to the size of 1000

Approach:
My first approach is to keep the program simple and working before implementing further improvements. I created a simple server and client
program that could communicate through the localhost without javafx. The client then sends a small array of size 10 to the server and the server would
 take the array, show it in the server, and it will send it back to the client. After the object send and receive has been established, I used the listing
 on the book to create a merge sort function. After verifying that the merge sort is working, I tried to use parallel processing and multithreading to make
 it more efficient. However, I could not manage to create take advantage of multithreading or parallel processing without the use of recursion. Since I was low
 on time, I decided to have all the requirements met before finding a suitable multithreaded/parallel mergesort algorithm. As such, I focused on the javafx next.
 I implemented java fx to the server first before the client. After confirming that the server and client could communicate outside the local host, I worked further
 on the UI to make it clear and inform the user of any errors. After more trials and errors, I failed to create/find a suitable parallel/multithreaded merge sort.
 Hence, I decided to keep the recursive nature of the algorithm to finish the program.

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
import java.util.Random;

public class EdselRClient extends Application {

    //const size of the random array
    final int SIZE = 1000;

    //instantiate a random class
    Random randomGenerator = new Random();

    //create the random array
    int[] randomArray = new int[SIZE];

    private  VBox main = new VBox(5);
    private Button connect = new Button("Connect");
    private TextField ipAddressField = new TextField();
    private Text ipPrompt = new Text();
    private Text title = new Text();
    private HBox ipAddressLine = new HBox(5);
    private TextArea textArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {

        //prompt for the text field
        ipPrompt.setText("Make sure client is in the same network, and enter server IP Address here: ");

        //add the address field and text on the same line(HBox)
        ipAddressLine.getChildren().addAll(ipPrompt, ipAddressField);
        ipAddressLine.setAlignment(Pos.CENTER);

        //set the font for the title
        title.setText("EdselR Client");
        title.setFont(Font.font("Verdana", 70));
        title.setFill(Color.BLUE);

        //set the text area to uneditable
        textArea.setEditable(false);

        //add all the nodes on the vbox
        main.getChildren().addAll(title, ipAddressLine, connect, textArea);
        main.setAlignment(Pos.CENTER);

        //set the connect button to the button listener event handler
        connect.setOnAction(new ButtonListener());

        //set the scene on the stage and show it
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(main,600,400));
        primaryStage.show();

        primaryStage.setResizable(false);

    }

    //event handler class to start the server connection
    private class ButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e){

            //try to connect to the socket
            try {

                //set the ip address to the textfield
                Socket socket = new Socket(ipAddressField.getText(), 8000);

                //clear the text area
                textArea.clear();

                //set the address to the socket
                InetAddress clientAddress =  socket.getInetAddress();

                //get the host address from the socket
                    String servIpAddress = clientAddress.getHostAddress();

                //get the name from the socket
                    String servHostName = clientAddress.getHostName();

                //append the info to the text area to inform the user
                    Platform.runLater(()-> {

                        textArea.appendText("Connected to Server,\n" +
                                "Host Name: " + servHostName + "\n"
                                + "Ip Address: " + servIpAddress + "\n\n");
                    });


                //inform the user the array has been sent
                Platform.runLater(()-> {

                    textArea.appendText("Sending Unsorted Array: ");
                });

                //loop through the array and append to the textfield
                for (int i = 0; i < SIZE; i++) {
                    int randomInt = randomGenerator.nextInt(100);

                    randomArray[i] = randomInt;

                    //array display formating
                    if(i == 0)
                    Platform.runLater(()-> {

                        textArea.appendText(Integer.toString(randomInt));
                    });

                    else
                        Platform.runLater(()-> {

                            textArea.appendText(", " + Integer.toString(randomInt));
                        });
                }

                //create a new object output stream to the socket
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

                //write the object to the server
                toServer.writeObject(randomArray);

                //create a new input stream from the server socket
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

                //set an array equal to the array received from the socket
                int[] fromServerArray = (int[])fromServer.readObject();

                //inform the user the array has been received
                Platform.runLater(()-> {

                    textArea.appendText("\n\nSorted Array received: ");
                });

                //loop throught the new array and show it to the user
                for (int i = 0; i < SIZE; i++) {

                    int servArrayNum = fromServerArray[i];

                    //display formating
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
            //catch a class not found exception if thrown
            catch(ClassNotFoundException x){
                System.out.println("Class Not Found Exception");
            }
            //when the server socket is not found, inform the user
            catch(IOException ex){
                System.out.println("IOException");

                Platform.runLater(()-> {
                    textArea.appendText("\nServer not found, wrong address entered");
                });

            }
        }
    }
}

