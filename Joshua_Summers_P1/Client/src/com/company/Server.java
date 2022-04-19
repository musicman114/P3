package com.company;
import java.io.File;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.text.DecimalFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

public class Server {

    private static final int SERVER_PORT = 0134;
    static Vector<createCommunicationLoop> ar = new Vector<>();
    static int i = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        //create server socket
        //listen for a connection
        System.out.println("Server started at " + new Date() + "\n");
        while (true) {
            //using a regular *client* socket
            Socket socket = serverSocket.accept();
            DataInputStream inputFromClient =
                    new DataInputStream(socket.getInputStream());

            DataOutputStream outputToClient =
                    new DataOutputStream(socket.getOutputStream());
            String loginList = "";
            createCommunicationLoop cL = new createCommunicationLoop(serverSocket, socket, inputFromClient, outputToClient, loginList);
            Thread t = new Thread(cL);
            ar.add(cL);
            t.start();
        }
    }//end main

}


class createCommunicationLoop implements Runnable{
    ServerSocket serverSocket;
    Socket socket;
    final DataInputStream inputFromClient;
    final DataOutputStream outputToClient;
    String loginList;
    String currentUser;
    public createCommunicationLoop(ServerSocket serverSocket, Socket socket, DataInputStream inputFromClient, DataOutputStream outputToClient, String loginList){
        this.serverSocket = serverSocket;
        this.inputFromClient =inputFromClient;
        this.outputToClient =outputToClient;
        this.loginList = loginList;
        this.socket = socket;

    }
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Override
    public void run(){
        try {
            outer: while(true){
                //now, prepare to send and receive data
                //on output streams


                File logins = new File("/Users/1stit/Desktop/Joshua_Summers_P1/Client/logins.txt");
                boolean valid = false; //bool to check if user is on logins list
                //server loop listening for the client
                //and responding
                inner: while(true) {
                    String strReceived = inputFromClient.readUTF();
                    String[] words = strReceived.split("\\W+");//split input into string array
                    Scanner scan = new Scanner(logins);//read login file
                    String users = scan.nextLine();
                    String[] userArr = users.split("\\W+");
                    System.out.println("Client Said: " + strReceived);
                    while (scan.hasNextLine()) {//compares input with logins file
                        if (words[0].equals("LOGIN") && words[1].equals(userArr[0]) && words[2].equals(userArr[1])) {
                            valid = true;
                            currentUser = words[1];
                            loginList = loginList + words[1] + " ";
                            System.out.println(userArr[0] + " is logged in");
                            outputToClient.writeUTF("hello " + userArr[0]);
                            break;
                        } else {
                            users = scan.nextLine();
                            userArr = users.split("\\W+");
                        }
                    }
                    if (!valid){//if not a valid user reject them
                        System.out.println("Invalid User");
                        outputToClient.writeUTF("Please Enter A Valid Username Or Password");
                    }
                    File solutions = new File(currentUser + "_solutions.txt");//create solutions file for user if none exists
                    solutions.createNewFile();
                    while(valid){ //while logged in
                        String strIn = inputFromClient.readUTF();
                        String[] word = strIn.split(" ");
                        if (strIn.equals("LOGOUT")) {//logs out user by breaking inner loop, returns to listening
                            System.out.println("Client Said: " + strIn);
                            System.out.println(currentUser + " has logged out");
                            outputToClient.writeUTF("200 OK");
                            this.socket.close();
                            break inner;
                        }
                        else if (strIn.equals("SHUTDOWN")) {//shuts down server, closes socket
                            System.out.println("Client Said: " + strIn);
                            System.out.println("Shutting down Server");
                            outputToClient.writeUTF("200 OK");
                            serverSocket.close();
                            this.socket.close();
                            break outer;
                        }
                        else if (word[0].equals("MESSAGE")){
                                boolean logged = false;
                                String[] loggedUsers = loginList.split("\\W+");
                                int l = loggedUsers.length;
                                boolean f = false;
                                String message = "";
                                for (int i = 2; i < word.length; i++){
                                    message = message + word[i];
                                }

                                if ((l == 4) && (loggedUsers[0].equals(word[1]) || loggedUsers[1].equals(word[1]) || loggedUsers[2].equals(word[1]) || loggedUsers[3].equals(word[1]))){
                                    logged = true;
                                    for (createCommunicationLoop mc : Server.ar){
                                        if (mc.currentUser.equals(word[1])){
                                            System.out.println("Message From Client: " + message);
                                            mc.outputToClient.writeUTF("Message From " + currentUser + ": " + message);
                                        }
                                    }
                                }
                             else if ((l == 3) && (loggedUsers[0].equals(word[1]) || loggedUsers[1].equals(word[1]) || loggedUsers[2].equals(word[1]))){
                                logged = true;
                                for (createCommunicationLoop mc : Server.ar){
                                    if (mc.currentUser.equals(word[1])){
                                        System.out.println("Message From Client: " + message);
                                        mc.outputToClient.writeUTF("Message From " + currentUser + ": " + message);
                                    }
                                }
                            }
                            else if ((l == 2) && (loggedUsers[0].equals(word[1]) || loggedUsers[1].equals(word[1]))){
                                logged = true;
                                for (createCommunicationLoop mc : Server.ar){
                                    if (mc.currentUser.equals(word[1])){
                                        System.out.println("Message From Client: " + message);
                                        mc.outputToClient.writeUTF("Message From " + currentUser + ": " + message);
                                    }
                                }
                            }
                            else if ((l == 1) && (loggedUsers[0].equals(word[1]))){
                                logged = true;
                                for (createCommunicationLoop mc : Server.ar){
                                    if (mc.currentUser.equals(word[1])){
                                        System.out.println("Message From Client: " + message);
                                        mc.outputToClient.writeUTF("Message From " + currentUser + ": " + message);
                                    }
                                }
                            }
                                else if((word[1].equals("root") || word[1].equals("john") ||word[1].equals("sally") ||word[1].equals("qiang")) && (logged = false)){
                                    System.out.println(word[1] + " is not logged in. Informing client.");
                                    outputToClient.writeUTF("User " + word[1] + " is not logged in");
                                }
                                else if (word[1].equals("-all") && (currentUser.equals("root"))){
                                    for (createCommunicationLoop mc : Server.ar){
                                        System.out.println("Message From Root: " + " " + message);
                                        mc.outputToClient.writeUTF("Message From Root:" + " " + message);
                                    }
                                }
                                else if (word[1].equals("-all") && (!currentUser.equals("root"))) {
                                    //if user isn't root and tries -all they get rejected
                                    System.out.println("Client Said: " + message);
                                    System.out.println("Client: " + currentUser + " Is Not Root User");
                                    outputToClient.writeUTF("Error: You Are Not The Root User");
                                }
                                else{
                                    System.out.println("User " + word[1] + " doesn't exist. Informing client.");
                                    outputToClient.writeUTF("User " + word[1] + " does not exist");
                                }
                            }
                        else if(word[0].equals("SOLVE") && (word[1].equals("-r") || word[1].equals("-c"))){//solves the requested math

                            FileWriter fw = new FileWriter(solutions.getName(), true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            fw = new FileWriter(currentUser + "_solutions.txt", true);
                            bw = new BufferedWriter(fw);

                            if(word[1].equals("-c")){//handles all circle cases
                                try{//checks for a radius
                                    String r = word[2];
                                    int radius = Integer.parseInt(r);
                                    double c = radius * 2 * 3.14;
                                    double a = (radius * radius) * 3.14;
                                    bw.write("radius " + radius + ": Circle's circumference is " + df.format(c) + " and area is " + df.format(a) + "\n");
                                    bw.close();
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF("Circle's circumference is " + df.format(c) + " and area is " + df.format(a));
                                }
                                catch( Exception e ){//no radius given
                                    bw.write("Error: No Radius Found" + "\n");
                                    bw.close();
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF("Error: No Radius Found");
                                }
                            }
                            else{//handles all rectangle cases
                                try {//checks for 2 sides
                                    int s1 = Integer.parseInt(word[2]);
                                    int s2 = Integer.parseInt(word[3]);
                                    double p = 2 * (s1 + s2);
                                    double ar = s1 * s2;
                                    bw.write("sides " + s1 + " " + s2 + ": Rectangle's perimeter is " + df.format(p) + " and area is " + df.format(ar) + "\n");
                                    bw.close();
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF("Rectangle's perimeter is " + df.format(p) + " and area is " + df.format(ar));
                                }
                                catch(Exception e){//checks for 1 side
                                    try{
                                        int s1 = Integer.parseInt(word[2]);
                                        double p = 2 * (s1 + s1);
                                        double ar = s1 * s1;
                                        bw.write("sides " + s1 + " " + s1 + ": Rectangle's perimeter is " + df.format(p) + " and area is " + df.format(ar) + "\n");
                                        bw.close();
                                        System.out.println("Client Said: " + strIn);
                                        outputToClient.writeUTF("Rectangle's perimeter is " + df.format(p) + " and area is " + df.format(ar));
                                    }
                                    catch(Exception d){//no sides found
                                        bw.write("Error: No Sides Found" + "\n");
                                        bw.close();
                                        System.out.println("Client Said: " + strIn);
                                        outputToClient.writeUTF("Error: No Sides Found");
                                    }
                                }

                            }
                        }
                        else if (word[0].equals("LIST")) {//lists all user's requests from their files
                            try {
                                //if user is root and requests all
                                if (word[1].equals("-all") && currentUser.equals("root")) {
                                    Scanner rScan = new Scanner(logins);
                                    String all = "\n";
                                    while(rScan.hasNextLine()){//checks each file with the names from login.txt
                                        String user = rScan.nextLine();
                                        String[] userList = user.split("\\W+");
                                        File alls = new File(userList[0] + "_solutions.txt");//creates fill if none exists
                                        alls.createNewFile();
                                        all = all + userList[0] + "\n";
                                        if (alls.length() > 0){//if the file isn't empty scan all lines to the string
                                            Scanner allScan = new Scanner(alls);
                                            while(allScan.hasNextLine()){
                                                String j = allScan.nextLine();
                                                all = all + j + "\n";
                                            }
                                        }
                                        else{//no requests yet
                                            all = all + "No Interactions Yet" + "\n";
                                        }
                                    }
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF(all);
                                }
                                else if (word[1].equals("-all") && (!currentUser.equals("root"))) {
                                    //if user isn't root and tries -all they get rejected
                                    System.out.println("Client Said: " + strIn);
                                    System.out.println("Client: " + currentUser + " Is Not Root User");
                                    outputToClient.writeUTF("Error: You Are Not The Root User");
                                }
                                else if(!word[1].equals("-all")){
                                    System.out.println("Invalid Command Received "
                                            + strIn);
                                    outputToClient.writeUTF("300 Invalid Command");
                                }
                            }
                            catch(Exception e) {
                                //executes this code if LIST is the only command, not followed by "-all" or anything else
                                if (solutions.length() > 0) {//prints the user's solution file
                                    Scanner scanSol = new Scanner(solutions);
                                    String sol = "\n";
                                    while (scanSol.hasNextLine()) {
                                        String s = scanSol.nextLine();
                                        sol = sol + s + "\n";
                                    }
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF(sol);
                                }
                                else{
                                    System.out.println("Client Said: " + strIn);
                                    outputToClient.writeUTF("No Interactions Yet");
                                }
                            }
                        }
                        else {//bad command
                            System.out.println("Invalid Command Received "
                                    + strIn);
                            outputToClient.writeUTF("300 Invalid Command");

                        }
                    }
                }
            }//end server loop
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }//end try-catch
    }//end createCommunicationLoop
}
