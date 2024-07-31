package ca.yorku.eecs;


import static org.neo4j.driver.v1.Values.parameters;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.neo4j.driver.v1.exceptions.ClientException;

public class Connection {
	private Driver driver;
	private String uriDB;
	private String username = "neo4j";
	private String password = "12345678";
	public Connection() {
		uriDB = "bolt://localhost:7687";
		
		Config config = Config.builder().withoutEncryption().build();
		driver = GraphDatabase.driver(uriDB, AuthTokens.basic(username,password), config);
		
	}
	
	public String insertActor(String actor, String actorId) {
	    final String checkStatement = "MATCH (a:actor {id: \"" + actorId +"\"}) RETURN COUNT(a) > 0 as exists";
	    final String statementTemplate = "CREATE (a:actor {name: $name, id: $id})";
		

		try(Session session = driver.session()){
//			CHECK IF IT EXISTS
			boolean actorExists = session.readTransaction(tx -> {
	            StatementResult result = tx.run(checkStatement, parameters("id", actorId));
	            return result.single().get("exists").asBoolean();
	        });
			
//			IF IT DOES, THROW ERROR AND RETURN 404
			if(actorExists) {
				System.out.println("Actor with ID " + actorId + " already exists.");
				return "400";
			}else {
//				IF NOT, OPEN A NEW SESSION AND PUSH IT
				try (Session s = driver.session()){
					s.writeTransaction(tx -> tx.run(statementTemplate, parameters("name", actor, "id", actorId)));
				}
				return "200";
			}			
		}
	}
	
	public String insertMovie(String movie, String movieId) {
	    final String checkStatement = "MATCH (m:movie {id: \"" + movieId +"\"}) RETURN COUNT(m) > 0 as exists";
	    final String statementTemplate = "CREATE (m:movie {name: $name, id: $id})";
		

		try(Session session = driver.session()){
//			CHECK IF IT EXISTS
			boolean movieExists = session.readTransaction(tx -> {
	            StatementResult result = tx.run(checkStatement, parameters("id", movieId));
	            return result.single().get("exists").asBoolean();
	        });
			
//			IF IT DOES, THROW ERROR AND RETURN 404
			if(movieExists) {
				System.out.println("Movie with ID " + movieId + " already exists.");
				return "400";
			}else {
//				IF NOT, OPEN A NEW SESSION AND PUSH IT
				try (Session s = driver.session()){
					s.writeTransaction(tx -> tx.run(statementTemplate, parameters("name", movie, "id", movieId)));
				}
				return "200";
			}			
		}catch (Neo4jException e) {
	        System.err.println("Error inserting actor: " + e.getMessage());
	        throw new RuntimeException("Failed to insert actor", e);
	    }
	}


	public boolean actorExists(String actorId) {
	    try (Session session = driver.session()) {
			final String checkStatement = "MATCH (a:actor {id: \"" + actorId +"\"}) RETURN a";
	        // String query = "MATCH (a:Actor {id: $actorId}) RETURN a";
	        return session.run(checkStatement, Values.parameters("actorId", actorId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean movieExists(String movieId) {
	    try (Session session = driver.session()) {
	        // String query = "MATCH (m:Movie {id: $movieId}) RETURN m";
			final String checkStatement = "MATCH (m:movie {id: \"" + movieId +"\"}) RETURN m";
	        return session.run(checkStatement, Values.parameters("movieId", movieId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean addRelationship(String actorId, String movieId) {
	    try (Session session = driver.session()) {
	        // Check if the relationship already exists
	        String checkQuery = "MATCH (a:actor {id: \"" + actorId + "\"})-[r:ACTED_IN]->(m:movie {id: \"" + movieId + "\"}) RETURN r";
	        if (session.run(checkQuery, Values.parameters("actorId", actorId, "movieId", movieId)).hasNext()) {
	            return false; // Relationship already exists
	        }else{
				try(Session s = driver.session()){
					// Create the relationship
					String createQuery = "MATCH (a:actor {id: \"" + actorId + "\"}), (m:movie {id: \"" + movieId + "\"}) " +
	                             "CREATE (a)-[:ACTED_IN]->(m)";
	        		s.run(createQuery, Values.parameters("actorId", actorId, "movieId", movieId));
					return true; // Relationship successfully created
				}
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}


