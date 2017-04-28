/** Author: Alexander Long and Michael Nelson
 *  Date: 4/27/2017
 *  Sender: Responsible for implementing the UI and sender side of FTP protocol for TCP and UDP
 */
import java.util.*;
import java.io.*;
import java.net.*;
public class Sender
{
    public static void main(String args[]) throws Exception
    {
        Scanner in = new Scanner(System.in);
        String connectiontype = "";
        while (connectiontype.isEmpty())
        {
            System.out.println("Type CONNECT_TCP or CONNECT_UDP");
            connectiontype = in.next();
            if (connectiontype.equals("CONNECT_TCP") || connectiontype.equals("CONNECT_UDP")) {break;}
            connectiontype = "";
        }
        //Preferred connection type is chosen

        if (connectiontype.equals("CONNECT_TCP"))
        {
            Sender thesender = new Sender();
            thesender.runTCP();
        }
        else
        {

        }
    }

    public void runTCP() throws Exception
    {
        ServerSocket sockServer = new ServerSocket(11109); // Opens socket with port 11109
        System.out.println("Attempting connection...");
        Socket sock = sockServer.accept();  // Accepts connection with client
        System.out.println("Connected to network!");
        Scanner in = new Scanner(System.in);
        String command = "";
        while (command.isEmpty()) //Waits for user to type SEND
        {
            command = in.next();
            if (command.equals("SEND")) {break;}
            command = "";
            System.out.println("Invalid command");
        }
        //Command received
        System.out.println("Type filename");
        File transferFile = new File(in.next()); // File that is being sent
        int counter = 1; // Segment number
        int byteCounter1 = 0; // Display the first byte of segment
        int byteCounter2 = 1000; // Display the last byte of segment
        int arraySize = 1000; // Arbitrary buffer size
        int fileSize = (int) transferFile.length();
        byte [] byteArray = new byte[arraySize]; // Hold file data temporarily. Holds 50 bytes
        FileInputStream inputStream = new FileInputStream(transferFile); //
        BufferedInputStream bufferIn = new BufferedInputStream(inputStream);
        while ((bufferIn.read(byteArray,0,arraySize)) != -1) // Run till end of file flag is reached
        {
            if (byteCounter2 > fileSize) // Make sure the last byte is listed correctly
                byteCounter2 = fileSize;
            System.out.println("Sending segment " + counter + " which contains bytes " +  + byteCounter1 + "-" + byteCounter2);

            byteCounter1 += arraySize;
            byteCounter2 += arraySize;

            OutputStream outStream = sock.getOutputStream(); //channel to communicate with the client

            counter++; // Increment segment counter

            outStream.write(byteArray, 0, byteArray.length); // Writes byte array to client
            outStream.flush(); // Clears the output stream
        }
        sock.close();
        System.out.println("File successfully transferred!");
    }

    public void runUDP() throws Exception
    {
        Scanner in = new Scanner(System.in);
        String command = "";
        while (command.isEmpty()) //Waits for user to type SEND
        {
            command = in.next();
            if (command.equals("SEND")) {break;}
            command = "";
            System.out.println("Invalid command");
        }
        //Command received
        System.out.println("Type filename");
        File transferFile = new File(in.next()); // File that is being sent
    }
}
