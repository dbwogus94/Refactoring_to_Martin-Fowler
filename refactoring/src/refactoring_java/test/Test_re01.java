package refactoring_java.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import refactoring_java.json.GsonHandler;

@SuppressWarnings("unused")
public class Test_re01 {
	GsonHandler handler = new GsonHandler();
	
	// 자바 Map을 사용한 버전
	@SuppressWarnings("unchecked")
	private String statement(Map<String, Object> invoice, Map<String, Object> plays) {
		String result = "청구 내역 (고객명 : " + invoice.get("customer") + ")\n";
		int totalPay = 0;
		int point = 0;
		int volumeCreadits = 0;

		// 달러 포메터
		DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");

		// unchecked 방어 코드
		List<Object> performances = new ArrayList<Object>();
		if (invoice.get("performances") instanceof ArrayList<?>) {
			for (Object obj : (ArrayList<?>) invoice.get("performances")) {
				performances.add(obj);
			}
		}

		Iterator<Object> iter = performances.iterator();
		while (iter.hasNext()) {
			Map<String, Object> aPerformance = (Map<String, Object>) iter.next();
			Map<String, Object> play = (Map<String, Object>) plays.get(aPerformance.get("playID"));
			int thisAmount = 0;
			int audience = 0;

			switch ((String) play.get("type")) {
				case "tragedy":
					thisAmount = 40000;
					audience = (int) Double.parseDouble(aPerformance.get("audience") + "");
	
					if (audience > 30) {
						thisAmount += 1000 * (audience - 30);
					}
					break;
				case "comedy":
					thisAmount = 30000;
					audience = (int) Double.parseDouble(aPerformance.get("audience") + "");
	
					if (audience > 20) {
						thisAmount += 10000 + 500 * (audience - 20);
					}
					thisAmount += 300 * audience;
					break;
				default:
					throw new Error("알 수 없는 장르");
			}
			// 포인트
			volumeCreadits += audience - 30;
			if (play.get("type").equals("comedy")) {
				volumeCreadits += audience / 5;
			}

			result += play.get("name") + ": " + usd.format(thisAmount / 100) + ", (" + audience + "석)\n";
			totalPay += thisAmount;
		}

		result += "총액: " + usd.format(totalPay / 100) + "\n";
		result += "적립 포인트: " + volumeCreadits + "점";
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
	private String usd(String amount) {
		DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");
		return usd.format(amount);
	}
	
	// Gson사용 버전, Gson에서 제공하는 json 타입확인 메서드 사용
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
			int thisAmount = 0;
			int audience = 0;
			JsonObject aPerformance = (JsonObject) handler.jsonTypeTransform(iter.next());
			JsonObject play = (JsonObject) plays.get(aPerformance.get("playID").getAsString());

			switch (play.get("type").getAsString()) {
			case "tragedy":
				thisAmount = 40000;
				audience = aPerformance.get("audience").getAsInt();
				if (audience > 30) {
					thisAmount += 1000 * (audience - 30);
				}
				break;
			case "comedy":
				thisAmount = 30000;
				audience = aPerformance.get("audience").getAsInt();
				if (audience > 20) {
					thisAmount += 10000 + 500 * (audience - 20);
				}
				thisAmount += 300 * audience;
				break;
			default:
				throw new Error("알 수 없는 장르");
			}
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
		
		 System.out.println("\n========================================================================================================");
		 String temp = "청구 내역 (고객명 : BingCo)\n" +
		    		  "Hamlet: $650.00, (55석)\n" + 
		    		  "As You Like It: $580.00, (35석)\n" + 
		    		  "Othello: $500.00, (40석)\n" +
		    		  "총액: $1,730.00\n" +
		    		  "적립 포인트: 47점\n";
		  
		 System.out.println("# Java map를 사용한 버전 : \n");
		 Test_re01 t = new Test_re01();
		 Map<String, Object> invoice = gson.readJson_toHashMap("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
		 Map<String, Object> plays = gson.readJson_toHashMap("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
		 String result = t.statement(invoice, plays);

		 System.out.println(result);
		 System.out.print("\n테스트 결과: ");
		 System.out.println(temp.replaceAll("\n", "").equals(result.replaceAll("\n", "")));
	      
		 System.out.println("\n========================================================================================================");
	      
	      
	      
		 System.out.println("\n========================================================================================================");
		 System.out.println("# Gson을 사용한 버전 : \n");
		 Test_re01 t2 = new Test_re01();
		 JsonObject invoiceJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
		 JsonObject playsJson = gson.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
		 String result2 = t2.statement(invoiceJson, playsJson);
	      
		 System.out.println(result2);
		 System.out.print("\n테스트 결과: ");
		 System.out.println(temp.replaceAll("\n", "").equals(result2.replaceAll("\n", "")));
	      
		 System.out.println("\n========================================================================================================\n");
	}

}
