package ca.yorku.eecs;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import org.json.JSONException;
import org.json.JSONObject;



public class App 
{
    static int PORT = 8080;
    public static void main(String[] args) throws IOException
    {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }
    
    static class MyHandler implements HttpHandler{
    	
    	public JSONObject checkBody(HttpExchange t) throws IOException{
//    		GET REQUEST BODY IN JSON FORMAT
    		String res = "";
    		int statusCode = 200;
    		String body = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            System.out.println("Request Body: " + body);
            
//           PARSE JSON OBJECT
            JSONObject jsonObject;
            try {
            	jsonObject = new JSONObject(body);
			} catch (JSONException e1) {
//				if request body is incomplete, send statusCode as response
				res = "INVALD BODY";
				statusCode = 400;
                t.sendResponseHeaders(statusCode, res.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(res.getBytes());
                os.close();
                return null;
			}
            return jsonObject;
    	}
    	@Override
    	public void handle(HttpExchange t) throws IOException{
//    		SETUP VARIABLES
    		String path = t.getRequestURI().getPath();
    		String res = "";
    		int statusCode = 200;
    		System.out.println(path);
            
    		try {
    			if(path.equals("/api/v1/addActor")) {
    				res = handleAddActor(t);
    				statusCode = Integer.parseInt(res);
<<<<<<< Updated upstream
    			}else {
    				res = "Invalid Path";
=======
    			}
    			else if(path.equals("/api/v1/addMovie") && method.equals("PUT")) {
    				res = handleAddMovie(t);
    				statusCode = Integer.parseInt(res);
    			}else if(path.equals("/api/v1/addRelationship") && method.equals("PUT")){
					res = handleAddRelationship(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/getActor") && method.equals("GET")){
					res = handleGetActor(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/hasRelationship") && method.equals("GET")){
					res = handleHasRelationship(t);
					// AT THIS POINT ONLY IF NO MOVIE/ACTOR EXISTS, WE WILL GET A 404 OTHERWISE IT WILL BE 200 BECAUSE WE ARE CHECKING FOR 400 OUTSIDE OF THE FUNCTION SO NO NEED TO PARSE ALL THE CODES 200 RESPONSE WILL BE DIFFERENT 
					if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
				}else if(path.equals("/api/v1/computeBaconNumber") && method.equals("GET")) {
    				res = computeBaconNumber(t);
    				statusCode = Integer.parseInt(res);
    			}
				else {
    				res = "Invalid Path or Method";
>>>>>>> Stashed changes
    				t.sendResponseHeaders(404, res.getBytes().length);
    				OutputStream os = t.getResponseBody();
    				os.write(res.getBytes());
    				os.close();
    				return;
    			}
    		}catch(Exception e) {
    			res = "Internal Server Error";
    			statusCode = 500;
    		}
    		
    		
    		t.sendResponseHeaders(statusCode, res.getBytes().length);
    		OutputStream os = t.getResponseBody();
    		os.write(res.getBytes());
    		os.close();
    	}
    	
    	private String handleAddActor(HttpExchange t) throws JSONException, IOException{
    		Connection nb = new Connection();
    		JSONObject jsonObject = checkBody(t);
            String name = jsonObject.getString("actorName");
            String id = jsonObject.getString("actorId");
            
//          IF INVALID REQUEST BODY 
            if(name.isEmpty() || id.isEmpty() || !id.matches("\\d+")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 500
            nb.insertActor(name, id);
            return "200";
    	}
<<<<<<< Updated upstream
=======
    	
    	private String handleAddMovie(HttpExchange t) throws JSONException, IOException{
    		Connection nb = new Connection();
    		JSONObject jsonObject = checkBody(t);
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("movieId");
            String responseCode;
            
//          IF INVALID REQUEST BODY 
            if(name.isEmpty() || id.isEmpty() || !id.matches("^nm\\d+$")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 500
            responseCode = nb.insertMovie(name, id);
            return responseCode;
    	}
    	
    	private String handleAddRelationship(HttpExchange t) throws JSONException, IOException {
    	    Connection nb = new Connection();
    	    JSONObject jsonObject = checkBody(t);
			String responseCode;

    	    String actorId = jsonObject.getString("actorId");
    	    String movieId = jsonObject.getString("movieId");

    	    if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
    	        responseCode = "404";
				return responseCode;
    	    }

    	    // Check if the actor and movie exist in the database
    	    if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
    	        responseCode = "404";
				return responseCode;
    	    }

    	    // Add the relationship and check for uniqueness
    	    boolean added = nb.addRelationship(actorId, movieId);
    	    if (!added) {
    	        responseCode = "400";
				return responseCode; // If the relationship already exists
    	    }
    	    responseCode = "200";
			return responseCode;
    	}

		private String handleHasRelationship(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    	    JSONObject jsonObject = checkBody(t);
			String responseCode;
			String res = "";

			
    	    String actorId = jsonObject.getString("actorId");
    	    String movieId = jsonObject.getString("movieId");

			if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
    	        responseCode = "404";
				return responseCode;
    	    }

			if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
    	        responseCode = "404";
				return responseCode;
    	    }

			boolean hasRel = nb.hasRelationship(actorId, movieId);
			JSONObject resObj = new JSONObject();
			resObj.put("actorId", actorId);
			resObj.put("movieId", movieId);
			resObj.put("hasRelationship", hasRel);
			res = resObj.toString();
			return res;
		}

		private String handleGetActor(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    		JSONObject jsonObject = checkBody(t);
            String actorId = jsonObject.getString("actorId");
			System.out.println(actorId);

			if(actorId.isEmpty() || actorId.matches("^nm\\d+$")) {
				return "404";
			}

			if(!nb.actorExists(actorId)){
				return "404";
			}

			
			return "";
		}

		private String computeBaconNumber(HttpExchange t) throws JSONException, IOException{
			Connection nb = new Connection();
    		JSONObject jsonObject = checkBody(t);
            String actorId = jsonObject.getString("actorId");
			String res = "";
			System.out.println(actorId);


			if(actorId.isEmpty() || actorId.matches("^nm\\d+$")) return "404";

			if(!nb.actorExists(actorId)) return "404";
			
			int baconNumber = nb.computeBaconNumber(actorId);
			JSONObject resObj = new JSONObject();
			resObj.put("baconNumber", baconNumber);
			res = resObj.toString();
			return res;
		}
>>>>>>> Stashed changes
    }
}
