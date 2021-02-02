package refactoring_java.calculator;

/**
 * 공연ID를 키로한 고객의 공연 정보
 * @author 
 */
public class Performance {
	private String playID;
	private int audience;
	/* 계산된 데이터 : 공연당 가격과, 포인트를 가지고 있을 수 있게 클래스를 확장 */
	private int volumeCredit;
	private int amount;
	/* play : playID에 해당하는 공연정보 class */
	private Play play;
	
	public Performance() {
	}
	public Performance(String playID, int audience) {
		super();
		this.playID = playID;
		this.audience = audience;
	}
	public Performance(String playID, int audience, int volumeCredit, int amount, Play play) {
		super();
		this.playID = playID;
		this.audience = audience;
		this.volumeCredit = volumeCredit;
		this.amount = amount;
		this.play = play;
	}
	
	public String getPlayID() {
		return playID;
	}
	public void setPlayID(String playID) {
		this.playID = playID;
	}
	public int getAudience() {
		return audience;
	}
	public void setAudience(int audience) {
		this.audience = audience;
	}
	
	/* 계산된 데이터를 가지고 있을 수 있게 클레스를 확장 */
	public int getVolumeCredit() {
		return volumeCredit;
	}
	public void setVolumeCredit(int volumeCredit) {
		this.volumeCredit = volumeCredit;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/* Play */
	public Play getPlay() {
		return play;
	}
	public void setPlay(Play play) {
		this.play = play;
	}
	
	@Override
	public String toString() {
		return "Performance [playID=" + playID + ", audience=" + audience + ", volumeCredit=" + volumeCredit
				+ ", amount=" + amount + ", play=" + play + "]";
	}
}
