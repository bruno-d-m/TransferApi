package com.revolut.transferApi.controller;

import com.revolut.transferApi.model.ApiResponse;
import com.revolut.transferApi.service.BankAccountService;

import spark.Request;
import spark.Response;
import spark.Route;

public class BankAccountController {
	
	private static BankAccountService bankAccountService = new BankAccountService();

	public static Route createBankAccount = (Request request, Response response) -> {
		ApiResponse apiResponse = bankAccountService.createBankAccount(request.body());
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};

	public static Route getAllBankAccounts = (Request request, Response response) -> {
		ApiResponse apiResponse =  bankAccountService.getAllBankAccounts();
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};
	
	public static Route getBankAccountById = (Request request, Response response) -> {
		ApiResponse apiResponse =  bankAccountService.getBankAccountById(Long.valueOf(request.params(":id")));
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};

	public static Route updateBankAccount = (Request request, Response response) -> {
		ApiResponse apiResponse =  bankAccountService.updateBankAccount(request.body());
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};

	public static Route deleteBankAccount = (Request request, Response response) -> {
		ApiResponse apiResponse =  bankAccountService.deleteBankAccount(Long.valueOf(request.params(":id")));
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};
	public static Route createInitialData = (Request request, Response response) -> {
		ApiResponse apiResponse =  bankAccountService.createInitialData();
		response.status(apiResponse.getCode());
		return apiResponse.toJson();
	};
	
}
