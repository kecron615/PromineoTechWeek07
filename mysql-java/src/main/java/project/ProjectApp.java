package project;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import project.dao.DbConnection;
import project.entity.Project;
import project.exception.DbException;
import project.service.ProjectService;

public class ProjectApp {
@SuppressWarnings("unused")
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();

	//@formatter:off
	private List<String> operations = List.of(
			"1) Add a project"
			);
	//@formatter:on
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection conn = DbConnection.getConnection();
		new ProjectApp().processUserSelections();
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void processUserSelections() {
		boolean done = false;

		while (!done) {

			try {
				int operation = getUserSelections();
				switch (operation) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				
				default:
					System.out.println("\n" + operation + " is not valid. Try again");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nERROR: " + e.toString() + " Try again.");
			}
		}
	}
	//////Gather user input for a project row and call the project service to create the row///////////////////////////////////////////////////
	private void createProject() {
		String projectName = getStringInput("Enter the project name ");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours ");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours ");
		Integer diffculty = getIntInput("Enter the project diffculty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(diffculty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}
//Get user input from console and convert it to BigDecimal////////////////////////////////////////////////////////////////////////////////////
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}
//////Called when the user wants to exit the menu//////////////////////////////////////////////////////
	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}
	
////////////////////Prints available menu selections, takes user input///////////////////////////////////////
	private int getUserSelections() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection: ");

		return Objects.isNull(input) ? -1 : input;
	}
//////////////Prints a prompt and takes user input. Return trimmed input/////////////////////////////////
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}
/////////////////Prints the menu selections/////////////////////////////////////////////////////////////////////////////
	private void printOperations() {
		System.out.println();
		System.out.println("\nThese are the available selections. Press the Enter key to quit: ");

		operations.forEach(line -> System.out.println("    " + line));
	}
	
///////////////////Creates a new table//////////////////////////////////////////////////////////////////////////////////
	private void createTables() {
		projectService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");

	}
////////////////////Gets user input for menu selections///////////////////////////////////////////////////////////////////
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
}
