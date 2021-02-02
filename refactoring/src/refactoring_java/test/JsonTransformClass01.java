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
 * *step1. json을 java언어에 맞게 class로 관리하라.
 * 1. json 파싱기능 분리: gson기능을 GsonHandler클레스로 분리
 * 2. json to class: json을 java클래스로 변경
 * 3. 공연비 출력: 생성한 class로 공연비 출력
 * </PRE> 
 * @author jaeHyun
 */
@SuppressWarnings("unused")
public class JsonTransformClass01 {
	private GsonHandler handler = new GsonHandler();
	private Gson gson = new Gson();

	/**
	 * JsonObject => Map<String, Play>
	 * @param JsonObject input
	 * @return Map<String, Play>
	 */
	private Map<String, Play> getPlays(JsonObject input) {
		Map<String, Play> plays = new HashMap<String, Play>();
		for (Entry<String, JsonElement> entry : input.entrySet()) {
			Play play = gson.fromJson(input.get(entry.getKey()), Play.class);
			plays.put(entry.getKey(), play);
		}
		return plays;
	}
	/**
	 * JsonObject => Invoice
	 * @param JsonObject input
	 * @return Invoice
	 */
	private Invoice getInvoice(JsonObject input) {
		return gson.fromJson(input, Invoice.class);
	}
	
	private String statement(Invoice invoice, Map<String, Play> plays) {
		String result = "청구 내역 (고객명 : " + invoice.getCustomer() + ")\n";
		int totalPay = 0;
		int point = 0;
		int volumeCredits = 0;

		List<Performance> performances = invoice.getPerformances();
		for(Performance aPerformance: performances) {
			Play play = plays.get(aPerformance.getPlayID());
			int thisAmount = 0;
			
			switch (play.getType()) {
				case "tragedy":
					thisAmount = 40000;
					if (aPerformance.getAudience() > 30) {
						thisAmount += 1000 * (aPerformance.getAudience() - 30);
					}
					break;
				case "comedy":
					thisAmount = 30000;
					if (aPerformance.getAudience() > 20) {
						thisAmount += 10000 + 500 * (aPerformance.getAudience() - 20);
					}
					thisAmount += 300 * aPerformance.getAudience();
					break;
				default:
					throw new Error("알 수 없는 장르");
			}
			
			// 포인트
			volumeCredits += aPerformance.getAudience() - 30;
			if (play.getType().equals("comedy")) {
				volumeCredits += aPerformance.getAudience() / 5;
			}
		
			result += play.getName() + ": " + usd(thisAmount / 100) + ", (" + aPerformance.getAudience() + "석)\n";
			totalPay += thisAmount;
		}
		result += "총액: " + usd(totalPay / 100) + "\n";
		result += "적립 포인트: " + volumeCredits + "점";
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
		JsonTransformClass01 t = new JsonTransformClass01();
		JsonObject invoiceJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
		JsonObject playsJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");

		String result2 = t.statement(t.getInvoice(invoiceJson), t.getPlays(playsJson));
		
	      
		System.out.println(result2);
		System.out.print("\n테스트 결과: ");
		System.out.println(temp.replaceAll("\n", "").equals(result2.replaceAll("\n", "")));
	      
		System.out.println("\n========================================================================================================\n");
	}
}
