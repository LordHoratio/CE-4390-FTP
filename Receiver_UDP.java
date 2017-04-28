/** Author: Alexander Long and Michael Nelson
 *  Date: 4/27/2017
 *  Receiver_UDP: Listens for the UDP connection created by sender and receives file
 */
import java.io.*;
import java.net.*;

public class Receiver_UDP
{
    private final byte CONNECT_REQUEST = 0x00;
    private final byte[] CONNECT_ACK = {0x01};
    private final byte SEND_REQUEST_FLAG = 0x03;
    private final byte SEND_REQUEST_ACK = 0x04;
    private final byte TEARDOWN_REQUEST = 0x09;
    private final byte[] TEARDOWN_ACK = {0x0a};


    public static void main(String args[]) throws Exception
    {
        Receiver_UDP thereceiver = new Receiver_UDP();
        thereceiver.run();
    }



    private void run() throws Exception
    {
        byte[] buffer = new byte[10000]; // Long enough for anything
        DatagramPacket receivepacket = new DatagramPacket(buffer, buffer.length);
        DatagramSocket socket = new DatagramSocket(11110, InetAddress.getLocalHost()); // Creates DatagramSocket on port 11110
        socket.receive(receivepacket); // Waits for initial CONNECT_REQUEST from sender


        // CONNECT_REQUEST received; send CONNECT_ACK
        if (receivepacket.getData()[0] != CONNECT_REQUEST) {return;}
        DatagramPacket packet = new DatagramPacket(CONNECT_ACK, CONNECT_ACK.length, InetAddress.getLocalHost(), 11109);
        socket.send(packet);


        // Wait for a SEND_REQUEST or TEARDOWN message
        boolean teardown = false;
        while (!teardown)
        {
            socket.receive(receivepacket);
            if (receivepacket.getData()[0] == TEARDOWN_REQUEST) {teardown = true; break;}


            else if (receivepacket.getData()[0] == SEND_REQUEST_FLAG)
            {
                // Reconstruct # of packets from the 2nd two bytes received, MSB first
                int MSB = (int)(receivepacket.getData()[1]*256);
                int LSB = (int)(receivepacket.getData()[2]);
                int numOfPackets = MSB + LSB;
                System.out.println("# of packets = " + numOfPackets);


                // Extract char array at the end of SEND_REQUEST and reconstruct filename as String
                char[] filenamechar = new char[receivepacket.getData().length - 3];
                for (int i = 3; i < receivepacket.getData().length - 1; i++)
                {
                    filenamechar[i - 3] = (char)receivepacket.getData()[i];
                }
                String filename = new String(filenamechar);
                System.out.println(filename);
            }
        }


        // TEARDOWN_REQUEST received; send TEARDOWN_ACK then close socket and end application
        packet = new DatagramPacket(TEARDOWN_ACK, TEARDOWN_ACK.length, InetAddress.getLocalHost(), 11109);
        socket.send(packet);
        socket.close();
    }
}