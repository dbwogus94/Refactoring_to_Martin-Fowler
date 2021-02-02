package refactoring_java.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * <PRE>
 * @Title <b>리펙터링 2판 초기 예제. 공연비 계산기: JavaScript코드 Java로 변경 </b></BR>
 * *조건. 클래스 미사용, json을 직접 파싱하여 출력
 * 1. gson Util 생성
 * 2. java 컬렉션을 사용하여 공연비 출력
 * 3. gson을 사용을 사용하여 공연비 출력
 * </PRE>
 * @author jaeHyun
 */
@SuppressWarnings("unused")
public class JsonTransformClass00 {
   private Gson gson = new Gson();
   
   /**
    * map => json
    * @param Map<String,Object> data
    * @return String
    */
   private String makeMapToJson(Map<String,Object> data){
      return gson.toJson(data);
   }
   /**
    * JsonObject => HashMap
    * @param JsonObject jsonObjects
    * @return HashMap
    */
   @SuppressWarnings("unchecked")
   private Map<String, Object> parseJSON(JsonObject jsonObject) {
      return gson.fromJson(jsonObject, Map.class);
   }
   /**
    * json => map 
    * @param String json
    * @return HashMap<String, JsonObject>
    */
   @SuppressWarnings("unchecked")
   private Map<String, Object> parseJSON(String json) {
      return gson.fromJson(json, Map.class);
   }
   /**
    * *.json => JsonObject
    * @param String filePath
    * @return JsonObject
    * @throws JsonIOException
    * @throws JsonSyntaxException
    * @throws FileNotFoundException
    */
   private JsonObject readJson_toJsonObject(String filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
      JsonParser jsonParser = new JsonParser();
      JsonElement element = jsonParser.parse(new FileReader(filePath));
      return element.getAsJsonObject();
   }
   /**
    * *.json => HashMap
    * @param String filePath
    * @return HashMap<String, Object>
    * @throws JsonIOException
    * @throws JsonSyntaxException
    * @throws FileNotFoundException
    */
   @SuppressWarnings("unchecked")
   private Map<String, Object> readJson_toHashMap(String filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
      JsonParser jsonParser = new JsonParser();
      JsonElement element = jsonParser.parse(new FileReader(filePath));
//      JsonObject jsonObject = element.getAsJsonObject();
      return gson.fromJson(element, Map.class);
   }
   
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
      if(invoice.get("performances") instanceof ArrayList<?>) {
         for(Object obj: (ArrayList<?>)invoice.get("performances")) {       
            performances.add(obj);                                 
         }
      }
      
      Iterator<Object> iter = performances.iterator();
      while(iter.hasNext()) {
         Map<String, Object> aPerformance = (Map<String, Object>) iter.next();
         Map<String, Object> play = (Map<String, Object>) plays.get(aPerformance.get("playID"));
         int thisAmount = 0;
         int audience = 0;
         
         switch((String)play.get("type")) {
            case "tragedy":
               thisAmount = 40000;
               audience = (int)Double.parseDouble(aPerformance.get("audience") + "");
               
               if(audience > 30) {
                  thisAmount += 1000 * (audience - 30);
               }
               break;
            case "comedy":
               thisAmount = 30000;
               audience = (int)Double.parseDouble(aPerformance.get("audience") + ""); 
               
               if(audience > 20) {
                  thisAmount += 10000 + 500 * (audience -20);
               }
               thisAmount += 300 * audience;
               break;
            default:
               throw new Error("알 수 없는 장르");
         }
         // 포인트
         volumeCreadits += audience - 30;
         if(play.get("type").equals("comedy")) {
            volumeCreadits += audience/5;
         }
         
         result += play.get("name") + ": " + usd.format(thisAmount/100) + ", (" + audience+ "석)\n";
         totalPay += thisAmount;
      }
      
