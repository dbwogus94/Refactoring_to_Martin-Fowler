package refactoring_java.calculator.performance_calculator;

import refactoring_java.calculator.Performance;

public class TragedyCarculator extends PerformanceCalculator {

	public TragedyCarculator(Performance performance) {
		super(performance);
	}

	// 공연비
	@Override
	public int getAmount() {
		int result = 40000;
		if (super.performance.getAudience() > 30) {
			result += 1000 * (super.performance.getAudience() - 30);
		}
		return result;
	}
	// 공연비 > 부모로직 사용
	
}
