import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;



public class Client {

    private static final int SERVER_PORT = 0134;

    public static void main(String[] args) {

        DataOutputStream toServer;
        DataInputStream fromServer;
        Scanner input =
                new Scanner(System.in);
        String message;

        //attempt to connect to the server
        try {
            Socket socket =
                    new Socket("localhost", SERVER_PORT);

            //create input stream to receive data
            //from the server
            fromServer =
                    new DataInputStream(socket.getInputStream());

            toServer =
                    new DataOutputStream(socket.getOutputStream());
            boolean nLog = true;

            while(nLog) { //runs while the user is not logged in
                System.out.print("Send command to server:\t");
                message = input.nextLine();
                toServer.writeUTF(message);

                //received message:
                message = fromServer.readUTF();
                System.out.println("Server says: " + message);
                String[] check = message.split("\\W+"); //checks if user is logged in
                if (check[0].equals("hello")){
                    nLog = false;
                }
            }
            while(!nLog) {//while user is logged in
                System.out.print("Send command to server:\t");
                message = input.nextLine();
                toServer.writeUTF(message);
                //user can now access logout and shutdown
                if (message.equals("SHUTDOWN")) {//breaks if shutdown
                    message = fromServer.readUTF();
                    System.out.println("Server says: " + message);
                    break;
                }
                if (message.equals("LOGOUT")) {//breaks if logged out
                    message = fromServer.readUTF();
                    System.out.println("Server says: " + message);
                    break;
                }
                message = fromServer.readUTF();
                System.out.println("Server says: " + message);
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }//end try-catch


    }//end main
}

