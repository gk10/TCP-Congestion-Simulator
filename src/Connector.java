package tcpConnection;

import java.util.*;

public class Connector {

	public int totalPackets;

	public static float ssthresh = 32;
	public static float cwnd;
	public static int nLost = 0;
	public static boolean slowStart = true;

	public static ArrayList<Packet> sPackets = new ArrayList<Packet>();
	public static ArrayList<Packet> cPackets = new ArrayList<Packet>();
	public static ArrayList<Float> times = new ArrayList<Float>();
	public static float pubData;
	public static float pubMSS;
	public static float pubProb;
	public static double pubTime;
	public static float temp;
	
	public static float totalLost = 0;
	public static int maxCwnd = 50;

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int dupAckCounter = 0;

		System.out.println("Enter amount of data (bytes)");
		int data = sc.nextInt();
		pubData = data;
		System.out.println("Enter average time between messages (ms)");
		int time = sc.nextInt();
		temp = time;
		System.out.println("Enter MSS (bytes)");
		int mss = sc.nextInt();
		pubMSS = mss;
		System.out.println("Enter probability of packet loss:");
		float ploss = sc.nextFloat();
		pubProb = ploss;

		// initiate connection
		threeWayHS();

		// start slow start
		sStart();
		// congestionAvoidance();

		// the final packet
		toServer(cPackets.get(cPackets.size() - 1), pubProb);
		toClient(pubData);

		System.out.println("DATA LEFT " + pubData);
		try {
			for (int i = 0; i < cPackets.size(); i++) {
				System.out.println(
						"CLIENT - SEQ: " + cPackets.get(i).getSeqNum() + " ACK " + cPackets.get(i).getackNum());
				System.out.println(
						"SERVER - SEQ: " + sPackets.get(i).getSeqNum() + " ACK " + sPackets.get(i).getackNum());
			}
		} catch (Exception e) {
			System.out.println("No more packets!");
			System.out.println("CWND: " + cwnd);
		}
	}
	
	//approximate time interval based off of assumptions 
	public static double timeoutInterval(double userRTT) {
		double[] samples = new double[4];
		
		double alpha = .125;
		double alpha2 = .875;
		
		samples[0] = userRTT * alpha2;
		samples[0] = samples[0] + (alpha * userRTT);
		
		double beta = .25;
		double beta2 = .75;
		double devRTT = 5;
		
		samples[1] = beta2 * devRTT;
		samples[1] = samples[1] + (.25 * (userRTT - samples[0]));
		
		samples[2] = 4 * samples[1];
		samples[2] = samples[2] + samples[0];
		
		double timeoutInterval = samples[2];	
		return timeoutInterval;
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
	 *            Packet you would like to send to server
	 */
	public static void toServer(Packet packet, float pSuccess) {
		System.out.println("num lost: " + nLost);
		// send successful packet back if not lost and reset lost counter if works
		if (isLost(pSuccess) == false && nLost < 3) {
			Packet sPacket = new Packet();
			sPacket.setSeqNum(packet.getackNum());
			sPacket.setAcknum((int) (packet.getSeqNum() + packet.getData()));
			sPackets.add(sPacket);
			nLost = 0;
			System.out.println("RESET");
			// server will send back dupe acks 3 times
		} else if (nLost <= 3 && nLost > 0) {
			System.out.println("dupe ack");
			sPackets.add(sPackets.get(sPackets.size() - 1));

		}
	}

	/**
	 * Send the latest packet from the server back to the client
	 */
	public static void toClient(float pubData2) {

		Packet sPacket = sPackets.get(sPackets.size() - 1);
		Packet cPacket = new Packet();
		cPacket.setData(pubData2);
		cPacket.setAcknum(sPacket.getSeqNum());
		cPacket.setSeqNum(sPacket.getackNum());
		cPackets.add(cPacket);

	}

	/**
	 * Decide if a packet will be lost
	 * 
	 * @param prob
	 *            chance packet will be lost
	 * @return if true, packet is lost; else packet isn't lost
	 */
	public static boolean isLost(float prob) {
		Random ran = new Random();
		int random = ran.nextInt(99) + 1;
		prob = prob * 100;
		System.out.println("PROB: " + prob + " RANDOM: " + random);
		if (prob > random) {
			nLost++;
			totalLost++;
			pubTime += timeoutInterval(pubTime);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Slow Start phase i is acting as cwnd should double after each RTT, so time
	 * should prob go here
	 */
	public static void sStart() {
		int i = 1;
		while (i < ssthresh && slowStart == true && nLost < 3 && pubData > pubMSS) {
			System.out.println("I" + i);
			int itemp = i * 2;
			for (int j = i; j < itemp; j++) {
				if (pubData - pubMSS > 0) {
					toServer(cPackets.get(cPackets.size() - 1), pubProb);
					toClient(pubMSS);
					pubData = pubData - pubMSS;
					pubTime += temp;
					// send second to last packet
				} else {
					toServer(cPackets.get(cPackets.size() - 1), pubProb);
					toClient(pubData);
					pubTime += temp;
					break;
				}
			}
			i = itemp;
		}
		cwnd = i;
		if (nLost >= 3) {
			timeOut();
		}
		
	}
	// if (cwnd > ssthresh || nLost >= 3) {
	// ssthresh = cwnd / 2;
	// slowStart = false;
	// cwnd = 1;
	// }

	public static void timeOut() {
		ssthresh = cwnd / 2;
		slowStart = true;
		cwnd = 1;
		nLost = 0;
	}

	public static void congestionAvoidance() {

		if (cwnd < ssthresh) {
			sStart();
		} else {

		}
		// cwnd = (cwnd + 1) / cwnd;
		cwnd++;
	}
}