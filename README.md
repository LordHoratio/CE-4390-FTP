# CE-4390-FTP
Custom file transfer protocol for class project

Use Case:
  1. User runs application and command prompt opens
  2. User types CONNECT and prompt will then ask for an IP address and the desired transport protocol
  3. Program confirms whether or not connection with receiver was sucessful
  4. User sends file with SEND command followed by the name of the file being sent, which must be in same folder as application
  5. Program sends file with desired transport protocol and confirms when the transfer is complete. File should appear in same folder
     as the receiving application
  6. User can use the SEND command after the program confirms the successful transfer of last file. TEARDOWN command will close all
     connections and terminate program.
     
FTP Messages:
Every message starts with a 1 byte flag indicating what it is, followed by a variety of different fields depending on message type.
0. TCP_CONNECT_REQUEST - [Flag (0x00)][1 byte]

1. UDP_CONNECT_REQUEST - [Flag (0x01)][1 byte]

2. CONNECTION_ACK - [Flag (0x02)][1 byte]

3. SEND_REQUEST - [Flag (0x03)][1 byte]

4. REQUEST_ACK - [Flag (0x04)][1 byte]

5. DATA_TCP - [Flag (0x05)][1 byte]

6. DATA_UDP - [Flag (0x06)][1 byte]

7. FILE_RECIEVED_ACK - [Flag (0x07)][1 byte]

8. RESEND_REQUEST - [Flag (0x08)][1 byte]

9. TEARDOWN_REQUEST - [Flag (0x09)][1 byte]

10. TTEARDOWN_ACK - [Flag (0x0A)][1 byte]

INCOMPLETE - WILL FINISH AFTER DINNER
