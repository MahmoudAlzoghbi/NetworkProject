package elzoghbi;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class RootServer {

	public static void main(String[] args) {

		try {
			DatagramSocket serverSocket = new DatagramSocket(5678);
			DatagramSocket tldSocket = new DatagramSocket();

			while (true) {
				byte[] receivebuffer = new byte[1024];
				byte[] sendbuffer = new byte[1024];
				DatagramPacket recvdpkt = new DatagramPacket(receivebuffer, receivebuffer.length);
				serverSocket.receive(recvdpkt);
				String clientdata = new String(receivebuffer, 0, receivebuffer.length);
				clientdata = clientdata.trim();
				System.out.println(clientdata);

				List<String> toSend = new ArrayList<>();
				toSend = getRoot(clientdata);
				System.out.println(toSend.get(0));
				if (!toSend.get(0).equals("notFound")) {
					System.out.println("Ana Hena");
					String Data = toSend.get(0);
					System.out.println(Data);

					sendbuffer = Data.getBytes();

					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
							recvdpkt.getPort());

					serverSocket.send(sendPacket);
					System.out.println(Data);
				} else if (toSend.get(0).equals("notFound")) {
					byte[] rootSendbuffer = new byte[1024];
					byte[] rootReceivebuffer = new byte[1024];

					rootSendbuffer = clientdata.getBytes();

					InetAddress ipAdd = InetAddress.getByName("localhost");
					DatagramPacket DP = new DatagramPacket(rootSendbuffer, rootSendbuffer.length, ipAdd, 3333);

					tldSocket.send(DP);

					DatagramPacket rec = new DatagramPacket(rootReceivebuffer, rootReceivebuffer.length);
					// System.out.println("Ana Hena 5");
					tldSocket.receive(rec);
					// System.out.println("Ana Hena 7");
					String Data = new String(rootReceivebuffer, 0, rootReceivebuffer.length);
					// System.out.println("Ana Hena 8");
					// System.out.println(Data);

					System.out.println(Data);
					

					sendbuffer = Data.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length, recvdpkt.getAddress(),
							recvdpkt.getPort());
					serverSocket.send(sendPacket);
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getRoot(String receive) throws IOException {
		List<String> toSend = new ArrayList<>();
		toSend.add("notFound");
		RandomAccessFile root = new RandomAccessFile("root_dns_table.txt", "r");
		for (int i = 0; i < 3; i++) {
			String line = root.readLine();
			String[] split = (line.split(" "));

			if (line.equals(null)) {
				break;
			} else if (split[0].equals(receive)) {
				toSend.clear();
				toSend.add("Reply from Server is : URL=" + split[0] + "\n" + "IP Address=" + split[1] + "\n"
						+ "Quary Type = A,NS" + "\n" + "Server name = Root DNS \n");

				toSend.add("Client Requested : " + split[0] + "\n" + "URL=" + split[0] + "\n" + "IP Address=" + split[1]
						+ "\n" + "Quary Type = A,NS" + "\n");
				break;
			}
		}
		root.close();
		return toSend;
	}

}
