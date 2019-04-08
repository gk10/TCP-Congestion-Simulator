public class Packet {
	private int seqNum=0;
	private int acknum=0;
	private float data=0;
	
	public int getSeqNum(){
		return seqNum;
	}
	
	public int getackNum(){
		return acknum;
	}
	
	public float getData(){
		return data;
	}
	
	public void setSeqNum(int num) {
		seqNum = num;
	}
	
	public void setAcknum(int num) {
		acknum = num;
	}
	
	public void setData(float num) {
		data = num;
	}
}