package refactoring_java.calculator.performance_calculator;

import refactoring_java.calculator.Performance;

public class ComedyCarculator extends PerformanceCalculator {
	
	public ComedyCarculator(Performance performance) {
		super(performance);
	}
	
	// 공연비
	@Override
	public int getAmount() {
		int result = 30000;
		if (super.performance.getAudience() > 20) {
			result += 10000 + 500 * (super.performance.getAudience() - 20);
		}
		result += 300 * super.performance.getAudience();
		return result;
	}
	// 포인트
	@Override
	public int getVolumeCredit() {
		return super.getVolumeCredit() + super.performance.getAudience() / 5;
	}
}
