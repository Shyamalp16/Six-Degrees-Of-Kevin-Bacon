package ca.yorku.eecs;


import static org.neo4j.driver.v1.Values.parameters;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
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
	        // You might want to re-throw the exception or handle it in a way that's appropriate for your application
	        throw new RuntimeException("Failed to insert actor", e);
	    }
	}
}
