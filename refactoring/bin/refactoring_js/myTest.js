'use strict';

function statement(invoice, plays){
	let result = `청구 내역 (고객명 : ${invoice.customer})\n`;
	let totalPay = 0;
	let point = 0;
	const format = new Intl.NumberFormat("en-US", {
		style: "currency",
		currency: "USD",
		minimumFractionDigits: 2
	}).format;

	// 공연당 금액계산 type에 따라 금액이 다르다  
	for(let perf of invoice.performances){
		const play = plays[perf.playID];
		let thisAmount = 0;
		
		if(play.type === "tragedy"){
			 thisAmount = 40000;	
             if(perf.audience > 30) {
                 thisAmount += 1000 * (perf.audience - 30);
             }
             
		} else if(play.type === "comedy"){
			thisAmount = 30000;
            if(perf.audience > 20) {
                thisAmount += 10000 + 500 * (perf.audience - 20);
            }
            thisAmount += 300 * perf.audience;
		} else {
			throw new Error(`알 수 없는 장르: ${play.type}`)
		}
		// 포인트
		point += Math.max(perf.audience - 30, 0);
	    if("comedy" === play.type) point += Math.floor(perf.audience / 5);
		
		result += `${play.name}: ${format(thisAmount/100)}, (${perf.audience}석)\n`;
		totalPay += thisAmount;
	}
	
	result += `총액: ${format(totalPay/100)}\n`; 
	result += `적립 포인트: ${point}점`;	
	return result;
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
}