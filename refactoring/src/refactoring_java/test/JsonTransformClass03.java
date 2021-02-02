package refactoring_java.test;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import refactoring_java.calculator.Invoice;
import refactoring_java.calculator.Performance;
import refactoring_java.calculator.Play;
import refactoring_java.json.GsonHandler;


/**
 * <PRE>
 * @Title <b>리펙터링 2판 초기 예제. 공연비 계산기: JavaScript코드 Java로 변경 </b></BR>
 * step3. 클래스를 확장하여 공연비 정보를 효율적으로 관리하게 만들기
 * 1. 클래스 확장: 클래스 Invoice, Performance, Play를 확장하여 계산한 공연비 정보를 관리하게 한다. 
 * 2. 계산시점과 출력시점 분리 : {@link #createStatement()}와 {@link #enrichPerformance()}메서드를 이용하여 공연비 계산시점과 출력시점을 분리한다.
 * (계산로직을 먼저 실행하여 나온 정보를 하나의 클래스로 관리하고, 출력시점에는 해당클래스를 가지고 출력기능만 하도록 분리한다.)
 * </PRE>
 * @author jaeHyun
 */
@SuppressWarnings("unused")
public class JsonTransformClass03 {
	private GsonHandler handler;
	private Gson gson;
	private Invoice invoice;
	private Map<String, Play> plays;

	// 생성자
	public JsonTransformClass03() {
		handler = new GsonHandler();						// gson을 사용한 Util
		gson = handler.getGson();
	}
	public JsonTransformClass03(JsonElement invoice, JsonElement plays) {
		handler = new GsonHandler();						// gson을 사용한 Util
		gson = handler.getGson();
		if(invoice.isJsonObject() && plays.isJsonObject()) {
			setInvoice(invoice.getAsJsonObject());			// 고객이 공연할 공연 정보
			setPlays(plays.getAsJsonObject());				// 공연 목록
			createStatement();								// 위의 정보를 토대로 출력 정보를 가공
		} else {
			throw new Error("JsonObject형태의 json이 아닙니다.");
		}
	}
	// get & set
	public GsonHandler getHandler() {
		return handler;
	}
	private void setHandler(GsonHandler handler) {
		this.handler = handler;
	}
	public Gson getGson() {
		return gson;
	}
	private void setGson(Gson gson) {
		this.gson = gson;
	}
	
	public Map<String, Play> getPlays() {
		return this.plays;
	}
	public void setPlays(JsonObject input) {
		Map<String, Play> plays = new HashMap<String, Play>();
		
		for (Entry<String, JsonElement> entry : input.entrySet()) {
			Play play = gson.fromJson(input.get(entry.getKey()), Play.class);
			plays.put(entry.getKey(), play);
		}
		this.plays = plays;
	}
	public Invoice getInvoice() {
		return this.invoice;
	}
	public void setInvoice(JsonObject input) {
		this.invoice = gson.fromJson(input, Invoice.class);
	}
	
	public void createStatement(JsonElement invoice, JsonElement plays) {
		if(invoice.isJsonObject() && plays.isJsonObject()) {
			setInvoice(invoice.getAsJsonObject());			// 고객이 공연할 공연 정보
			setPlays(plays.getAsJsonObject());
			createStatement();
		}
	}
	
	// 1. 계산된 공연비 정보를 가진 객체를 만든다.
	private void createStatement() {
		// json을 파싱한 초기 Invoice.performance에 속성 추가 [공연목록, 공연별 요금, 공연별 포인트] 
		invoice.setPerformances(enrichPerformance());
		// 총 금액
		invoice.setTotalAmount(getTotalAmount());
		// 적립포인트
		invoice.setTotalVolumeCredits(getVolumeCredits());
	}
	// 2. Performance객체에 공연비 계산에 필요한 값을 추가  
	private List<Performance> enrichPerformance() {
		for(Performance aPerformance: invoice.getPerformances()) {
			aPerformance.setPlay(playFor(aPerformance).clone());									// 공연 목록 세팅 : 객체이기 때문에 깊은복사후 할당
			aPerformance.setAmount(amountFor(aPerformance)); 										// 공연별 공연비 	
			aPerformance.setVolumeCredit(getVolumeCredit(aPerformance));							// 공연당 포인트
		}
		return invoice.getPerformances();
	}
	// 3. 계산된 정보를 가진 객체를 이용하여 공연 청구비 출력한다.
	public String statement() {
		//createStatement();	 => 생성자
		String result = "청구 내역 (고객명 : " + invoice.getCustomer() + ")\n";
		for(Performance aPerformance: invoice.getPerformances()) {
			result += aPerformance.getPlay().getName() + ": " + usd(aPerformance.getAmount() / 100) + ", (" + aPerformance.getAudience() + "석)\n";
		}
		result += "총액: " + usd(invoice.getTotalAmount() / 100) + "\n";
		result += "적립 포인트: " + invoice.getTotalVolumeCredits() + "점";
		return result;
	}
	/*
	 *  장르별 금액 계산
	 */
	private int amountFor(Performance aPerformance) {
		int result = 0;
		switch (aPerformance.getPlay().getType()) {
			case "tragedy":
				result = 40000;
				if (aPerformance.getAudience() > 30) {
					result += 1000 * (aPerformance.getAudience() - 30);
				}
				break;
			case "comedy":
				result = 30000;
				if (aPerformance.getAudience() > 20) {
					result += 10000 + 500 * (aPerformance.getAudience() - 20);
				}
				result += 300 * aPerformance.getAudience();
				break;
			default:
				throw new Error("알 수 없는 장르");
		}
		return result;
	}
	/**
	 * 총액 계산
	 */
	private int getTotalAmount() {
		int result = 0;
		for(Performance aPerformance: invoice.getPerformances()) {
			result += aPerformance.getAmount(); 
//			result += amountFor(playFor(aPerformance).getType(), aPerformance.getAudience()); 
		}
		return result;
	}
	/**
	 * 포인트 총액 계산
	 */
	private int getVolumeCredits(){
		int result = 0;
		for(Performance aPerformance: invoice.getPerformances()) {
			result += aPerformance.getVolumeCredit();
//			result += getVolumeCredit(aPerformance);
		}
		return result;
	}
	/**
	 * 공연당 포인트 계산
	 */
	private int getVolumeCredit(Performance aPerformance){
		int result = 0;
		result += aPerformance.getAudience() - 30;
		if (playFor(aPerformance).getType().equals("comedy")) {
			result += aPerformance.getAudience() / 5;
		}
		return result;
	}
	/**
	 * get Play : 자주 선언되는 변수, 함수로 변경
	 */
	private Play playFor(Performance aPerformance) {
		return plays.get(aPerformance.getPlayID());
	}
	/**
	 * usd 달러 format
	 * @param amount
	 * @return
	 */
	private String usd(int amount) {
		DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");
		return usd.format(amount);
	}
}
