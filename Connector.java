package tcpConnection;

import java.util.*;

public class Connector {

	public int totalPackets;

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
		double ploss = sc.nextDouble();
		
		
		int packs =50;
		System.out.println("# of packets: " + packs);
		Packet[] packets = new Packet[packs+2];
		for (int i = 0; i < packets.length; i++) {
			packets[i] = new Packet();
			if(data - mss > 0) {
			packets[i].setData(mss);
			data -= mss;
			}else {
				packets[i].setData(data);
			}
		}
		threeWayHS(packets);
		packets[3].setSeqNum(packets[2].getackNum());
		packets[3].setAcknum((int) ((int) packets[2].getSeqNum() + packets[2].getData()));
		
		for (int i = 0; i < packets.length; i++) {
			Random rand = new Random();
			int low = 0;
			int high = 100;
			int result = rand.nextInt(high - low) + low;
			
			double lost = ploss * 100;
			
			System.out.println(
					"PACKET # " + i +" : ESTABLISHING CONNECTION: " + "SEQ: " + packets[i].getSeqNum() + " ACK:" + packets[i].getackNum() + " DATA: " + packets[i].getData());
			if(i > 2) {
				transferData(packets, i, result, lost, dupAckCounter);
			}
		}

	}

	public static void threeWayHS(Packet packet[]) {
		Random ran = new Random();
		
		Packet sPacket = new Packet();
		
		packet[0].setSeqNum(ran.nextInt(99999));
		// syn-ack; these are server packets, so may need to separate from array of
		// 'total packets'
		packet[1].setAcknum(packet[0].getSeqNum() + 1);
		packet[1].setSeqNum(0);
		// ack
		packet[2].setAcknum(packet[1].getSeqNum() + 1);
		packet[2].setSeqNum(packet[1].getackNum());
		System.out.println("Syn Ack: " + "SEQ: " + sPacket.getSeqNum() + " ACK: " + sPacket.getackNum() );
	}

	/**
	 * 
	 * @param packet
	 *            array of all packets
	 * @param i
	 *            index of packet from array
	 */
	public static void transferData(Packet packet[], int i, int random, double lost, int dupAckCounter) {
		// server packet, probably will have to update away from 'total packets'
		if (packet[i + 1] != null) {
			
			while(lost <= random) {
				System.out.println("Packet Lost!");
				dupAckCounter++;
				Random rand = new Random();
				int low = 0;
				int high = 100;
				random = rand.nextInt(high - low) + low;
				
				if(dupAckCounter == 3) {
					System.out.println("Congest Avoidance Starts Here --->");
				}
				//random = rand.nextInt(high - low) + low
			}
						
			// client variables
			int cSeq = packet[i].getSeqNum();
			int cAck = packet[i].getackNum();
			float cData = packet[i].getData();
			
			// set 'server packet' ack and seq
			packet[i+1].setSeqNum(cAck);
			packet[i+1].setAcknum((int) (cSeq + cData));
			
			// future client packet variables
			int cSeq2 = packet[i+2].getSeqNum();
			int cAck2 = packet[i+2].getackNum();
			
			// update what next client packet should look like
			packet[i+2].setSeqNum(packet[i+1].getackNum());
			packet[i+2].setAcknum(packet[i+1].getSeqNum());
			
//			if(lost <= random) {
//				System.out.println("Packet Lost!");
//			}
			
		} else {
			System.out.println("no more packets!");
			System.exit(0);
		}

	}

}