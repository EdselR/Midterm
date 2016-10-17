package Midterm;

import com.sun.deploy.util.ArrayUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by EdselR on 16/10/2016.
 */
public class EdselRServer {

    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;

    public EdselRServer(){

        try{

            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Server Started");


            while(true){

                Socket socket = serverSocket.accept();

                inputFromClient = new ObjectInputStream(socket.getInputStream());

                int[] arrayFromClient = (int[])inputFromClient.readObject();



                mergeSort(arrayFromClient);

                outputToClient = new ObjectOutputStream(socket.getOutputStream());
                outputToClient.writeObject(arrayFromClient);

                for (int i : arrayFromClient) {
                    System.out.print(i);
                }


                System.out.println("Wrote to Client");
            }

        }
        catch(ClassNotFoundException x){
            System.out.println("Class Not Found Exception");
        }
        catch(IOException ex){
            System.out.println("IOException ex");
        }
        finally{

            try{
                inputFromClient.close();
                outputToClient.close();

            }catch(IOException ex){
                System.out.println("IOException Finally block");
            }

        }

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


    public static void main(String []args){new EdselRServer();}
}
