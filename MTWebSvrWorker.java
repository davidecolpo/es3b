/*  ESEMPIO

  -----------------------------------------------  
  Esempio di messaggio HTTP request
  -----------------------------------------------  
  GET /ciao.html HTTP/1.1
  Accept: *
  Accept-Encoding: gzip, deflate
  Connection: keep-alive
  Host: 10.10.5.105
  User-Agent: HTTPie/1.0.3

  -----------------------------------------------  
  Esempio di messaggio HTTP response 
  -----------------------------------------------    
  HTTP/1.1 200 OK
  Access-Control-Allow-Credentials: true
  Cache-Control: no-cache
  Connection: keep-alive
  Content-Length: 117
  Content-Type: application/json; charset=utf-8
  Date: Sun, 08 Nov 2020 16:54:22 GMT
  ETag: W/"75-AeomHGviEEnj4heuOEgAmd0Rmwo"
  Expires: -1
  Pragma: no-cache
  Vary: Origin, Accept-Encoding
  X-Content-Type-Options: nosniff
  X-Powered-By: Express

  <html>
    <body>
      <p>ciao Modo</p>
     </body>
  </html>
  
 */


package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

class MTWebSvrWorker extends Thread{
	Socket clientSock;
	int clientNo;
	DataInputStream inStream;
    DataOutputStream outStream;
	

	MTWebSvrWorker( String threadName , Socket socketToClient, int ClientNo ) {
		super(threadName);
		clientSock = socketToClient;
		clientNo = ClientNo;
	}

	 
	
	
	public void run() {
	
	    // variabili relative alla HTTP Request
		String[] headerLines;   // L'Header della rischiesta HTTP
         
		String method;       // GET method
        String path ;        // index.html
        String httpVersion;  // HTTP version
        
        // variabili relative alla HTTP response
        String[] startLine;
        String respHeader_contentType="text/html; charset=UTF-8";
        String respHeader_extra="Server: Microsoft-IIS/8.5\r\n";  
        String respBody;
		
		try {

			InetSocketAddress clienAddr = (InetSocketAddress) clientSock.getRemoteSocketAddress();
	        System.out.println("ServerTread: " + this.getName() + " New connection from port=" + clienAddr.getPort() + " host=" + clienAddr.getHostName());
	        
			// Streams to read and write the data to socket streams
			inStream   = new DataInputStream(clientSock.getInputStream());   // andrebbe nel costruttore FIXME
			outStream = new DataOutputStream(clientSock.getOutputStream());  // andrebbe nel costruttore FIXME

			Scanner scan = new Scanner(inStream, "UTF-8");
			
			while (true) {
		      // separating the HTTP request header from rest of the HTTP message
              // "\\r\\n\\r\\n" below refers to the 'empty line' from fig.1
              String requestHeader = scan.useDelimiter("\\r\\n\\r\\n").next();
              System.out.println("---- Client Request ----");
              System.out.println("---- Header");
              System.out.println(requestHeader);
            
              // decomposing request header to understand client request message
              headerLines = requestHeader.split("\r\n");
              startLine = headerLines[0].split(" ");
            
			  method = startLine[0];  // GET method
              path = startLine[1];    // index.html
              httpVersion = startLine[2]; // HTTP version
              System.out.println("---- request line");
              System.out.println("  method: " + method);
              System.out.println("  path: " + path );
              System.out.println("  version. " + httpVersion);

              System.out.println("---- Server Response ----");
			  // elaboro la richiesta del client
              System.out.println("ServerTread: " + this.getName() + " Invio Messaggio al client " + clientNo );
              // respHeader_contentType="text/html; charset=UTF-8";
              // respHeader_extra="Server: Microsoft-IIS/8.5\r\n";    
              // calling helper method and setting standard HTTP status code, response type, and response content
            
              respBody="<html><body> <h3>Hello dal server " + this.getName() + "</h3></body></html>";
              sendResponse("200 OK", respHeader_contentType, respHeader_extra , respBody );
			}
		 

		} catch (NoSuchElementException ex) {
			//System.out.println(ex);
			System.out.println("ServerTread: " + this.getName() + " Client " + clientNo + " Disconnesso" );
		} catch (Exception ex) {
			System.out.println(ex);
			System.out.println("ServerTread: " + this.getName() + "errore generico" );
		} finally {
			try {
			    outStream.close();
			    inStream.close();
			    clientSock.close();
			 } catch (Exception ex)  {System.out.println(ex);}
		}
	}

	
	// helper method: creating response message for the client's request
    private void sendResponse(String status, String contentType, String additional_header , String payload ) throws IOException {  // FIXME
     

    	byte[] respBody = payload.getBytes("UTF-8");
    	
        byte[] respHeader = (
              "HTTP/1.1 " + status + "\r\n"
              + "Content-Type: " + contentType + "\r\n"
              + "Content-Length: " + respBody.length + "\r\n"
              + additional_header 
              + "\r\n").getBytes("UTF-8");
        
        outStream.write(respHeader, 0, respHeader.length);
        outStream.write(respBody, 0, respBody.length);
        outStream.write("\r\n\r\n".getBytes("UTF-8"));
        outStream.flush();
    }
	

}
