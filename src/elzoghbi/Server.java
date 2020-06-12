package elzoghbi;

import java.net.*;
import java.io.*;

public class Server {

	public static void main(String[] args) throws IOException {

		ServerSocket ss = new ServerSocket(1234);

		while (true) {
			Socket s = null;

			try {
				s = ss.accept();

				System.out.println("A new client is connected : " + s);

				System.out.println("Assigning new thread for this client");

				Thread t = new ClientHandler(s);

				t.start();

			} catch (Exception e) {
				s.close();
				ss.close();
				e.printStackTrace();
				// TODO: handle exception
			}
		}

	}
}

class ClientHandler extends Thread {

	final Socket s;

	public ClientHandler(Socket s) {
		this.s = s;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(1234);
			DatagramSocket rootSocket = new DatagramSocket();
			//	DatagramSocket tldSocket = new DatagramSocket();

			// int c=5;
			while (true) // instead of c i want to use true
			{
				byte[] receivebuffer = new byte[1024];
				byte[] sendbuffer = new byte[1024];
				DatagramPacket recvdpkt = new DatagramPacket(receivebuffer, receivebuffer.length);
				serverSocket.receive(recvdpkt);
				String clientdata  = new String (receivebuffer , 0 , receivebuffer.length);
				
				InetAddress IP = recvdpkt.getAddress();
				int portno = recvdpkt.getPort();

				// Kda Ana Gebt El-Client Data
				/*String clientdata = new String(recvdpkt.getData());*/
				clientdata = clientdata.trim();
				/*sendbuffer = clientdata.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, IP, portno);
				serverSocket.send(sendPacket);
				*/
				System.out.println("Server Received : "+ clientdata);

				if (clientdata.equals("quit")) {
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection.");
					this.s.close();
					System.out.println("Connection closed");
					break;
				}
				
				String toSend = getLocal(clientdata); 
				if (toSend.equals("notFound")) {
					
					byte[] rootSendbuffer = new byte[1024];
				    byte[] rootReceivebuffer = new byte[1024];
				    
				    rootSendbuffer = clientdata.getBytes();
				    
				    InetAddress ipAdd = InetAddress.getByName("localhost");
				    DatagramPacket DP = new DatagramPacket(rootSendbuffer, rootSendbuffer.length , ipAdd , 5678);
				    
				    rootSocket.send(DP);
				    
				    DatagramPacket rec = new DatagramPacket(rootReceivebuffer, rootReceivebuffer.length);
				    //System.out.println("Ana Hena 5");
				    rootSocket.receive(rec);
				    //System.out.println("Ana Hena 7");
				    String Data = new String(rootReceivebuffer , 0 , rootReceivebuffer.length);
				    //System.out.println("Ana Hena 8");
				    //System.out.println(Data);
				    
				    System.out.println(Data);
				    
				    sendbuffer = Data.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, IP, portno);
					serverSocket.send(sendPacket);
					
					
				}else if (!toSend.equals("notFound")) {
					sendbuffer = toSend.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, IP, portno);
					serverSocket.send(sendPacket);
				}

			}
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public String getLocal (String receive) {
		String toSend = "notFound";
		RandomAccessFile local;
		try {
			local = new RandomAccessFile("local_dns_table.txt", "r");
			
			for(int i = 0 ; i < 2 ; i++) {
				String line = local.readLine();
				String[] split = (line.split(" "));
				
				if (line.equals(null)) {
					break;
				}else if (split[0].equals(receive)){
					toSend = "Reply from Server is : URL=" + split[0] + "\n" 
								+ "IP Address=" + split[1] +"\n"+
								"Quary Type = A,NS" + "\n" + "Server name = local DNS \n";
					
					System.out.println("Client Requested : " + split[0]+ "\n" +"URL=" + split[0] + "\n" 
								+ "IP Address=" + split[1] +"\n"+
								"Quary Type = A,NS" + "\n");
					local.close();
					break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toSend;
	}

}
