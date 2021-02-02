'use strict';

/**
 * 구조화를 통한 확장1
 * 1. 출력되는 형식에 종속되지 않게 데이터 처리만 관리하는 함수를 추출한다.
 * - 추가 함수 [step 7]
 */
function statement(invoice, plays){
	return renderPlainText(createStatementData(invoice, plays));
	
	/**
	* step 7: 데이터 처리 함수선언
	*/
	function createStatementData(invoice, plays) {
		const statementData = {};
		// 고객명
		statementData.customer = invoice.customer;
		// 공연별 금액/좌석 
		//statementData.performances = invoice.performances;							// 얕은 복사 x,
		statementData.performances = invoice.performances.map((aperformance) => {		// 깊은 복사(**가장 밖의 Object만 복사를 수행, object의 내부의 item은 깊은 복사를 수행하지 않음)
			return Object.assign({}, aperformance);
		})
		// 총액
		statementData.totalAmount = getTotalAmount(invoice.performances);		 
		// 총 포인트
		statementData.totalPoint = getTotalPoint(invoice.performances);			
		return statementData;
	}
	/**
	* step 6: plain Text 형식 출력 
	*/
	function renderPlainText(data){
		let result = `청구 내역 (고객명 : ${data.customer})\n`;
		
		for(let aPerformance of data.performances){
			result += `${playFor(aPerformance).name}: ${format_US(getAmount(playFor(aPerformance).type, aPerformance.audience)/100)}, (${aPerformance.audience}석)\n`;
		}
		
		result += `총액: ${format_US(data.totalAmount/100)}\n`; 
		result += `적립 포인트: ${data.totalPoint}점`;
		return result;
	}
	/**
	* step 1: playFor  
	* 여러번 자주 호출되는 변수는 함수로 변환하여 호출하자
	*/
	function playFor(aPerformance){
		return plays[aPerformance.playID];
	}
	/**
	* step 21: 달러 format기능 분리
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
	function getAmount(type, audience){
		let result = 0;
		switch(type){
			case "tragedy":				// 비극
				result = 40000;	
				if(audience > 30) {
					result += 1000 * (audience - 30);
				}
				break;
			case "comedy":				// 희극
				result = 30000;
	            if(audience > 20) {
	            	result += 10000 + 500 * (audience - 20);
	            }
	            result += 300 * audience;
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
		let result = 0;
		for(let aPerformance of performances){
			result += getAmount(playFor(aPerformance).type, aPerformance.audience);		// 장르별 금액 계산 함수(getAmount()) 호출
		}
		return result;
	}
	/**
	* step 5-1: 포인트 계산 로직 
	*/
	function getPoint(aPerformance){
		let result = Math.max(aPerformance.audience - 30, 0);
	    if("comedy" === playFor(aPerformance).type){
	    	result += Math.floor(aPerformance.audience / 5);
	    } 
		return result;
	}
	/**
	* step 5-2: 총 포인트 계산 
	*/
	function getTotalPoint(performances){
		let result = 0;
		for(let aPerformance of performances){
			result += getPoint(aPerformance);
		}
		return result;
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