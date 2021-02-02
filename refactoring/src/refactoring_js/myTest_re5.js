'use strict';

const plays = require('./plays.json');

module.exports = function (invoice) {
    const statementData = {};

    statementData.customer = invoice.customer;

    statementData.performances = invoice.performances.map(enrichPerformance);

    statementData.totalAmount = totalAmount(statementData);

    statementData.totalVolumeCredits = totalVolumeCredits(statementData);

    return statementData;
}

/**
 * 공연비 계산기 - 부모클래스
 */
class PerformanceCalculator {
    constructor(aPerformance, aPlay) {
        this.performance = aPerformance;
        this.play = aPlay;
    }

    get amount() {
    	// 자식 클래스에서 호출하도록 설계
        throw new Error('Subclass responsibility!');
    }

    get volumeCredits() {
        return Math.max(this.performance.audience - 30, 0);
    }
}
/**
 * 비극 공연비 계산기 - 자식클래스
 */
class TragedyCalculator extends PerformanceCalculator {
	/* Super의 constructor 호출 */
	
	// @Override
    get amount() {	
        let result = 40000;

        if(this.audience > 30) {
            result += 1000 * (this.audience - 30);
        }

        return result;
    }
}
/**
 * 희극 공연비 계산기 - 자식클래스
 */
class ComedyCalculator extends PerformanceCalculator {
	/* Super의 constructor 호출 */
	
	// @Override
    get amount() {	
        let result = 30000;

        if(this.performance.audience > 20) {
            result += 10000 + 500 * (this.performance.audience - 20);
        }

        result += 300 * this.performance.audience;

        return result;
    }
    // @Override
    get volumeCredits() {	
        return super.volumeCredits + Math.floor(this.performance.audience / 5);
    }
}

/**
 * 함수를 통한 클래스 생성 
 */
function createPerformanceCalculator(aPerformance, aPlay) {
    switch(aPlay.type) {
        case "tragedy": return new TragedyCalculator(aPerformance, aPlay);

        case "comedy": return new ComedyCalculator(aPerformance, aPlay);

        default:
            throw new Error(`Unknown type: ${aPlay.type}`);
    }
}

/**
 * .map() callback에 사용되는 함수 
 */
function enrichPerformance(aPerformance) {
    const calculator = createPerformanceCalculator(aPerformance, playFor(aPerformance));	// 계산기 생성

    const result = Object.assign({}, aPerformance);

    result.play = calculator.play;			// get play 호출(선언되어 있지 않아도 constructor안에 정의된 속성은 기본적으로 get/set이 생성된다.)

    result.amount = calculator.amount;		// get amount 호출, 자식 클래스에 Override된 메서드가 없다면 부모(Super)의 메서드를 호출한다.

    result.volumeCredits = calculator.volumeCredits;	// 비극은 Override된 메서드 호출, 희극은 this에 선언된 메서드가 없기 때문에 Super의 메서드를 호출한다.

    return result;
}

/*============= 공통 함수 ============*/
function playFor(aPerformance) {
    return plays[aPerformance.playID];
}

function totalVolumeCredits(data) {
//    let result = 0;
//    for(let perf of data.performances) {
//        result += perf.volumeCredits;
//    }
//    return result;
	
	return data.performances.reduce((total, aPerformance) => {total + aPerformance.volumeCredits}, 0);
}

function totalAmount(data) {
//    let result = 0
//    for(let perf of data.performances) {
//        result += perf.amount;
//    }
//    return result;
	return data.performances.reduce((total, aPerformance) => {total + aPerformance.amount}, 0);
}