      result += "총액: " + usd.format(totalPay/100) + "\n"; 
      result += "적립 포인트: " + volumeCreadits + "점";
      return result;
   }
   
   // Gson사용 버전, Gson에서 제공하는 json 타입확인 메서드 사용
   private String statement(JsonObject invoice, JsonObject plays) {
      String result = "청구 내역 (고객명 : " + invoice.get("customer").getAsString() + ")\n";
      int totalPay = 0;
      int point = 0;
      int volumeCreadits = 0;
      
      // 달러 포메터
      DecimalFormat usd = new DecimalFormat("$###,###,###,###.00");
      
      //  Gson에서 권장하는 방법 : json에서 새로운 객체를 가져오는 경우 객체의 타입이 JsonArray, JsonObject, JsonPrimitive인지 확인 후 사용.
      if(invoice.get("performances").isJsonArray()) {
    	  JsonArray performances = invoice.get("performances").getAsJsonArray();
    	  Iterator<JsonElement> iter = performances.iterator();
    	  
    	  while(iter.hasNext()) {
    		  int thisAmount = 0; 
        	  int audience = 0;
    		  JsonElement temp = iter.next();		// 주의 : Iterator.next() 호출할 때마다 다음 item을 가리키게 된다.
    		  if(temp.isJsonObject()) {
    			  JsonObject aPerformance = temp.getAsJsonObject();
    			  JsonObject play = (JsonObject)plays.get(aPerformance.get("playID").getAsString());
    			  
    			  switch(play.get("type").getAsString()) {
	                  case "tragedy":
	                     thisAmount = 40000;
	                     audience = aPerformance.get("audience").getAsInt();
	                     if(audience > 30) {
	                    	 thisAmount += 1000 * (audience - 30);
	                     }
	                     break;
	                  case "comedy":
	                     thisAmount = 30000;
	                     audience = aPerformance.get("audience").getAsInt();
	                     if(audience > 20) {
	                       thisAmount += 10000 + 500 * (audience -20);
	                     }
	                     thisAmount += 300 * audience;
	                     break;
	                  default:
	                	  throw new Error("알 수 없는 장르");
    			  }
    			  volumeCreadits += audience - 30;
    			  if(play.get("type").getAsString().equals("comedy")){
    				  volumeCreadits += audience/5;
    			  }
    			  result += play.get("name").getAsString() + ": " + usd.format(thisAmount/100) + ", (" + audience+ "석)\n";
		          totalPay += thisAmount;
    		  }
    	  }
      }
      result += "총액: " + usd.format(totalPay/100) + "\n";
      result += "적립 포인트: " + volumeCreadits + "점";
      return result;
   }
   
   
   
   
   public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
      // 포메터 사용법
//      double money = 1845510.11;
//      DecimalFormat dc = new DecimalFormat("$###,###,###,###.00");
//      String ch = dc.format(돈);
//      System.out.println(ch);
      
      /* gson기본 사용법
       * 
       * jsonParser 	: String형태의 json을 json 객체로 만드는 파서
       * JsonElement 	: json의 요소를 가져오면 기본적으로 JsonElement 형태로 가져온다. (JsonElement를 제외한 4가지는 JsonElement를 상속) 
       * JsonObject		: key의 value가 object인 json객체 		ex) {key1: {key2: value}} 
       * JsonArray		: key의 value가 Array인 json객체			ex) {key1: [item1, item2, item3...]}
       * JsonPrimitive	: key의 value가 원시타입인 json객체		ex) {key1: value}
       * JsonNull		: null object를 표현하기 위한 class
       */
//	  /* json을 읽어오는 jsonParser 선언 */
//      JsonParser jsonParser = new JsonParser();
//      /* json파일을 읽어서 json element로 가져온다. */
//      JsonElement element = jsonParser.parse(new FileReader("C:\\workspaces\\workspace_emro\\TestProkect\\src\\test\\invoices.json"));
//      /* jsonObject인지 판별 */
//      boolean isJsonObj = element.isJsonObject();
//      /* JsonElement를  JsonObject로 변환한다. */
//      JsonObject jsonObject = element.getAsJsonObject();
//      /* Json을 java의 HashMap로 변환한다. */
//      HashMap<String, Object> map = new Gson().fromJson(jsonObject, HashMap.class);
//      /* Map를 jsonString로 변경 */
//      Gson gson = new Gson();
//      String jsonString = gson.toJson(map);
//      /* jsonString를 jsonElement로 변경 */
//      JsonElement jsonElement = jsonParser.parse(jsonString);
//      /* jsonString를 JsonObject로 변경 */
//      JsonObject jsonObj = (JsonObject) jsonParser.parse(jsonString);
      
//      System.out.println("map: " + map);
//      System.out.println("JsonString: " + jsonString);
//      System.out.println("JsonElement: " + jsonElement);
//      System.out.println("JsonObject: " + jsonObj);
      
