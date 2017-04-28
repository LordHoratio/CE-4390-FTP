# CE-4390-FTP
Custom file transfer protocol for class project

Use Case:
  1. User runs application and command prompt opens
  2. User types CONNECT_TCP or CONNECT_UDP, depending on which protocol is desired
  3. Program confirms whether or not connection with receiver was sucessful
  4. User sends file with SEND command followed by the name of the file being sent, which must be in same folder as application
  5. Program sends file with desired transport protocol and confirms when the transfer is complete. File should appear in same folder
     as the receiving application
  6. User can use the SEND command after the program confirms the successful transfer of last file. TEARDOWN command will close all
     connections and terminate program
     
FTP Messages:
Every message starts with a 1 byte flag indicating what it is, followed by a variety of different fields depending on message type

0. TCP_CONNECT_REQUEST - [Flag (0x00) - 1 byte]

1. UDP_CONNECT_REQUEST - [Flag (0x01) - 1 byte]

2. CONNECTION_ACK - [Flag (0x02) - 1 byte]

3. SEND_REQUEST - [Flag (0x03) - 1 byte]_[# of packets - 5 bytes]_[Name - 1-20 bytes]

4. REQUEST_ACK - [Flag (0x04) - 1 byte]

5. TCP_DATA - [Flag (0x05) - 1 byte]_[Packet # - 5 bytes]_[DATA]

6. UDP_DATA - [Flag (0x06) - 1 byte]_[Packet # - 5 bytes]_[Checksum - 4 bytes]_[DATA]

7. FILE_RECIEVED_ACK - [Flag (0x07) - 1 byte]_[0x0 = TCP, 0x1 = UDP File Received, 0x2 = UDP Resend Packets - 1 byte]

8. RESEND_ACK - [Flag (0x08) - 1 byte]

9. RESEND_REQUEST - [Flag (0x09) - 1 byte]_[Packet # - 5 bytes]

10. TEARDOWN_REQUEST - [Flag (0x0A) - 1 byte]

11. TTEARDOWN_ACK - [Flag (0x0B) - 1 byte]

Protocol:
- Allows user to choose protocol
- Case: TCP
  - Sends TCP_CONNECT_REQUEST and waits for CONNECTION_ACK
  - Divides file 1 KB per packet
  - Sends TCP_SEND_REQUEST message with filename and # of packets to expect
  - Sends TCP_DATA messages (starting at packet # 0 for every new file) once REQUEST_ACK message is received
  - Prompt resets for next SEND or TEARDOWN command once FILE_RECEIVED_ACK message is received with data field 0x0
- Case: UDP
  - Sends UDP_CONNECT_REQUEST and waits for CONNECTION_ACK
  - Divides file 1 KB per packet
  - Sends UDP_SEND_REQUEST message with filename and # of packets to expect
  - Sends UDP_DATA message (starting at packet # 0 for every new file) once REQUEST_ACK message is received
  - Checksum = (1st 4 file bytes) OR (2nd 4 file bytes)
  - Case: Checksum fails or packet isn't received
    - Receiver keeps track of all packets that must be resent
    - Receiver sends a FILE_RECEIVED_ACK with data field 0x2 indicating that the file wasn't wholly received
    - Once the RESEND_ACK message is received, the receiver sends a RESEND_REQUEST message for every missing packet
    - Repeat as necessary until every packet is correctly received
  - Prompt resets for next SEND or TEARDOWN command once FILE_RECEIVED_ACK is received with data field 0x1
- Case: TEARDOWN command
  - Sender sends TEARDOWN_REQUEST message
  - Receiver sends TEARDOWN_ACK then closes the sockets and the application
  - Sender receives TEARDOWN_ACK and closes its sockets and the application
