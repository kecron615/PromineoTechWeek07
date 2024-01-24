package project;

import java.sql.Connection;

import project.dao.DbConnection;

public class Project {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection conn = DbConnection.getConnection();
	}

}
