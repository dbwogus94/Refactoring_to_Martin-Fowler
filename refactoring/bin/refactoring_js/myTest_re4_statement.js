'use strict';
/*
* 구조분리 - 출력 기능 
*/
//import createStatementData from './myTest_re4_createStatementData.js'

function statement(invoice, plays){
	//console.log(JSON.stringify(createStatementData(invoice, plays)))
	return renderPlainText(createStatementData(invoice, plays));
	//return renderHTML(invoice, plays);
	
	/**
	* step 9: HTML 형식 출력
	*/
	function renderHTML(invoice, plays){
		return "";
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
	* step 2: 달러 format기능 분리
	*/
	function format_US(amount){
		return new Intl.NumberFormat("en-US", {
			style: "currency",
			currency: "USD",
			minimumFractionDigits: 2
		}).format(amount);
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