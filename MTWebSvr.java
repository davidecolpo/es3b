package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MTWebSvr {
		
	public static void main(String[] args) throws Exception {
	   
	  int severPort=8698;
	  int clienCount = 0;       // conta il numero di client
	
		
		
	  // Listen to port
      ServerSocket ssock = new ServerSocket(8698);
      System.out.println("Server started on http://127.0.0.1:" + severPort );
     
      while (true) {
			
        // Start accepting requests and wait until client connects
	    Socket serverClientSocket = ssock.accept();  // bloccante
        clienCount++;
        System.out.println("Server: Serving Client " + clienCount);
        // Handle the client communication
        MTWebSvrWorker sa = new MTWebSvrWorker( "Thread-Numero-" +clienCount ,  serverClientSocket, clienCount);
        //sa.setName("Thread-Numero-" +count);  
        sa.start();  // non è bloccante
		}
	}
}
