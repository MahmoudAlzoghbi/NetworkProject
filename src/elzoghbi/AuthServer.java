package elzoghbi;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class AuthServer {

	public static void main(String[] args) {

		try {
			DatagramSocket tldSocket = new DatagramSocket(4444);
			//DatagramSocket authSocket = new DatagramSocket();

			while (true) {
				byte[] receivebuffer = new byte[1024];
				byte[] sendbuffer = new byte[1024];
				DatagramPacket recvdpkt = new DatagramPacket(receivebuffer, receivebuffer.length);
				tldSocket.receive(recvdpkt);
				String clientdata = new String(receivebuffer, 0, receivebuffer.length);
				clientdata = clientdata.trim();
				System.out.println(clientdata);

				List<String> toSend = new ArrayList<>();
				toSend = getAuth(clientdata);

				/* if (!toSend.get(0).equals("notFound")) { */
				String Data = toSend.get(0);
				// System.out.println(Data);

				sendbuffer = Data.getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
						recvdpkt.getPort());

				tldSocket.send(sendPacket);
				/*
				 * }else if (toSend.get(0).equals("notFound")) { byte[] rootSendbuffer = new
				 * byte[1024]; byte[] rootReceivebuffer = new byte[1024];
				 * 
				 * rootSendbuffer = clientdata.getBytes();
				 * 
				 * InetAddress ipAdd = InetAddress.getByName("localhost"); DatagramPacket DP =
				 * new DatagramPacket(rootSendbuffer, rootSendbuffer.length, ipAdd, 4444);
				 * 
				 * authSocket.send(DP);
				 * 
				 * DatagramPacket rec = new DatagramPacket(rootReceivebuffer,
				 * rootReceivebuffer.length); // System.out.println("Ana Hena 5");
				 * authSocket.receive(rec); // System.out.println("Ana Hena 7"); String Data =
				 * new String(rootReceivebuffer, 0, rootReceivebuffer.length); //
				 * System.out.println("Ana Hena 8"); // System.out.println(Data);
				 * 
				 * System.out.println(Data);
				 * 
				 * sendbuffer = Data.getBytes(); DatagramPacket sendPacket = new
				 * DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
				 * recvdpkt.getPort()); serverSocket.send(sendPacket); }
				 */
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static List<String> getAuth(String receive) throws IOException {
		List<String> toSend = new ArrayList<>();
		toSend.add("notFound");
		RandomAccessFile auth = new RandomAccessFile("authoritative_dns_table.txt", "r");
		for (int i = 0; i < 3; i++) {
			String line = auth.readLine();
			String[] split = (line.split(" "));

			if (line.equals(null)) {
				break;
			} else if (split[0].equals(receive)) {
				toSend.clear();
				toSend.add("Reply from Server is : URL=" + split[0] + "\n" + "IP Address=" + split[1] + "\n"
						+ "Quary Type = A,NS" + "\n" + "Server name = authoritative DNS \n" + "authoritative answer:\n"
						+ "Name: authoritative_dns_table.txt");

				System.out.println("Client Requested : " + split[0] + "\n" + "URL=" + split[0] + "\n" + "IP Address="
						+ split[1] + "\n" + "Quary Type = A,NS" + "\n" + "Found Record on authoritative DNS servers.");
				break;
			}
		}
		auth.close();
		return toSend;
	}

}
