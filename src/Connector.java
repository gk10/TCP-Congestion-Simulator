import java.util.*;

public class Connector {

	public int totalPackets;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter amount of data (bytes)");
		int data = sc.nextInt();
		System.out.println("Enter latency (ms)");
		int time = sc.nextInt();
		System.out.println("Enter MSS (bytes)");
		int mss = sc.nextInt();
		
		int packs = data/mss;
		System.out.println("# of packets" + packs);
		Packet[] packets = new Packet[packs];
		for (int i = 0; i < packets.length; i++) {
			packets[i] = new Packet();
		}
		threeWayHS(packets);
		for (int i = 0; i < packets.length; i++) {

			System.out.println(
					"ESTABLISHING CONNECTION: " + "SEQ: " + packets[i].getSeqNum() + " ACK:" + packets[i].getackNum());
		}

	}

	public static void threeWayHS(Packet packet[]) {
		Random ran = new Random();
		packet[0].setSeqNum(ran.nextInt(99999));
		// syn-ack; these are server packets, so may need to separate from array of
		// 'total packets'
		packet[1].setAcknum(packet[0].getSeqNum() + 1);
		packet[1].setSeqNum(0);
		// ack
		packet[2].setAcknum(packet[1].getSeqNum() + 1);
		packet[2].setSeqNum(packet[1].getackNum() + 1);
	}

	/**
	 * 
	 * @param packet
	 *            array of all packets
	 * @param i
	 *            index of packet from array
	 */
	public static void transferData(Packet packet[], int i) {
		// server packet, probably will have to update away from 'total packets'
		if (packet[i + 1] != null) {
			int temppackackNum = packet[i + 1].getackNum();
			packet[i + 1].setAcknum((int) (temppackackNum + packet[i].getData()));
		} else {
			System.out.println("no more packets!");
			System.exit(0);
		}

	}

}
