package tcpConnection;

public class clientPacket {
	private int startSeqNum;
	private int ACK;
	private int data;
	
	clientPacket() {
		this.startSeqNum = 79;
		this.ACK = 0;
	}
	
	public int getSeqNum() {
		return this.startSeqNum;
	} 
	
	public int getACK() {
		return this.ACK;
	}
	
	public int getData() {
		return this.data;
	}
	
	public void setSeqNum(int seqNum) {
		this.startSeqNum = seqNum;
	}
	
	public void setACK(int ACK) {
		this.ACK = ACK;
	}
	
	public String toString() {
		return "seqNUM = " + this.startSeqNum + " ACK = " + this.ACK;
	}

}
