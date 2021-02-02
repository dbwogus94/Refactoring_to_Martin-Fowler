package refactoring_java.calculator;

/**
 * 공연의 이름과 장르를 담는 class
 * @author 
 */
public class Play implements Cloneable {		// Cloneable 구현해야 Deep copy를 사용가능함
	private String name;	// 공연이름
	private String type;	// 장르
	
	public Play() {
	}
	public Play(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	/* implements Cloneable구현 하지 않으면 사용 불가 : 속성이 class인 경우 그 객체까지 deep copy하지않는다. */ 
	public Play Cloneable_clone() {
		Play play = null;
		try {
			play = (Play) super.clone();
		} catch (CloneNotSupportedException e) {	// implements Cloneable구현 하지 않으면 사용 불가
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return play;
	}
	
	/* 새로운 객체 리턴 : 속성이 class인 경우 그 객체까지 deep copy하지않는다. */ 
	public Play clone() {
		return new Play(this.name, this.type);
	}
	
	@Override
	public String toString() {
		return "Play [name=" + name + ", type=" + type + "]";
	}
}
