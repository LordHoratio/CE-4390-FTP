import java.io.*;
import java.net.*;

// Main server for TCP file transfer. Opens a socket, splits the bytes of the file into
// segments, and sends each segment through.
// Note: the server only closes when the program ends.
// Should we implement ACKS?

public class TCPServer {
	public static void main(String[] args) throws Exception {
		TCPServer server = new TCPServer(); //server object
		server.run(); 
	}
	
	public void run() throws Exception{
		int counter = 1; // Segment number
		int byteCounter1 = 0; // Display the first byte of segment
		int byteCounter2 = 1000; // Display the last byte of segment
		int arraySize = 1000; // Arbitrary buffer size
		
		ServerSocket sockServer = new ServerSocket(11109); // Opens socket with port 11109
		
		System.out.println("Connected to network!");
		
		Socket sock = sockServer.accept();  // Accepts connection with client
		File transferFile = new File("mac128k.png"); // File that is being sent (hardcoded)
		
		int fileSize = (int) transferFile.length();
		
		System.out.println("The length of the file is " + fileSize + " bytes.");
		
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
	    
	    sock.close(); //Closes port
	    
	    System.out.println("File successfully transferred!");

	}

}
