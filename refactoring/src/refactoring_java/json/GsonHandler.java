package refactoring_java.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/* gson기본 사용법
 * 
 * jsonParser 		: String형태의 json을 json 객체로 만드는 파서
 * JsonElement 		: json의 요소를 가져오면 기본적으로 JsonElement 형태로 가져온다. (JsonElement를 제외한 4가지는 JsonElement를 상속) 
 * JsonObject		: key의 value가 object인 json객체 		ex) {key1: {key2: value}} 
 * JsonArray		: key의 value가 Array인 json객체			ex) {key1: [item1, item2, item3...]}
 * JsonPrimitive	: key의 value가 원시타입인 json객체		ex) {key1: value}
 * JsonNull			: null object를 표현하기 위한 class
 */
@SuppressWarnings({"unchecked", "unused"})
public class GsonHandler {				// 모든 메서드를 static으로 변경하던가, 싱글톤으로 관리되게 변경 예정 
	private Gson gson = new Gson();
	private JsonParser jsonParser = new JsonParser();
	
	public Gson getGson() {
		return gson;
	}
	public void setGson(Gson gson) {
		this.gson = gson;
	}
	public JsonParser getJsonParser() {
		return jsonParser;
	}
	public void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}
	/**
	 * map => json
	 * 
	 * @param Map<String,Object> data
	 * @return String
	 */
	public String makeMapToJson(Map<String, Object> data) {
		return gson.toJson(data);
	}
	/**
	 * JsonObject => Map
	 * 
	 * @param JsonObject jsonObjects
	 * @return HashMap
	 */
	public Map<String, Object> parseJSON_toJsonObj(JsonObject jsonObject) {
		return gson.fromJson(jsonObject, Map.class);
	}
	/**
	 * Json => Map
	 * 
	 * @param String json
	 * @return HashMap<String, JsonObject>
	 */
	public Map<String, Object> parseJSON_toMap(String json) {
		return gson.fromJson(json, Map.class);
	}
	/**
	 * json => JsonElement
	 * 
	 * @param json
	 * @return JsonElement
	 */
	public JsonElement parseJSON_toJsonEle(String json) {
		return (JsonElement) jsonParser.parse(json);
	}
	/**
	 * *.json file => JsonElement
	 * 
	 * @param String filePath
	 * @return JsonObject
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public JsonElement readJson_toJsonElement(String filePath) {
		try {
			return jsonParser.parse(new FileReader(filePath));
		} catch (JsonIOException e) {
			throw new Error("[Error] Gson I/O Error.");
		} catch (JsonSyntaxException e) {
			throw new Error("[Error] JSON Syntax Error.");
		} catch (FileNotFoundException e) {
			throw new Error("[Error] 파일 없음.");
		}
	}
	/**
	 * *.json file => JsonObject
	 * 
	 * @param String filePath
	 * @return JsonObject
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public JsonObject readJson_toJsonObject(String filePath) {
		JsonElement element;
		try {
			element = jsonParser.parse(new FileReader(filePath));
			if(element.isJsonObject()) {
				return element.getAsJsonObject();
			}
			return null;
		} catch (JsonIOException e) {
			throw new Error("[Error] Gson I/O Error.");
		} catch (JsonSyntaxException e) {
			throw new Error("[Error] JSON Syntax Error.");
		} catch (FileNotFoundException e) {
			throw new Error("[Error] 파일 없음.");
		}
	}
	/**
	 * *.json file => JsonArray
	 * 
	 * @param filePath
	 * @return JsonArray
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public JsonArray readJson_toJsonArray(String filePath) {
		JsonElement element;
		try {
			element = jsonParser.parse(new FileReader(filePath));
			if(element.isJsonArray()) {
				return element.getAsJsonArray();
			}
		} catch (JsonIOException e) {
			throw new Error("[Error] Gson I/O Error.");
		} catch (JsonSyntaxException e) {
			throw new Error("[Error] JSON Syntax Error.");
		} catch (FileNotFoundException e) {
			throw new Error("[Error] 파일 없음.");
		}
		return null;
	}
	/**
	 * *.json file => Map
	 * 
	 * @param String filePath
	 * @return HashMap<String, Object>
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws FileNotFoundException
	 */
	public Map<String, Object> readJson_toHashMap(String filePath) {
		JsonElement element = null;
		try {
			element = jsonParser.parse(new FileReader(filePath));
			return gson.fromJson(element, Map.class);
		} catch (JsonIOException e) {
			throw new Error("[Error] Gson I/O Error.");
		} catch (JsonSyntaxException e) {
			throw new Error("[Error] JSON Syntax Error.");
		} catch (FileNotFoundException e) {
			throw new Error("[Error] 파일 없음.");
		}
	}
	
	/**
	 * JsonElement => object
	 * @param element
	 * @return object
	 */
	public Object jsonTypeTransform(JsonElement element) {
		Object result = null;
		if(element.isJsonObject()) {
			result = element.getAsJsonObject();
		} else if(element.isJsonArray()) {
			result = element.getAsJsonArray();
		} else if(element.isJsonNull()) {
			result = element.getAsJsonNull();
		} else if(element.isJsonPrimitive()) {
			JsonPrimitive primitve = element.getAsJsonPrimitive(); 
			if(primitve.isBoolean()) {
				result = primitve.getAsBoolean();
			} else if (primitve.isString()) {
				result = primitve.getAsString();
			} else if (primitve.isNumber()) {
				result = primitve.getAsNumber();
			}
		}
		return result;
	}
	/**
	 * JsonObject => object
	 * @param jsonObj
	 * @return object
	 */
	public Object jsonTypeTransform(JsonObject jsonObj) {
		Object result = null;
		if(jsonObj.isJsonObject()) {
			result = jsonObj.getAsJsonObject();
		} else if(jsonObj.isJsonArray()) {
			result = jsonObj.getAsJsonArray();
		} else if(jsonObj.isJsonNull()) {
			result = jsonObj.getAsJsonNull();
		} else if(jsonObj.isJsonPrimitive()) {
			JsonPrimitive primitve = jsonObj.getAsJsonPrimitive(); 
			if(primitve.isBoolean()) {
				result = primitve.getAsBoolean();
			} else if (primitve.isString()) {
				result = primitve.getAsString();
			} else if (primitve.isNumber()) {
				result = primitve.getAsNumber();
			}
		}
		return result;
	}
}
