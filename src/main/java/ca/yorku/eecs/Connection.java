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
	
	public void insertActor(String actor, String actorId) {
		final String statementTemplate = "MERGE (a:actor {name: $x})";
		final String keysAndValues = "x";
		try(Session session = driver.session()){
			session.writeTransaction(tx -> tx.run(statementTemplate, parameters(keysAndValues, actor)));
		}
	}

	public boolean actorExists(String actorId) {
	    try (Session session = driver.session()) {
	        String query = "MATCH (a:Actor {id: $actorId}) RETURN a";
	        return session.run(query, Values.parameters("actorId", actorId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean movieExists(String movieId) {
	    try (Session session = driver.session()) {
	        String query = "MATCH (m:Movie {id: $movieId}) RETURN m";
	        return session.run(query, Values.parameters("movieId", movieId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean addRelationship(String actorId, String movieId) {
	    try (Session session = driver.session()) {
	        // Check if the relationship already exists
	        String checkQuery = "MATCH (a:Actor {id: $actorId})-[r:ACTED_IN]->(m:Movie {id: $movieId}) RETURN r";
	        if (session.run(checkQuery, Values.parameters("actorId", actorId, "movieId", movieId)).hasNext()) {
	            return false; // Relationship already exists
	        }

	        // Create the relationship
	        String createQuery = "MATCH (a:Actor {id: $actorId}), (m:Movie {id: $movieId}) " +
	                             "CREATE (a)-[:ACTED_IN]->(m)";
	        session.run(createQuery, Values.parameters("actorId", actorId, "movieId", movieId));
	        return true; // Relationship successfully created
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}
