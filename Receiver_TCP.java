/** Author: Alexander Long
 *  Date: 4/27/2017
 *  Receiver_TCP: Listens for the TCP connection created by sender
 */
import java.io.*;
import java.net.*;

public class Receiver_TCP
{
    public static void main(String args[]) throws Exception
    {
        Receiver_TCP theReceiver = new Receiver_TCP();
        theReceiver.run();
    }



    public static void run() throws Exception
    {
        int counter = 1;
        int byteCounter1 = 0;
        int byteCounter2 = 1000;	// Byte size of 50 (same as server)
        int segmentSize = 1000;

        int filesize = 1000000; // Int large enough to hold the file size
        int bytesRead; // Holds the int value of the bytes
        int currentTotal = 0; // Counts number of bytes read

        Socket socket = new Socket("localhost", 11109); // Open socket at port 11109

        byte [] byteArray = new byte[filesize]; // Buffer for temporary data

        FileOutputStream receiveFile = new FileOutputStream("received.png"); 	// File being received
        BufferedOutputStream bufferOut = new BufferedOutputStream(receiveFile); // Writes data to output file
        InputStream input = socket.getInputStream();
        bytesRead = input.read(byteArray, 0, byteArray.length - currentTotal); 	// Read data from input, store in byte array
        currentTotal = bytesRead;

        do
        {
            bytesRead = input.read(byteArray, currentTotal, segmentSize); 	// Update total bytes read
            if (bytesRead >= 0)
            {
                currentTotal += bytesRead;

                System.out.println("Received part " + counter + " which includes bytes " + byteCounter1 + "-" + byteCounter2);

                counter++;						// Increment the counter
                byteCounter1 += segmentSize; 	// Increment byteCounter by segment size.
                byteCounter2 += segmentSize;
            }

        }
        while (bytesRead > -1);
        System.out.println("Received part " + counter + " which includes bytes " + byteCounter1 + "-" + currentTotal);
        bufferOut.write(byteArray, 0, currentTotal); 	// Add data to file
        bufferOut.flush(); 								// Clears the output stream
        bufferOut.close();
        socket.close(); 								// Close the stream

        System.out.print("File has been received!");
    }
}
