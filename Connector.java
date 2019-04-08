package tcpConnection;

import java.util.*;

public class Connector {

	public int totalPackets;

	public static int ssthresh = 32;

	public static boolean slowStart = true;

	public static ArrayList<Packet> sPackets = new ArrayList<Packet>();
	public static ArrayList<Packet> cPackets = new ArrayList<Packet>();

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int dupAckCounter = 0;

		System.out.println("Enter amount of data (bytes)");
		int data = sc.nextInt();
		System.out.println("Enter latency (ms)");
		int time = sc.nextInt();
		System.out.println("Enter MSS (bytes)");
		int mss = sc.nextInt();
		System.out.println("Enter probability of packet loss:");
		float ploss = sc.nextFloat();

		threeWayHS();
		toServer(cPackets.get(cPackets.size() - 1));
		toClient();
		toServer(cPackets.get(cPackets.size() - 1));
		toClient();
		toServer(cPackets.get(cPackets.size() - 1));
		toClient();
		try {
			for (int i = 0; i < cPackets.size(); i++) {
				clientDuplicateAck(ploss, i);
				serverDuplicateAck(ploss, i);
				
			}
		} catch (Exception e) {
			System.out.println("No more packets!");
		}
	}

	public static void threeWayHS() {
		Random ran = new Random();
		Packet sPacket = new Packet();
		Packet c1 = new Packet();
		c1.setSeqNum(ran.nextInt(99999));
		cPackets.add(c1);
		Packet s1 = new Packet();
		// syn-ack; these are server packets, so may need to separate from array of
		// 'total packets'
		s1.setAcknum(cPackets.get(cPackets.size() - 1).getSeqNum() + 1);
		s1.setSeqNum(0);
		sPackets.add(s1);
		// ack
		Packet c2 = new Packet();
		c2.setAcknum(s1.getSeqNum() + 1);
		c2.setSeqNum(s1.getackNum());
		cPackets.add(c2);
	}

	/**
	 * 
	 * @param packet
	 *            array of all packets
	 * @param i
	 *            index of packet from array
	 */
	public static void toServer(Packet packet) {
		
		Packet sPacket = new Packet();
		sPacket.setSeqNum(packet.getackNum());
		sPacket.setAcknum((int) (packet.getSeqNum() + packet.getData()));
		sPackets.add(sPacket);
	}

	/**
	 * Send the latest packet from the server back to the client
	 */
	public static void toClient() {
		Packet sPacket = sPackets.get(sPackets.size() - 1);
		Packet cPacket = new Packet();
		cPacket.setAcknum(sPacket.getSeqNum());
		cPacket.setSeqNum(sPacket.getackNum());
		cPacket.setData(10);
		cPackets.add(cPacket);
	}
	
	public static void clientDuplicateAck(double ploss, int i) {
		int display = 0;
		int dupAckCounter = 0;
		Random rand = new Random();
		int low = 0;
		int high = 100;
		int result = rand.nextInt(high - low) + low;
		
		while(result <= ploss * 100) {
			//System.out.println("Packet Lost");
			System.out.println("CLIENT - SEQ: " + cPackets.get(i).getSeqNum() + " ACK " + cPackets.get(i).getackNum());
			result = rand.nextInt(high - low) + low;
			dupAckCounter++;
			display++;
			
			if(dupAckCounter == 3) {
				System.out.println("Begin Congestion Avoidance");
				break;
			}
		}
		
		//dupAckCounter = 0;
		
		if(result > ploss && display != 2) {
		System.out.println(
				"CLIENT - SEQ: " + cPackets.get(i).getSeqNum() + " ACK " + cPackets.get(i).getackNum());
		}
	}
	
	public static void serverDuplicateAck(double ploss, int i) {
		Random rand = new Random();
		int dupAckCounter = 0;
		int display = 0;
		int low = 0;
		int high = 100;
		int result = rand.nextInt(high - low) + low;
		
		while(result <= ploss * 100) {
			//System.out.println("Packet Lost");
			System.out.println("SERVER - SEQ: " + sPackets.get(i).getSeqNum() + " ACK " + sPackets.get(i).getackNum());
			result = rand.nextInt(high - low) + low;
			dupAckCounter++;
			display++;
			
			if(dupAckCounter == 3) {
				System.out.println("Begin Congestion Avoidance");
				break;
			}
		}
		
		if(result > ploss && display != 2) {
		
		System.out.println(
				"SERVER - SEQ: " + sPackets.get(i).getSeqNum() + " ACK " + sPackets.get(i).getackNum());
	}
	}
	

}