package refactoring_java.calculator;

import java.util.List;
/**
 * 고객이 요청한 공연목록
 * @author 
 */
public class Invoice {
	private String customer;
	private List<Performance> performances;
	/* 계산된 데이터를 가지고 있을 수 있게 클레스를 확장 */
	private int totalAmount;
	private int totalVolumeCredits;
	
	public Invoice() {
	}
	public Invoice(String customer, List<Performance> performances) {
		super();
		this.customer = customer;
		this.performances = performances;
	}
	public Invoice(String customer, List<Performance> performances, int totalAmount, int totalVolumeCredits) {
		super();
		this.customer = customer;
		this.performances = performances;
		this.totalAmount = totalAmount;
		this.totalVolumeCredits = totalVolumeCredits;
	}
	
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public List<Performance> getPerformances() {
		return performances;
	}
	public void setPerformances(List<Performance> performances) {
		this.performances = performances;
	}
	/* 계산된 데이터를 가지고 있을 수 있게 클레스를 확장 */
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getTotalVolumeCredits() {
		return totalVolumeCredits;
	}
	public void setTotalVolumeCredits(int totalVolumeCredits) {
		this.totalVolumeCredits = totalVolumeCredits;
	}
	
	/* 새로운 객체 리턴 : 속성이 class인 경우 그 객체까지 deep copy하지않는다. */ 
//	public Invoice clone() {
//		return new Invoice(this.customer, this.performances, this.totalAmount, this.totalVolumeCredits);
//	}
	
	@Override
	public String toString() {
		return "Invoice [customer=" + customer + ", performances=" + performances + ", totalAmount=" + totalAmount
				+ ", totalVolumeCredits=" + totalVolumeCredits + "]";
	}
}
