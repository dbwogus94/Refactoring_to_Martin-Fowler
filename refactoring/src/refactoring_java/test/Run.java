package refactoring_java.test;

import com.google.gson.JsonElement;

import refactoring_java.json.GsonHandler;


public class Run {
	public static void main(String[] args) {
		
		String temp = "청구 내역 (고객명 : BingCo)\n" +
		    		  "Hamlet: $650.00, (55석)\n" + 
		    		  "As You Like It: $580.00, (35석)\n" + 
		    		  "Othello: $500.00, (40석)\n" +
		    		  "총액: $1,730.00\n" +
		    		  "적립 포인트: 47점\n";
	      
		System.out.println("\n========================================================================================================");
		System.out.println("# Class를 사용한 버전 : \n");
		
// 		GsonHandler	handler = new GsonHandler();
// 		JsonElement invoiceJson = handler.readJson_toJsonObject(GsonHandler.class.getResource("").getPath() + "invoices.json");
// 		JsonElement playsJson = handler.readJson_toJsonObject(GsonHandler.class.getResource("").getPath() + "plays.json");
//		JsonTransformClass03 t = new JsonTransformClass03(invoiceJson, playsJson);

		JsonTransformClass04 t = new JsonTransformClass04();
		JsonElement invoiceJson = t.getHandler().readJson_toJsonElement(GsonHandler.class.getResource("invoices.json").getPath());
//		JsonElement invoiceJson = t.getHandler().readJson_toJsonElement("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\refactoring\\src\\refactoring_java/json/invoices.json");
		JsonElement playsJson = t.getHandler().readJson_toJsonElement(GsonHandler.class.getResource("plays.json").getPath());
//		JsonElement playsJson = t.getHandler().readJson_toJsonElement("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\refactoring/src/refactoring_java/json/plays.json");
		t.createStatement(invoiceJson, playsJson);
		
		String result = t.statement();
//		System.out.println(t.getInvoice());
//		System.out.println("\n");
		
		System.out.println(result);
		System.out.print("\n테스트 결과: ");
		System.out.println(temp.replaceAll("\n", "").equals(result.replaceAll("\n", "")));
	      
		System.out.println("\n========================================================================================================\n");
		
		
//		File f = new File(".");
//		f.getAbsolutePath();	// 현재 경로
//		System.out.println(GsonHandler.class.getResource("").getPath());	// 해당 클래스의 경로
	}
}
