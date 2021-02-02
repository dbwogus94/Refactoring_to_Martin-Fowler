package refactoring_java.test;

import java.text.DecimalFormat;
import java.util.HashMap;
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
 * * step2. {@link #statement()} 메서드를 기능별로 분리하여 관리하기 쉽게 만들어라
 * <b>기본 - 가장 기본적인 리팩토링</b> 
 * 1. 반복문 쪼개기
 * 2. 문장 슬라이드하기
 * 3. 함수 추출하기	: 빈번하게 변수를 함수화, 계산로직 추출...
 * 4. 변수 인라인하기 
 * </PRE>
 * @author jaeHyun
 */
@SuppressWarnings("unused")
public class JsonTransformClass02 {
	private GsonHandler handler = new GsonHandler();
	private Gson gson = new Gson();
	private Invoice invoice;
	private Map<String, Play> plays;

	// 생성자
	public JsonTransformClass02(JsonElement invoice, JsonElement plays) {
		if(invoice.isJsonObject()) {
			setInvoice(invoice.getAsJsonObject());
		} else {
			throw new Error("JsonObject형태의 json이 아닙니다.");
		}
		if(plays.isJsonObject()) {
			setPlays(plays.getAsJsonObject());
		} else {
			throw new Error("JsonObject형태의 json이 아닙니다.");
		}
	}
	
	// get & set
	private Map<String, Play> getPlays(JsonObject input) {
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
	private Invoice getInvoice(JsonObject input) {
		return this.invoice;
	}
	private void setInvoice(JsonObject input) {
		this.invoice = gson.fromJson(input, Invoice.class);
	}
	
	// 공연 청구비 출력
	private String statement() {
		String result = "청구 내역 (고객명 : " + invoice.getCustomer() + ")\n";
		
		for(Performance aPerformance: invoice.getPerformances()) {
			Play play = plays.get(aPerformance.getPlayID());
			result += play.getName() + ": " + usd(amountFor(play.getType(), aPerformance.getAudience()) / 100) + ", (" + aPerformance.getAudience() + "석)\n";
		}
		
		result += "총액: " + usd(getTotalAmount() / 100) + "\n";
		result += "적립 포인트: " + getVolumeCredits() + "점";
		return result;
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
	/*
	 *  장르별 금액 계산
	 */
	private int amountFor(String type, int audience) {
		int result = 0;
		switch (type) {
			case "tragedy":
				result = 40000;
				if (audience > 30) {
					result += 1000 * (audience - 30);
				}
				break;
			case "comedy":
				result = 30000;
				if (audience > 20) {
					result += 10000 + 500 * (audience - 20);
				}
				result += 300 * audience;
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
			result += amountFor(playFor(aPerformance).getType(), aPerformance.getAudience()); 
		}
		return result;
	}
	/**
	 * 포인트 계산
	 */
	private int getVolumeCredits(){
		int result = 0;
		for(Performance aPerformance: invoice.getPerformances()) {
			result += aPerformance.getAudience() - 30;
			if (playFor(aPerformance).getType().equals("comedy")) {
				result += aPerformance.getAudience() / 5;
			}
		}
		return result;
	}
	/**
	 * get Play : 자주 선언되는 변수, 함수로 변경
	 */
	private Play playFor(Performance aPerformance) {
		return plays.get(aPerformance.getPlayID());
	}
	
	
	public static void main(String[] args) {
		GsonHandler gson = new GsonHandler();
		
		String temp = "청구 내역 (고객명 : BingCo)\n" +
		    		  "Hamlet: $650.00, (55석)\n" + 
		    		  "As You Like It: $580.00, (35석)\n" + 
		    		  "Othello: $500.00, (40석)\n" +
		    		  "총액: $1,730.00\n" +
		    		  "적립 포인트: 47점\n";
	      
		System.out.println("\n========================================================================================================");
		System.out.println("# Gson을 사용한 버전 : \n");
		JsonElement invoiceJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
		JsonElement playsJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
		
		JsonTransformClass02 t = new JsonTransformClass02(invoiceJson, playsJson);
		String result2 = t.statement();
		
	      
		System.out.println(result2);
		System.out.print("\n테스트 결과: ");
		System.out.println(temp.replaceAll("\n", "").equals(result2.replaceAll("\n", "")));
	      
		System.out.println("\n========================================================================================================\n");
	}
}
