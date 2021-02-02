'use strict';

/**
 * 구조화를 통한 확장2
 * 1. 데이터만 처리하는 함수를 효율적으로 변환한다.
 * 2. 데이터 처리 함수에 따라 이전 함수를 재구성.  
 * - 추가 함수 [step 8, step 9]
 * - 수정 함수 [step3, step 4, step 5-1, step 5-2]
 */
function statement(invoice, plays){
	console.log(JSON.stringify(createStatementData(invoice, plays)))
	return renderPlainText(createStatementData(invoice, plays));
	//return renderHTML(invoice, plays);
	
	/**
	* step 9: HTML 형식 출력
	*/
	function renderHTML(invoice, plays){
		return "";
	}
	/**
	* step 8: 출력에 사용되는 데이터 처리 함수
	*/
	function createStatementData(invoice, plays) {
		const statementData = {};
		statementData.customer = invoice.customer;
		statementData.performances = invoice.performances.map((enrichPerformance));		// Array.prototype.map(callback)	>> item을 순차적으로 callback의 파라미터로 전달하고 연산. 최종 결과를 배열로 리턴한다.		 
		// 총 공연비
		statementData.totalAmount = getTotalAmount(statementData.performances);			// totalAmount = statementData.performances[0].amount + statementData.performances[1].amount + statementData.performances[2].amount
		// 총 포인트
		statementData.totalPoint = getTotalPoint(statementData.performances);			// totalPoint = statementData.performances[0].point + statementData.performances[1].point + statementData.performances[2].point
		return statementData;
		/*
		 statementData =
		 {
			 "customer":"BingCo",
			 "performances":
			 	[
				 	{"playID": "hamlet", "audience": 55, "play": {"name":"Hamlet","type":"tragedy"}, "amount": 65000, "point": 25},
				 	{"playID": "as-like", "audience": 35, "play": {"name":"As You Like It","type":"comedy"}, "amount": 58000, "point": 12},
				 	{"playID": "othello", "audience": 40, "play": {"name":"Othello","type":"tragedy"}, "amount": 50000, "point": 10}
		 		],
			 "totalAmount":173000,
			 "totalPoint":47
		 }
		 */
	}
	/**
	* step 7: set Data
	* 자주 사용되는 invoice.performances를 사용하여 1차 연산 
	*/
	function enrichPerformance(aPerformance) {
		// result = aPerformance 깊은 복사
		const result = Object.assign({}, aPerformance);									// es6부터 제공되는 객체 깊은복사. (**가장 밖의 Object만 복사를 수행, object의 내부의 item은 깊은 복사를 수행하지 않음)
		// result에 해당하는 단일 공연 정보
	    result.play = playFor(result);
		// result에 해당하는 단일 공연 금액
	    result.amount = getAmount(result);
		// result에 해당하는 단일 적립 포인트
	    result.point = getPoint(result);
	    return result;
	}
	/**
	* step 6: 결과 plain Text 형식 출력 
	*/
	function renderPlainText(data){
		let result = `청구 내역 (고객명 : ${data.customer})\n`;
		
		for(let aPerformance of data.performances){
			result += `${aPerformance.play.name}: ${format_US(aPerformance.amount/100)}, (${aPerformance.audience}석)\n`;
		}
		result += `총액: ${format_US(data.totalAmount/100)}\n`; 
		result += `적립 포인트: ${data.totalPoint}점`;
		return result;
	}
	/**
	* step 1: 공연 id에 따른 공연정보를 가져온다.  
	* 여러번 자주 호출되는 변수는 함수로 변환하여 호출하자
	*/
	function playFor(aPerformance){
		return plays[aPerformance.playID];
	}
	/**
	* step 2: 달러 format기능 분리
	*/
	function format_US(amount){
		return new Intl.NumberFormat("en-US", {
			style: "currency",
			currency: "USD",
			minimumFractionDigits: 2
		}).format(amount);
	}
	/**
	* step 3: 공연 type별 금액 계산
	*/
	function getAmount(aPerformance){
		let result = 0;
		switch(aPerformance.play.type){
			case "tragedy":				// 비극
				result = 40000;	
				if(aPerformance.audience > 30) {
					result += 1000 * (aPerformance.audience - 30);
				}
				break;
			case "comedy":				// 희극
				result = 30000;
	            if(aPerformance.audience > 20) {
	            	result += 10000 + 500 * (aPerformance.audience - 20);
	            }
	            result += 300 * aPerformance.audience;
				break;
			default:
				throw new Error(`알 수 없는 장르: ${type}`)
		}
		return result;
	}
	/**
	* step 4: 종액 계산
	*/
	function getTotalAmount(performances){
		/*
		let result = 0;
		for(let aPerformance of performances){
			result += aPerformance.amount;
		}
		return result;
		*/
		//배열.reduce((누적값, 현잿값, 인덱스, 요소) => { return 결과 }, 초깃값) : 순차적으로 연산을 수행. 최종 결과를 return 
		return performances.reduce((total, aPerformance) => total + aPerformance.amount, 0);			
	}
	/**
	* step 5-1: 포인트 계산 로직 
	*/
	function getPoint(aPerformance){
		let result = Math.max(aPerformance.audience - 30, 0);
	    if("comedy" === aPerformance.play.type){
	    	result += Math.floor(aPerformance.audience / 5);
	    } 
		return result;
	}
	/**
	* step 5-2: 총 포인트 계산
	*/
	function getTotalPoint(performances){
		/*
		let result = 0;
		for(let aPerformance of performances){
			result += aPerformance.point;
		}
		return result;
		*/
		return performances.reduce((total, aPerformance) => total + aPerformance.point, 0);
	}
}

function main(){
	const invoice = { 
			"customer": "BingCo", 
			"performances": [{ "playID": "hamlet", "audience": 55 }, { "playID": "as-like", "audience": 35 }, { "playID": "othello", "audience": 40 }]
	};
	const plays = 	{
			  "hamlet": { "name": "Hamlet", "type": "tragedy" },
			  "as-like": { "name": "As You Like It", "type": "comedy" },
			  "othello": { "name": "Othello", "type": "tragedy" }
			};
	console.log(statement(invoice, plays));
	console.log(
			  statement(invoice, plays).includes(
			    `청구 내역 (고객명 : BingCo)
Hamlet: $650.00, (55석)
As You Like It: $580.00, (35석)
Othello: $500.00, (40석)
총액: $1,730.00
적립 포인트: 47점`
			  )
			);
}