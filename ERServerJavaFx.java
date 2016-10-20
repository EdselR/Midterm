package Midterm;

/*
Programmer: Edsel Rudy

Date: 19/10/16

Course: CIT-289-01

Description: This is the Server program for the midterm
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class ERServerJavaFx extends Application {

    @Override
    public void start(Stage primaryStage) {

        VBox main = new VBox(10);

        //set the title's font
        Text title = new Text();
        title.setText("EdselR Server");
        title.setFont(Font.font("Verdana", 70));
        title.setFill(Color.BLUE);

        //set the text area to uneditable
        TextArea textArea = new TextArea();
        textArea.setEditable(false);

        //add the title and textarea to the client
        main.getChildren().addAll(title, textArea);
        main.setAlignment(Pos.CENTER);

        //set the scene on the stage and show it
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(main,600,400));
        primaryStage.show();

        primaryStage.setResizable(false);

        new Thread(()-> {

            try{

                //instantiate input streams
                ObjectInputStream inputFromClient;
                ObjectOutputStream outputToClient;

                //create a new server socket
                ServerSocket serverSocket = new ServerSocket(8000);

                //inform he user the server has been created
               Platform.runLater(()-> {

                    textArea.appendText("Server Started at " + new Date() + " \n\n");
                });

                InetAddress servAddress = null;

                //try to get the local host's info
                try{

                    //get the host name and address
                    servAddress = InetAddress.getLocalHost();
                    String servIpAddress = servAddress.getHostAddress();
                    String servHostName = servAddress.getHostName();

                    //inform the user the server's information
                    Platform.runLater(()-> {

                        textArea.appendText(
                                "Host Name: " + servHostName + "\n"
                                + "Ip Address: " + servIpAddress + "\n\n");
                    });

                }
                //if the host is unknown, inform the user
                catch(UnknownHostException ex){
                    String unknown = "Unknown Host";

                    Platform.runLater(()-> {

                        textArea.appendText(unknown);
                    });
                }


                //keep looking for a socket connection
                while(true){

                    //accept the connection
                    Socket socket = serverSocket.accept();

                    //get the client's info, address and name
                    InetAddress clientAddress = socket.getInetAddress();

                    String clientName = clientAddress.getHostName();
                    String clientIP = clientAddress.getHostAddress();

                    //inform the user the client's info
                    Platform.runLater(()-> {

                        textArea.appendText("\nEstablished Connection with Client\n"
                                + "Client Name: " + clientName + "\n"
                                + "Client IP: " + clientIP + " \n\n");
                    });

                    //create an input stream for the server from the client
                    inputFromClient = new ObjectInputStream(socket.getInputStream());

                    //accept the array that was sent
                    int[] arrayFromClient = (int[])inputFromClient.readObject();

                    //use parallel mege sort to the array
                    parallelMergeSort(arrayFromClient);

                    //create a new output stream to send the array to the client
                    outputToClient = new ObjectOutputStream(socket.getOutputStream());
                    outputToClient.writeObject(arrayFromClient);

                    //inform the user that the array has been sent
                    Platform.runLater(()-> {

                        textArea.appendText("Wrote to Client\n");
                    });
                }

            }
            //if the class is not found
            catch(ClassNotFoundException x){
                System.out.println("Class Not Found Exception");
            }
            //when the server is already running, inform the user
            catch(IOException ex){
                System.out.println("IOException ex");

                Platform.runLater(()-> {

                    textArea.appendText("Server is already running");

                });
            }

        }).start();

    }


    //parallel merge sort based on the book's listing
    public static void parallelMergeSort(int[] randArray){

        //create a fork join task and pool
        RecursiveAction mainTask = new SortTask(randArray);
        ForkJoinPool pool = new ForkJoinPool();

        //execute the task
        pool.invoke(mainTask);
    }

    //fork join task
    private static class SortTask extends RecursiveAction {

        private final int THRESHOLD = 500;

        private int[] randArray;

        SortTask(int[] array) {
            randArray = array;
        }

        //compute performs the task
        @Override
        protected void compute() {

            //if the array is not big enough, use the built in array sort method instead
            if (randArray.length < THRESHOLD)
                java.util.Arrays.sort(randArray);
            else {

                // Obtain the first half of the array
                int[] firstHalf = new int[randArray.length / 2];
                System.arraycopy(randArray, 0, firstHalf, 0, randArray.length / 2);

                // Obtain the second half of the array
                int secondHalfLength = randArray.length - randArray.length / 2;
                int[] secondHalf = new int[secondHalfLength];
                System.arraycopy(randArray, randArray.length / 2,
                        secondHalf, 0, secondHalfLength);

                // Recursively sort the two halves
                invokeAll(new SortTask(firstHalf),
                        new SortTask(secondHalf));

                // Merge first and second half into list
                merge(firstHalf, secondHalf, randArray);
            }
        }
    }

    public static void merge(int[] array1, int[] array2, int[] sortedArray){

        //index for the three arrays
        int currIndex1 = 0, currIndex2 = 0, currIndexSorted = 0;

        //while the index for the first and second halves are below their respective array lenghts
        while((currIndex1 < array1.length) && (currIndex2 < array2.length)){

            //if the first array is less
            if(array1[currIndex1] < array2[currIndex2]) {

                //set the sorted array as the first array
                sortedArray[currIndexSorted] = array1[currIndex1];

                currIndexSorted++;
                currIndex1++;
            }

            else {

                //since second array is bigger, set it to that array instead
                sortedArray[currIndexSorted] = array2[currIndex2];

                currIndexSorted++;
                currIndex2++;
            }
        }

        //set all the remaining elements from array 1 to the sorted array
        while(currIndex1 < array1.length) {
            sortedArray[currIndexSorted] = array1[currIndex1];

            currIndexSorted++;
            currIndex1++;
        }

        //set all the remaining elements from array 2 to the sorted array
        while(currIndex2 < array2.length) {
            sortedArray[currIndexSorted++] = array2[currIndex2++];

            currIndexSorted++;
            currIndex2++;
        }
    }


}

