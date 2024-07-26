package ca.yorku.eecs;

import static org.neo4j.driver.v1.Values.parameters;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

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
	
	public void insertAuthor(String author) {
		final String statementTemplate = "MERGE (a:Author {author: $x})";
		final String keysAndValues = "x";
		try(Session session = driver.session()){
			session.writeTransaction(tx -> tx.run(statementTemplate, parameters(keysAndValues, author)));
		}
	}
}
