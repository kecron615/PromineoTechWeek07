package project.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import project.DaoBase;
import project.entity.Category;
import project.entity.Material;
import project.entity.Project;
import project.entity.Step;
import project.exception.DbException;

//////////CRUD operations////////////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("unused")
public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

///////////////Insert a project row into the project table////////////////////////////////////////////////////////////////////

	public Project insertProject(Project project) {
		//@formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		//@formatter: on
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
					
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch(SQLException e) {
			throw new DbException(e);
		}
	}

////////////////////////////Fetch all Projects//////////////////////////////////////////////////////////////////////////////////////
	public List<Project> fetchAllProjects() {
	    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
	    
	    try (Connection conn = DbConnection.getConnection()) {
	        startTransaction(conn);
	        List<Project> projects = new LinkedList<>(); // Initialize the list here
	        
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    Project project = new Project();
	                    
	                    project.setActualHours(rs.getBigDecimal("actual_hours"));
	                    project.setDifficulty(rs.getObject("difficulty", Integer.class));
	                    project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
	                    project.setNotes(rs.getString("notes"));
	                    project.setProjectId(rs.getObject("project_id", Integer.class));
	                    project.setProjectName(rs.getString("project_name"));
	                    
	                    projects.add(project);
	                }
	                
	                return projects;
	            }
	        } catch (Exception e) {
	            rollbackTransaction(conn);
	            throw new DbException(e);
	        }
	    } catch (SQLException e) {
	        throw new DbException(e);
	    }
	}
///////////////////WEEK 10 MODIFICATIONS TO DAO /////////////////////////////////////////////////////////////////////////////////////////
	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try {
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					try(ResultSet rs = stmt.executeQuery()){
						
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
			
		
		if(Objects.nonNull(project)) {
			project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
			project.getSteps().addAll(fetchStepsForProject(conn, projectId));
			project.getCategories().addAll(fetchCatgoriesforProject(conn, projectId));
		}
			commitTransaction(conn);
				
				return Optional.ofNullable(project);
			}
			catch(Exception e){
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
	}
/////////////////////////////////////////Fetch Materials for WEEK 10////////////////////////////////////////////////////////////////////////

private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException{
	//@formatter: off
	String sql = ""
			+"SELECT m.* FROM " + MATERIAL_TABLE + " m "
			+"WHERE project_id = ? "; 
	//@formatter: on
	
	try(PreparedStatement stmt = conn.prepareStatement(sql)){
		setParameter(stmt, 1, projectId, Integer.class);
		
		try(ResultSet rs = stmt.executeQuery()){
			List<Material> materials = new LinkedList<>();
			
			while(rs.next()) {
				materials.add(extract(rs, Material.class));
			}
			return materials; 
		}
	}
}
/////////////////////////////////////////Fetch Steps for WEEK 10////////////////////////////////////////////////////////////////////////

private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException{
	//@formatter:off
	String sql = ""
			+ "SELECT s.* FROM " + STEP_TABLE + " s "
			+"JOIN " + PROJECT_TABLE + " p ON s.project_id = p.project_id WHERE p.project_id = ?";
	//@formatter:on
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	}

/////////////////////////////////////////Fetch categories for WEEK 10//////////////////////////////////////////////////////////////////
	private List<Category> fetchCatgoriesforProject(Connection conn, Integer projectId) throws SQLException {

	//@formatter:off
	String sql = ""
			+"SELECT c.* FROM " + CATEGORY_TABLE + " c "
			+"JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
			+"WHERE project_id = ?";
	//@formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}
	}

}
