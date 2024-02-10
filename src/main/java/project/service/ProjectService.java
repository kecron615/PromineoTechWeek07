
package project.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import project.dao.ProjectDao;
import project.entity.Project;
import project.exception.DbException;

public class ProjectService {
	private static final String SCHEMA_FILE = "projects-schema.sql";
	// private static final String DATA_FILE = "project_data.sql"

	private ProjectDao projectDao = new ProjectDao();

/////////////////////The Project object with the newly generated primary key value/////////////////////////////////////////////

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

////////////////////////////////////////////////To call all the projects available in the Database////////////////////////////////////
	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();

	}

	public Project fetchProjectById(Integer projectId) {
		//Optional<Project> op = projectDao.fetchProjectById(projectId);
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID " + projectId + " does not exist. "));
	}

}
