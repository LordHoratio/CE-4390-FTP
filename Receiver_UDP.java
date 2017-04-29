/** Author: Michael Nelson
 *  Date: 4/27/2017
 *  Receiver_UDP: Listens for the UDP connection created by sender and receives file
 */
import java.io.*;
import java.net.*;

public class Receiver_UDP
{
    private final byte CONNECT_REQUEST = 0x00;
    private final byte[] CONNECT_ACK = {0x01};
    private final byte DATA_FLAG = 0x02;
    private final byte SEND_REQUEST_FLAG = 0x03;
    private final byte[] SEND_REQUEST_ACK = {0x04};
    private final byte DATA_ACK_FLAG = 0x05;
    private final byte TEARDOWN_REQUEST = 0x09;
    private final byte[] TEARDOWN_ACK = {0x0a};


    public static void main(String args[]) throws Exception
    {
        Receiver_UDP thereceiver = new Receiver_UDP();
        thereceiver.run();
    }



    private void run() throws Exception
    {
        byte[] buffer = new byte[1003]; // Long enough for anything
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
                int MSB = (receivepacket.getData()[1] & 0xff)*256; // & with 0xff to correct signed byte
                int LSB = (receivepacket.getData()[2] & 0xff); // & with 0xff to correct signed byte
                int numOfPackets = MSB + LSB;


                // Extract char array at the end of SEND_REQUEST and reconstruct filename as String
                char[] filenamechar = new char[receivepacket.getData().length - 3];
                for (int i = 3; i < receivepacket.getData().length; i = i+2)
                {
                    byte a = receivepacket.getData()[i];
                    byte b = receivepacket.getData()[i+1];
                    filenamechar[(i - 3)/2] = (char)((a << 8) | (b & 0xff));
                }
                String filename = new String(filenamechar);
                String append = "received_";
                filename = append.concat(filename);
                System.out.println(filename);

                packet = new DatagramPacket(SEND_REQUEST_ACK, SEND_REQUEST_ACK.length, InetAddress.getLocalHost(), 11109);
                socket.send(packet);


                // Ack sent; begin receiving data packets
                FileOutputStream received = new FileOutputStream(filename);
                BufferedOutputStream bufferout = new BufferedOutputStream(received);
                int z = 0;
                while (z < numOfPackets)
                {
                    socket.receive(receivepacket);

                    // If wrong packet is received, ACK the last sequence # so sender will resend last unACKed packet
                    if (receivepacket.getData()[0] != DATA_FLAG)
                    {
                        z--;
                        byte[] dataAck = new byte[3];
                        dataAck[0] = DATA_ACK_FLAG;
                        dataAck[1] = (byte)(z >> 8);
                        dataAck[2] = (byte)(z);
                        packet = new DatagramPacket(dataAck, dataAck.length, InetAddress.getLocalHost(), 11109);
                        socket.send(packet);
                    }

                    // Write to the file
                    bufferout.write(receivepacket.getData(), 3, receivepacket.getData().length - 3);

                    // Reconstruct sequence #
                    MSB = (int)(receivepacket.getData()[1] & 0xff)*256; // & with 0xff to correct signed byte
                    LSB = (int)(receivepacket.getData()[2] & 0xff); // & with 0xff to correct signed byte
                    int sequencenum = MSB + LSB;

                    // ACK the sequence # just received by sending it back
                    byte[] dataAck = new byte[3];
                    dataAck[0] = DATA_ACK_FLAG;
                    dataAck[1] = (byte)(sequencenum >> 8);
                    dataAck[2] = (byte)(sequencenum);
                    packet = new DatagramPacket(dataAck, dataAck.length, InetAddress.getLocalHost(), 11109);

                    // Increment the last ACKed sequence # by 1
                    // z does not continue incrementing unless sent the next sequence #
                    z = sequencenum+1;
                    socket.send(packet);
                }
                bufferout.flush();
                bufferout.close();
            }
        }


        // TEARDOWN_REQUEST received; send TEARDOWN_ACK then close socket and end application
        packet = new DatagramPacket(TEARDOWN_ACK, TEARDOWN_ACK.length, InetAddress.getLocalHost(), 11109);
        socket.send(packet);
        socket.close();
    }
}