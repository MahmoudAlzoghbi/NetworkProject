package elzoghbi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class TLDServer {

	public static void main(String[] args) {

		try {
			DatagramSocket rootSocket = new DatagramSocket(3333);
			DatagramSocket authSocket = new DatagramSocket();

			while (true) {
				byte[] receivebuffer = new byte[1024];
				byte[] sendbuffer = new byte[1024];
				DatagramPacket recvdpkt = new DatagramPacket(receivebuffer, receivebuffer.length);
				rootSocket.receive(recvdpkt);
				String clientdata = new String(receivebuffer, 0, receivebuffer.length);
				clientdata = clientdata.trim();
				System.out.println(clientdata);

				List<String> toSend = new ArrayList<>();
				toSend = getTLD(clientdata);
				System.out.println(toSend.get(0));
				if (!toSend.get(0).equals("notFound")) {
					//System.out.println("Ana Hena");
					String Data = toSend.get(0);
					//System.out.println(Data);

					sendbuffer = Data.getBytes();

					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
							recvdpkt.getPort());

					rootSocket.send(sendPacket);
					//System.out.println(Data);
				}else if (toSend.get(0).equals("notFound")) {
					byte[] authSendbuffer = new byte[1024];
					byte[] authReceivebuffer = new byte[1024];

					authSendbuffer = clientdata.getBytes();

					InetAddress ipAdd = InetAddress.getByName("localhost");
					DatagramPacket DP = new DatagramPacket(authSendbuffer, authSendbuffer.length, ipAdd, 4444);

					authSocket.send(DP);

					DatagramPacket rec = new DatagramPacket(authReceivebuffer, authReceivebuffer.length);
					// System.out.println("Ana Hena 5");
					authSocket.receive(rec);
					// System.out.println("Ana Hena 7");
					String Data = new String(authReceivebuffer, 0, authReceivebuffer.length);
					// System.out.println("Ana Hena 8");
					// System.out.println(Data);

					System.out.println(Data);

					sendbuffer = Data.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
							recvdpkt.getPort());
					rootSocket.send(sendPacket);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public static List<String> getTLD(String receive) throws IOException {
		List<String> toSend = new ArrayList<>();
		toSend.add("notFound");
		RandomAccessFile tld = new RandomAccessFile("TLD_dns_table.txt", "r");
		for (int i = 0 ; i < 4 ; i++) {
			String line = tld.readLine();
			String[] split = (line.split(" "));
			
			if (line.equals(null)) {
				break;
			}else if (split[0].equals(receive) && split.length ==3){
				toSend.clear();
				toSend.add("Reply from Server is : URL=" + split[0] + "\n" 
							+ "IP Address=" + split[1] +"\n"+
							"Quary Type = A,CNAME" + "\n" + "Server name = TLD DNS \n"
							+ "Canonical name:" + split[2] + "\n" 
							+ "Aliases:" + split[0]);
				
				toSend.add("Client Requested : " + split[0]+ "\n" +"URL=" + split[0] + "\n" 
							+ "IP Address=" + split[1] +"\n"+
							"Quary Type = A,CNAME" + "\n"
							+ "Canonical name:" + split[2] + "\n" 
							+ "Aliases:" + split[0]);
				
				break;
			}
		}
		tld.close();
		return toSend;
	}

}
