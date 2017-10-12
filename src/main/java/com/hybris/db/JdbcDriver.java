package com.hybris.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		try {
			
			//1. get a connection to class
			Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/provision", "hybris", "hybris");
			//2. Create a Statement
			Statement statement = jdbcConnection.createStatement();
			//3. Execute SQL query
			ResultSet resultSet = statement.executeQuery("select * from environment");
			//4. Process the result set
			while(resultSet.next()){
				System.out.println(resultSet.getString("host_names") + ", " + resultSet.getString("host_ips"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
