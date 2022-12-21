 
package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

//WebClient represents single web client
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html
public class HttpClient {

	public static void main(String[] args) {
		final String CRLF = "\r\n"; //carriage return line feed
		final String SP = " ";      //status line parts separator
		
		String severAddress="127.0.0.1";  // localhost
		int severPort=8698;
		 
		//initialize filePath with default file /
		String filePath;
		filePath=prompt("[CLIENT] Inserisci l'URI della pagina da richiedere (ex. /index.html) >");
	
		System.out.println("[CLIENT] Using Server Port: " + severPort);
		System.out.println("[CLIENT] Using FilePath: " + filePath);
	 
		//define a socket
		Socket socket = null;
		
		//define input and output streams
		BufferedReader socketInStream = null; //reads data received over the socket's inputStream
		DataOutputStream socketOutStream = null; //writes data over the socket's outputStream
		
		
		try {
			
			//get inet address of the serverHost
			InetAddress serverInet = InetAddress.getByName(severAddress);
			
			//try to connect to the server
			socket = new Socket(serverInet, severPort);
			System.out.println("[CLIENT] Connected to the server at " + severAddress + ":" + severPort);
			
			//get a reference to socket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//get a reference to socket's outputStream
			socketOutStream = new DataOutputStream(socket.getOutputStream());

			//now send a HTTP GET request
			String requestLine = "GET" + SP + filePath + SP +"HTTP/1.0" + CRLF;
			System.out.println("[CLIENT] Sending HTTP GET request: " + requestLine);
			
			//send the requestLine
			socketOutStream.writeBytes(requestLine);
			
			//send an empty line
			socketOutStream.writeBytes(CRLF);
			
			//flush out output stream
			socketOutStream.flush();
			
			System.out.println("[CLIENT] Waiting for a response from the server");
			//extract response Code
			String responseLine = socketInStream.readLine();
			System.out.println("[CLIENT] Received HTTP Response with status line: " + responseLine);

			//extract content-type of the response
			String contentType = socketInStream.readLine();
			System.out.println("[CLIENT] Received " + contentType);

			//read a blank line i.e. CRLF
			socketInStream.readLine();

			System.out.println("[CLIENT] Received Response Body:");
			//start reading content body
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
				//save content to a buffer
				content.append(res + "\n");
				
				//print it as well
				System.out.println(res);
			}
			
			
			System.out.println("[CLIENT] HTTP Response received.");

		} catch (Exception e) {
			System.err.println("[CLIENT] ERROR " + e);
		}
		finally {
			try {
				//close all resources
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				
				if (socket != null) {
					socket.close();
					System.out.println("[CLIENT] Closing the Connection.");
				}
			} catch (IOException e) {
				System.err.println("[CLIENT] EXCEPTION in closing resource." + e);
			}
		}
	}
	

	public static String prompt(String msg) {
		System.out.print(msg);
		Scanner scanner = new Scanner(System.in);
		String uri = scanner.nextLine();
		return uri.trim();
	}
}
