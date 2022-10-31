package com.bridgelabz;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;

	// Instance of EmployeePayrollDBService to be used in methods.
	private EmployeePayrollDBService employeePayrollDBService;


	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		
		System.out.println("Enter the Employee Id : ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter the Employee Name : ");
		String name = consoleInputReader.next();
		System.out.println("Enter the Employee Salary : ");
		double salary = consoleInputReader.nextDouble();
		
		employeePayrollList.add(new EmployeePayrollData(id, name, salary, LocalDate.now()));
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}
	
	public void writeEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee Payroll Roster to Console\n" + employeePayrollList);
		
		else if(ioService.equals(IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
	}
	
	public void printData(IOService fileIo) {
		if(fileIo.equals(IOService.FILE_IO)) new EmployeePayrollFileIOService().printData();
	}


	public long countEntries(IOService fileIo) {
		if(fileIo.equals(IOService.FILE_IO)) 
			return new EmployeePayrollFileIOService().countEntries();
		
		return 0;
	}
		
	public long readDataFromFile(IOService fileIo) {
		
		List<String> employeePayrollFromFile = new ArrayList<String>();
		if(fileIo.equals(IOService.FILE_IO)) {
			System.out.println("Employee Details from payroll-file.txt");
			employeePayrollFromFile = new EmployeePayrollFileIOService().readDataFromFile();
			
		}
		return employeePayrollFromFile.size();
	}

	//UC2 - read data from database
	public List<EmployeePayrollData> readEmployeePayrollDataFromDatabase(IOService ioService){
		if(ioService.equals(IOService.DB_IO)){
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		}
		return this.employeePayrollList;
	}

	//UC3 - update salary using statement

	private EmployeePayrollData getEmployeePayrollData(String name){
		return this.employeePayrollList.stream()
				.filter(EmployeePayrollDataItem->EmployeePayrollDataItem.employeeName.equals(name))
				.findFirst().orElse(null);
	}

	public void  updateEmployeeSalary(String name, double salary){
		int result = employeePayrollDBService.updateEmployeeSalary(name, salary);

		if(result == 0)
			return;

		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null)
			employeePayrollData.employeeSalary = salary;
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name){
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return  employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}



	public static void main(String[] args) {
		
		System.out.println("---------- Welcome To Employee Payroll Application ----------\n");
		List<EmployeePayrollData> employeePayrollList = null;
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);

		employeePayrollList = employeePayrollService.readEmployeePayrollDataFromDatabase(IOService.DB_IO);

		System.out.println();
		employeePayrollList.forEach(System.out::println);  // java 8 feature

	}

}
