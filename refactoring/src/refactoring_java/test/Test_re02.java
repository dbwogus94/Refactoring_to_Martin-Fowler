package refactoring_java.test;

import java.text.DecimalFormat;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import refactoring_java.json.GsonHandler;

@SuppressWarnings("unused")
public class Test_re02 {
	GsonHandler handler = new GsonHandler();
	Gson gson = new Gson();

	/**
	 * usd 달러 format
	 * @param amount
	 * @return
	 */
	private String usd(int amount) {
		DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");
		return usd.format(amount);
	}
	
	// 장르별 금액 계산
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
	
	private String statement(JsonObject invoice, JsonObject plays) {
		String result = "청구 내역 (고객명 : " + invoice.get("customer").getAsString() + ")\n";
		int totalPay = 0;
		int point = 0;
		int volumeCreadits = 0;

		JsonArray performances = (JsonArray) handler.jsonTypeTransform(invoice.get("performances"));
//		for(JsonElement jsonEle: performances) {
//			JsonObject aPerformance = (JsonObject) handler.jsonTypeTransform(jsonEle);
//			...
//		}
		Iterator<JsonElement> iter = performances.iterator();
		while (iter.hasNext()) {
			JsonObject aPerformance = (JsonObject) handler.jsonTypeTransform(iter.next());
			JsonObject play = (JsonObject) plays.get(aPerformance.get("playID").getAsString());
			
			String type = play.get("type").getAsString();				// 장르
			int audience = aPerformance.get("audience").getAsInt();		// 좌석
			
			int thisAmount = amountFor(type, audience);					// 공연당 금액 계산
			
			// 포인트
			volumeCreadits += audience - 30;
			if (play.get("type").getAsString().equals("comedy")) {
				volumeCreadits += audience / 5;
			}
		
			result += play.get("name").getAsString() + ": " + usd(thisAmount / 100) + ", (" + audience + "석)\n";
			totalPay += thisAmount;
		}
		result += "총액: " + usd(totalPay / 100) + "\n";
		result += "적립 포인트: " + volumeCreadits + "점";
		return result;
	}
	
	public static void main(String[] args) {
		GsonHandler gson = new GsonHandler();
		
//		String temp = "청구 내역 (고객명 : BingCo)\n" +
//		    		  "Hamlet: $650.00, (55석)\n" + 
//		    		  "As You Like It: $580.00, (35석)\n" + 
//		    		  "Othello: $500.00, (40석)\n" +
//		    		  "총액: $1,730.00\n" +
//		    		  "적립 포인트: 47점\n";
	      
		System.out.println("\n========================================================================================================");
		System.out.println("# Gson을 사용한 버전 : \n");
		Test_re02 t2 = new Test_re02();
		JsonObject invoiceJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
		JsonObject playsJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
		String result2 = t2.statement(invoiceJson, playsJson);
		
	      
//		System.out.println(result2);
//		System.out.print("\n테스트 결과: ");
//		System.out.println(temp.replaceAll("\n", "").equals(result2.replaceAll("\n", "")));
	      
		System.out.println("\n========================================================================================================\n");
	}

}
