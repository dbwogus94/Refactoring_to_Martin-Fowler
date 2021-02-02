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
import refactoring_java.calculator.performance_calculator.ComedyCarculator;
import refactoring_java.calculator.performance_calculator.PerformanceCalculator;
import refactoring_java.calculator.performance_calculator.TragedyCarculator;
import refactoring_java.json.GsonHandler;

/**
 * <PRE>
 * @Title <b>리펙터링 2판 초기 예제. 공연비 계산기: JavaScript코드 Java로 변경</b></BR>
 * step4. 장르별 공연비, 포인트를 계산하는 기능을 확장가능한 계산기 클래스로 분리한다. 
 * 1. 계산기(super)class를 상속하는 희극계산기class, 비극계산기class 생성
 * 2. 계산용 메서드({@link #amountFor(Performance)}, {@link #getVolumeCredit(Performance)})를 
 * 	  계산기 클래스({@link #createPerformanceCalculator(Performance, Play)})로 변경
 * </PRE>
 * @author jaeHyun
 */
@SuppressWarnings("unused")
public class JsonTransformClass04 {
	private GsonHandler handler;
	private Gson gson;
	private Invoice invoice;
	private Map<String, Play> plays;

	// 생성자
	public JsonTransformClass04() {
		handler = new GsonHandler();
		gson = handler.getGson();
	}
	public JsonTransformClass04(JsonElement invoice, JsonElement plays) {
		handler = new GsonHandler();
		gson = handler.getGson();
		if(invoice.isJsonObject() && plays.isJsonObject()) {
			setInvoice(invoice.getAsJsonObject());			// 고객이 공연할 공연 정보
			setPlays(plays.getAsJsonObject());				// 공연 목록
			createStatement();								// 위의 정보를 토대로 출력 정보를 가공
		} else {
			throw new Error("JsonObject형태의 json이 아닙니다.");
		}
	}
	// get & set : 외부에서 set은 사용 불가하도록 설계
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
	
	/**
	 * 공연 목록 
	 */
	public Map<String, Play> getPlays() {
		return this.plays;
	}
	private void setPlays(JsonObject input) {
		Map<String, Play> plays = new HashMap<String, Play>();
		
		for (Entry<String, JsonElement> entry : input.entrySet()) {
			Play play = gson.fromJson(input.get(entry.getKey()), Play.class);
			plays.put(entry.getKey(), play);
		}
		this.plays = plays;
	}
	/**
	 * 고객이 요청한 공연정보
	 */
	public Invoice getInvoice() {
		return this.invoice;
	}
	private void setInvoice(JsonObject input) {
		this.invoice = gson.fromJson(input, Invoice.class);
	}
	
	/**
	 * class 생성시 파라이터를 전달하지 않았을 경우 초기데이터 세팅하는데 사용되는 메서드
	 */
	public void createStatement(JsonElement invoice, JsonElement plays) {
		if(invoice.isJsonObject() && plays.isJsonObject()) {
			setInvoice(invoice.getAsJsonObject());			// 고객이 공연할 공연 정보
			setPlays(plays.getAsJsonObject());
			createStatement();
		}
	}
	
	/**
	 * 1. 계산된 공연비 정보를 가진 객체를 만든다.
	 */
	private void createStatement() {
		// json을 파싱한 초기 Invoice.performance에 속성 추가 [공연목록, 공연별 요금, 공연별 포인트] 
		invoice.setPerformances(enrichPerformance());
		// 총 금액
		invoice.setTotalAmount(getTotalAmount());
		// 적립포인트
		invoice.setTotalVolumeCredits(getVolumeCredits());
	}
	/**
	 * 2. Performance객체에 공연비 계산에 필요한 값을 추가  
	 */
	private List<Performance> enrichPerformance() {
		for(Performance aPerformance: invoice.getPerformances()) {
			// 계산기
			PerformanceCalculator calculator = createPerformanceCalculator(aPerformance, playFor(aPerformance));
			aPerformance.setPlay(playFor(aPerformance).clone());									// 공연 목록 세팅 : 객체이기 때문에 깊은복사 후 할당
			aPerformance.setAmount(calculator.getAmount()); 										// 계산기.getAmount()호출시 공연비 계산 	
			aPerformance.setVolumeCredit(calculator.getVolumeCredit());								// 계산기.getVolumeCredit()호출시 포인트 계산
//			aPerformance.setAmount(amountFor(aPerformance)); 											
//			aPerformance.setVolumeCredit(getVolumeCredit(aPerformance));							
		}
		return invoice.getPerformances();
	}
	/**
	 * 3. 계산된 정보를 가진 객체를 이용하여 공연 청구비 출력한다.
	 */
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
	/**
	 * 장르별 계산기 생성
	 */
	private PerformanceCalculator createPerformanceCalculator(Performance performance, Play play) {
		switch(play.getType()) {
			case "tragedy":
				return new TragedyCarculator(performance); 
			case "comedy":
				return new ComedyCarculator(performance);
			default:
				throw new Error("알 수 없는 장르: " + play.getType());
		}
	}
	/*
	 *  장르별 금액 계산	> 제거: PerformanceCalculator 클래스로 변경
	 */
//	private int amountFor(Performance aPerformance) {
//		int result = 0;
//		switch (aPerformance.getPlay().getType()) {
//			case "tragedy":
//				result = 40000;
//				if (aPerformance.getAudience() > 30) {
//					result += 1000 * (aPerformance.getAudience() - 30);
//				}
//				break;
//			case "comedy":
//				result = 30000;
//				if (aPerformance.getAudience() > 20) {
//					result += 10000 + 500 * (aPerformance.getAudience() - 20);
//				}
//				result += 300 * aPerformance.getAudience();
//				break;
//			default:
//				throw new Error("알 수 없는 장르");
//		}
//		return result;
//	}
	/**
	 * 공연당 포인트 계산  > 제거: PerformanceCalculator 클래스로 변경
	 */
//	private int getVolumeCredit(Performance aPerformance){
//		int result = 0;
//		result += aPerformance.getAudience() - 30;
//		if (playFor(aPerformance).getType().equals("comedy")) {
//			result += aPerformance.getAudience() / 5;
//		}
//		return result;
//	}
	/**
	 * 총액 계산
	 */
	private int getTotalAmount() {
		int result = 0;
		for(Performance aPerformance: invoice.getPerformances()) {
			result += aPerformance.getAmount(); 
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
	 * @param int amount
	 * @return
	 */
	public String usd(int amount) {
		DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");
		return usd.format(amount);
	}
}