//      System.out.println(jsonObject.get("customer"));
//      System.out.println(map.get("customer"));
//      System.out.println(jsonObject.get("performances").getAsJsonArray().get(0).getAsJsonObject().get("audience").getAsInt());
//      System.out.println(jsonObject.get("performances").getAsJsonArray().get(0).getAsJsonObject().get("audience").getAsString());
      
	   System.out.println("\n========================================================================================================");
	   String temp = "청구 내역 (고객명 : BingCo)\n" +
	    		  "Hamlet: $650.00, (55석)\n" + 
	    		  "As You Like It: $580.00, (35석)\n" + 
	    		  "Othello: $500.00, (40석)\n" +
	    		  "총액: $1,730.00\n" +
	    		  "적립 포인트: 47점\n";
	  
      System.out.println("# Java map를 사용한 버전 : \n");
      JsonTransformClass00 t = new JsonTransformClass00();
      Map<String, Object> invoice = t.readJson_toHashMap("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
      Map<String, Object> plays = t.readJson_toHashMap("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
      String result = t.statement(invoice, plays);

      System.out.println(result);
      System.out.print("\n테스트 결과: ");
      System.out.println(temp.replaceAll("\n", "").equals(result.replaceAll("\n", "")));
      
      System.out.println("\n========================================================================================================");
      
      
      
      System.out.println("\n========================================================================================================");
      System.out.println("# Gson을 사용한 버전 : \n");
      JsonTransformClass00 t2 = new JsonTransformClass00();
      JsonObject invoiceJson = t2.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json");
      JsonObject playsJson = t2.readJson_toJsonObject("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json");
      String result2 = t2.statement(invoiceJson, playsJson);
      
      System.out.println(result2);
      System.out.print("\n테스트 결과: ");
      System.out.println(temp.replaceAll("\n", "").equals(result2.replaceAll("\n", "")));
      
      System.out.println("\n========================================================================================================\n");
      
      
      
//      JsonParser jsonParser = new JsonParser();
//      JsonElement invoiceJson = jsonParser.parse(new FileReader("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\invoices.json"));
//      JsonElement playsJson = jsonParser.parse(new FileReader("C:\\Users\\nextict\\Desktop\\eclipse\\TO_BE\\eclipse(oxygen)\\workspace_TOBE\\test\\src\\test\\plays.json"));
//      System.out.println(invoiceJson.getAsJsonObject());
      
      
      
      
      
/**
 * # unchecked 경고 방어 			// unchecked란? : 런타임시에 선언된 객체의 타입으로 할당 되는것을 보장할 수 없다는 경고이다. 주로 제네릭 사용시 발생함.
 * 
 *      Map안에 있는 List를 꺼내어 새로운 List에 할당하는 작업에서 아래와 같이 코드를 작성했다면 
 *       unchecked(무접점)경고가 발생함. 이는 컴파일시 제네릭에 정의된 타입으로 할당되는 것을 보장하지 못 한다는 경고임 
 *       즉, 런타임시 타입으로 인한 에러가 발생할 수 있다.
 *       이 경우 개발자가 완벽하게 에러가 발생하지 않는다는 판단이 있다면 @SuppressWarnings("unchecked")해당 어노테이션을 사용하여 무시 할 수 있다.
 *       그렇지 않다면 아래와 같은 방어를 해주는 것을 권장함. 
 */
      /* 1. 무작정 할당:  unchecked을 방어하지 못함 */
//      List<Map<String, String>> performances_fall2 = (List<Map<String, String>>) invoice.get("performances");

      /* 2. unchecked을 방어하지 못함
       * : List<Map<String, String>> : Object => Map<String, String>이 형변환되어 할당되는것을  보장 할 수 없음.
       */ 
//      List<Map<String, String>> performances_fall1 = new ArrayList<Map<String, String>>();
//      if(invoice.get("performances") instanceof ArrayList<?>) {
//         for(Object obj: (ArrayList<?>)invoice.get("performances")) {      // ArrayList인 것은 확인 가능  
//            performances_fall1.add((Map<String, String>) obj);            // 문제는 ArrayList안의 item이 어떠한 타입인지 보장할 수 없음
//         }
//      }
      
      /* 3. unchecked 방어 성공
       * : List<Object> : Object => Object에 할당되는 것은 보장됨 unchecked 방어  
       */
//      List<Object> performances = new ArrayList<Object>();
//      if(invoice.get("performances") instanceof ArrayList<?>) {
//         for(Object obj: (ArrayList<?>)invoice.get("performances")) {       
//            performances.add(obj);                                 // List<Object> : Object => Object에 할당되는 것은 보장됨 unchecked 방어
//         }
//      }
      
//      List<Object> performances = new ArrayList<Object>();
//      for(Map.Entry<String, Object> e: invoice.entrySet()) {
//         if(e.getValue() instanceof ArrayList<?>) {
//            for(Object list: (ArrayList<?>)e.getValue()) {
//               performances.add(list);
//            }
//         }
//      }
//      System.out.println(performances);
   }
}
