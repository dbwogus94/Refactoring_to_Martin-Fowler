package refactoring_java.calculator.performance_calculator;

import refactoring_java.calculator.Performance;

public class PerformanceCalculator {
	Performance performance;
	int Amount;
	int volumeCredit;
	
	// 생성자
	public PerformanceCalculator(Performance aperformance) {
		this.performance = aperformance;
	}
	
	// get && set : 필요없는 getter/setter 제거
	
	// 공연비
	public int getAmount() {
        throw new Error("자식 클래스에서 호출하도록 설계되었습니다!");
	}
	// 포인트
	public int getVolumeCredit() {
		// 공통 계산 로직은 부모클래스에 구현: 기본포인트 =  관객수 - 30
		return this.performance.getAudience() - 30;
	}
}
