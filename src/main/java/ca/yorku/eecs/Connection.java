package ca.yorku.eecs;


import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import java.util.LinkedHashMap; 
import java.util.Map;

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

	public String deleteMovie(String movieId){
		final String remRel = "MATCH (m:movie {id: \"" + movieId + "\"})<-[r:ACTED_IN]-() DELETE r";
		final String checkStatement = "MATCH (m:movie {id: \"" + movieId +"\"}) RETURN COUNT(m) > 0 as exists";
		final String deleteStatement = "MATCH (m:movie {id: \"" + movieId +"\"}) DELETE m";	

		try(Session session = driver.session()){
//			CHECK IF IT EXISTS
			boolean movieExists = session.readTransaction(tx -> {
				StatementResult result = tx.run(checkStatement, parameters("id", movieId));
				return result.single().get("exists").asBoolean();
			});
			
//			IF IT DOES NOT, THROW ERROR AND RETURN 404
			if(!movieExists) {
				System.out.println("Movie with ID " + movieId + " Doesnt Exist.");
				return "400";
			}else {
				// CHECK IF THERE ARE ANY RELATIONSHIPS, IF YES REMOVE THEM FIRST
				try(Session s1 = driver.session()){
					s1.writeTransaction(tx -> tx.run(remRel, parameters("id", movieId)));
				}
//				IF YES, OPEN A NEW SESSION AND DELETEIT
				try (Session s = driver.session()){
					s.writeTransaction(tx -> tx.run(deleteStatement, parameters("id", movieId)));
				}
				return "200";
			}			
		}catch (Neo4jException e) {
			System.err.println("Error Deleting Movie: " + e.getMessage());
			throw new RuntimeException("Failed to Delete Movie", e);
		}
	}
	

	public String deleteActor(String actorId){
		// final String remRel = "MATCH (a:actor {id: \"" + actorId + "\"})<-[r:ACTED_IN]-() DELETE r";
		final String remRel = "MATCH (a:actor {id: \"" + actorId + "\"})-[r:ACTED_IN]->() RETURN r";
		final String checkStatement = "MATCH (a:actor {id: \"" + actorId +"\"}) RETURN COUNT(a) > 0 as exists";
		final String deleteStatement = "MATCH (a:actor {id: \"" + actorId +"\"}) DELETE a";	

		try(Session session = driver.session()){
//			CHECK IF IT EXISTS
			boolean actorExists = session.readTransaction(tx -> {
				StatementResult result = tx.run(checkStatement, parameters("id", actorId));
				return result.single().get("exists").asBoolean();
			});
			
//			IF IT DOES NOT, THROW ERROR AND RETURN 404
			if(!actorExists) {
				System.out.println("Actor with ID " + actorId + " Doesnt Exist.");
				return "400";
			}else {
				// CHECK IF THERE ARE ANY RELATIONSHIPS, IF YES REMOVE THEM FIRST
				try(Session s1 = driver.session()){
					s1.writeTransaction(tx -> tx.run(remRel, parameters("id", actorId)));
				}
//				IF YES, OPEN A NEW SESSION AND DELETEIT
				try (Session s = driver.session()){
					s.writeTransaction(tx -> tx.run(deleteStatement, parameters("id", actorId)));
				}
				return "200";
			}			
		}catch (Neo4jException e) {
			System.err.println("Error Deleting Actor: " + e.getMessage());
			throw new RuntimeException("Failed to Delete Actor", e);
		}
	}


	public boolean actorExists(String actorId) {
	    try (Session session = driver.session()) {
			final String checkStatement = "MATCH (a:actor {id: \"" + actorId +"\"}) RETURN a";
	        return session.run(checkStatement, Values.parameters("actorId", actorId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	public boolean movieExists(String movieId) {
	    try (Session session = driver.session()) {
			final String checkStatement = "MATCH (m:movie {id: \"" + movieId +"\"}) RETURN m";
	        return session.run(checkStatement, Values.parameters("movieId", movieId)).hasNext();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean hasRelationship(String actorId, String movieId){
		try(Session session = driver.session()){
			String checkQuery = "MATCH (a:actor {id: \"" + actorId + "\"})-[r:ACTED_IN]->(m:movie {id: \"" + movieId + "\"}) RETURN r";
			if (session.run(checkQuery, Values.parameters("actorId", actorId, "movieId", movieId)).hasNext()) {
	            return true; // Relationship already exists
	        }
			return false;
		}
	}

	public String deleteRelationship(String actorId, String movieId){
		try(Session session = driver.session()){
			String checkQuery = "MATCH (a:actor {id: \"" + actorId + "\"})-[r:ACTED_IN]->(m:movie {id: \"" + movieId + "\"}) DELETE r";
			session.run(checkQuery, Values.parameters("actorId", actorId, "movieId", movieId));
		}catch (Exception e){
			e.printStackTrace();
	        return null;
		}
		return "200";
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

	public String getActorName(String actorId){
		try(Session session = driver.session()){ 
			String checkQuery = "MATCH (a:actor) WHERE a.id = \"" + actorId + "\" RETURN a.name";
			StatementResult cursor = session.run(checkQuery, Values.parameters("actorId", actorId));
			return(cursor.single().get("a.name").asString());
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getMovieName(String movieId){
		try(Session session = driver.session()){ 
			final String checkQuery = "MATCH (m:movie {id: \"" + movieId +"\"}) RETURN m.name";
			String movieName;
			StatementResult cursor = session.run(checkQuery, Values.parameters("movieId", movieId));
			movieName = cursor.single().get("m.name").asString();
			return movieName;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getActorMovies(String actorId){
		List<String> movieNames = new ArrayList<>();
		try(Session session = driver.session()){ 
			String checkQuery = "MATCH (a:actor {id: \"" + actorId + "\"})-[r:ACTED_IN]-(b:movie) RETURN b.id";
			StatementResult cursor = session.run(checkQuery, Values.parameters("actorId", actorId));
			cursor.list().forEach(record -> movieNames.add(record.get("b.id").asString()));
			return movieNames.toArray(new String[0]);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Object> getActorOverview(String actorId){
		Map<String, Object> actorOverview = new LinkedHashMap<>();
		Map<String, String> movie = new LinkedHashMap<>();

		try(Session s1 = driver.session()){
			String basicInfo = "MATCH (a:actor {id:$actorId}) RETURN a.name as name";
			StatementResult basicInfoResult = s1.run(basicInfo, Values.parameters("actorId", actorId));
			if(basicInfoResult.hasNext()){
				Record record = basicInfoResult.next();
				actorOverview.put("name", record.get("name").asString());
			}
		}try(Session s2 = driver.session()){
			String moviesQuery = "MATCH (a:actor {id:$actorId})-[:ACTED_IN]->(m:movie) RETURN m.id as movieId, m.name as movieName";
			StatementResult moviesQueryResult = s2.run(moviesQuery, Values.parameters("actorId", actorId));
			List<Map<String, String>> moviesList = new ArrayList<>();
			while(moviesQueryResult.hasNext()){
				Record record = moviesQueryResult.next();
				movie.put("movieId", record.get("movieId").asString());
				movie.put("movieName", record.get("movieName").asString());
				moviesList.add(movie);
			}
			actorOverview.put("movies",moviesList);
		}try(Session s3 = driver.session()){
			String collaboratorsQuery = "MATCH (a:actor {id: $actorId})-[:ACTED_IN]->(m:movie)<-[:ACTED_IN]-(coactor:actor) RETURN DISTINCT coactor.id AS coactorId, coactor.name AS coactorName";
			StatementResult collaboratorsResult = s3.run(collaboratorsQuery, Values.parameters("actorId", actorId));
			List<Map<String, String>> collaboratorsList = new ArrayList<>();
			while (collaboratorsResult.hasNext()) {
                Record record = collaboratorsResult.next();
                Map<String, String> collaborator = new LinkedHashMap<>();
                collaborator.put("coactorId", record.get("coactorId").asString());
                collaborator.put("coactorName", record.get("coactorName").asString());
                collaboratorsList.add(collaborator);
            }
            actorOverview.put("collaborators", collaboratorsList);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

		return actorOverview;
	}

	public String[] getMovieActors(String movieId){
		List<String> actorNames = new ArrayList<String>();
		try(Session session = driver.session()){
			String checkQuery = "MATCH (m:movie {id: \"" + movieId + "\"})-[r:ACTED_IN]-(a:actor) RETURN a.id";
			StatementResult cursor = session.run(checkQuery, Values.parameters("movieId", movieId));
			cursor.list().forEach(record -> actorNames.add(record.get("a.id").asString()));
			return actorNames.toArray(new String[0]);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public int getBaconNumber(String actorId){
			if(actorId.equals("nm0000102"))//If Kevin Bacon Querried return 0
				return 0;
		 try (Session session = driver.session()) { //Compute the path to kevin and devide by 2 in order to not double count
            String query = "MATCH p = shortestPath((a1:actor {id: \"" + actorId + "\"})-[*]-(a2:actor {id: 'nm0000102'})) RETURN length(p) / 2 AS degree";
            StatementResult result = session.run(query, Values.parameters("actorId", actorId));
			
            if (result.hasNext()) {
                Record record = result.next();
                int baconNumber = record.get("degree").asInt();
				return baconNumber;
            } else {
                System.out.println(actorId + " is not connected to Kevin Bacon.");
				return -1;
            }
        }
	}

	public Object[] getBaconPath(String actorId){
		
		if(actorId.equals("nm0000102"))//If Kevin Bacon Querried return 0
			return new String[0];
	 try (Session session = driver.session()) { //Compute the path to kevin and devide by 2 in order to not double count
		String query = "MATCH p = shortestPath((a1:actor {id: \"" + actorId + "\"})-[*]-(a2:actor {id: 'nm0000102'})) RETURN p AS path";
		StatementResult result = session.run(query, Values.parameters("actorId", actorId));
		
		if (result.hasNext()) {
			Record record = result.next();
			Path baconPath = record.get("path").asPath();
			List<String> actorIds = new ArrayList<>();
            for (Node node : baconPath.nodes()) {
                actorIds.add(node.get("id").asString());
            }

			return actorIds.toArray();
		} else {
			System.out.println(actorId + " is not connected to Kevin Bacon.");
			return new String[0];
			}
		}
	}

	public List<List<String>> getActorPairFreq() {
    List<List<String>> actorPairs = new ArrayList<>();

    try (Session session = driver.session()) {
        StatementResult result = session.run("MATCH (a1:actor)-[:ACTED_IN]->(m:movie)<-[:ACTED_IN]-(a2:actor) " +
                                             "WHERE id(a1) < id(a2) " +
                                             "RETURN [a1.id, a2.id] AS actor_pair, count(m) AS movies_together " + 
											 "LIMIT 1");

        while (result.hasNext()) {
            Record record = result.next();
            List<String> actorPair = record.get("actor_pair").asList(Value::asString);
            actorPairs.add(actorPair);
        }
    }

    return actorPairs;
}


	public List<String> shortestPath(String actor1, String actor2){
		List<String> path = new ArrayList<>();
		try(Session session = driver.session()){
			String query =  "MATCH (a:actor {id: $actor1}), (b:actor {id: $actor2}), " +
               				"p = shortestPath((a)-[*]-(b)) " +
               				"RETURN [n in nodes(p) | n.id] AS path";

			StatementResult result = session.run(query, Values.parameters("actor1", actor1, "actor2", actor2));

			if(result.hasNext()){
				Record record = result.next();
				List<Object> pathObjects = record.get("path").asList();
				for(Object o : pathObjects){
					if(o instanceof String){
						path.add((String) o);
					}else{
						System.out.println("Unexpected type in path: " + o.getClass().getName());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return path;
	}
}


