package Midterm;/**
 * Created by EdselR on 16/10/2016.
 */
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class EdselRClient extends Application {

    //host name/ip
    String host = "localhost";

    final int SIZE = 5;

    Random randomGenerator = new Random();

    int[] randomArray = new int[SIZE];

    @Override
    public void start(Stage primaryStage) {

        try {

            Socket socket = new Socket(host, 8000);

            System.out.println("Before Server connection, unsorted:\n");

            for (int i = 0; i < SIZE; i++) {
                int randomInt = randomGenerator.nextInt(100);

                randomArray[i] = randomInt;

                System.out.println(randomInt + "\n");
            }

            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

            toServer.writeObject(randomArray);

            //
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

            int[] fromServerArray = (int[])fromServer.readObject();

            System.out.println("After Server connection, sorted:\n");

            for (int i = 0; i < SIZE; i++) {

                System.out.println(fromServerArray[i] + "\n");
            }
        }
        catch(ClassNotFoundException x){
            System.out.println("Class Not Found Exception");
        }
        catch(IOException ex){
            System.out.println("IOException");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}

