package com.revolut.transferApi.controller;

import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.service.TransactionService;

import spark.Request;
import spark.Response;
import spark.Route;

public class TransactionController {
	
	private static TransactionService transactionService = new TransactionService();

	public static Route createTransaction = (Request request, Response response) -> {
		ApiResponse apiResponse = transactionService.createTransaction(request.body());
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};
	
	public static Route getAllTransactions = (Request request, Response response) -> {
		ApiResponse apiResponse = transactionService.getAllTransactions();
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};
	
	public static Route getTransactionById = (Request request, Response response) -> {
		ApiResponse apiResponse = transactionService.getTransactionById(Long.valueOf(request.params(":id")));
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};

}
