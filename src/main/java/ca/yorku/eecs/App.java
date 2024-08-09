package ca.yorku.eecs;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
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
			String method = t.getRequestMethod();
    		String res = "";
    		int statusCode = 200;
            
    		try {
    			if(path.equals("/api/v1/addActor") && method.equals("PUT")) {
    				res = handleAddActor(t);
    				statusCode = Integer.parseInt(res);
    			}else if(path.equals("/api/v1/deleteActor") && method.equals("DELETE")){
					res = handleDeleteActor(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/addMovie") && method.equals("PUT")) {
    				res = handleAddMovie(t);
    				statusCode = Integer.parseInt(res);
    			}else if(path.equals("/api/v1/deleteMovie") && method.equals("DELETE")){
					res = handleDeleteMovie(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/addRelationship") && method.equals("PUT")){
					res = handleAddRelationship(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/deleteRelationship") && method.equals("DELETE")){
					res = handleDeleteRelationship(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/getActor") && method.equals("GET")){
					res = handleGetActor(t);
					if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
				}else if(path.equals("/api/v1/getMovie") && method.equals("GET")){
					res = handleGetMovie(t);
					if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
					System.out.println(statusCode);
				}else if(path.equals("/api/v1/hasRelationship") && method.equals("GET")){
					res = handleHasRelationship(t);
					// AT THIS POINT ONLY IF NO MOVIE/ACTOR EXISTS, WE WILL GET A 404 OTHERWISE IT WILL BE 200 BECAUSE WE ARE CHECKING FOR 400 OUTSIDE OF THE FUNCTION SO NO NEED TO PARSE ALL THE CODES 200 RESPONSE WILL BE DIFFERENT 
					if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
				}else if(path.equals("/api/v1/computeBaconNumber") && method.equals("GET")) {
    				res = computeBaconNumber(t);
    				if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
    			}else if(path.equals("/api/v1/computeBaconPath") && method.equals("GET")){
					res = computeBaconPath(t);
    				if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
				}else if(path.equals("/api/v1/mostFrequent") && method.equals("GET")){
					res = handleMostFreq(t);
					if(res == "404"){
						statusCode = Integer.parseInt(res);
					}
				}else {
    				res = "Invalid Path or Method";
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
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("actorId");
            String responseCode;
            
//          IF INVALID REQUEST BODY 
            if(name.isEmpty() || id.isEmpty() || !id.matches("^nm\\d+$")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 400

			if(nb.actorExists(id)){
				return "400";
			}
            responseCode = nb.insertActor(name, id);
            return responseCode;
    	}
    	
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
//    		ADD THE IF ALREADY IN DATABASE, RETURN 400
			if(nb.movieExists(id)){
				return "400";
			}
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

		private String handleDeleteRelationship(HttpExchange t) throws JSONException, IOException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String movieId = null;
			String actorId = null;
			String res = "";
			int statusCode = 0;

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("actorId")){
						actorId = pair[1];
					}else if(pair.length == 2 && pair[0].equals("movieId")){
						movieId = pair[1];
						break;
					}
				}
			}

			if(actorId == null || movieId == null){
				res = "Invalid Body";
				statusCode = 400;
				t.sendResponseHeaders(statusCode, res.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(res.getBytes());
				os.close();
				return null;
			}

			if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
				return "404";
			}

			if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
				return "404";	
			}

			if(!nb.hasRelationship(actorId, movieId)){
				return "404";
			}

			res = nb.deleteRelationship(actorId, movieId);
			return res;
		}
		private String handleHasRelationship(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    	    String query = t.getRequestURI().getQuery();
			String actorId = null;
			String movieId = null;
			int statusCode;
			String res = "";

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("actorId")){
						actorId = pair[1];
					}else if(pair.length == 2 && pair[0].equals("movieId")){
						movieId = pair[1];
						break;
					}
				}
			}

			if(actorId == null || movieId == null){
				res = "INVALD BODY";
				statusCode = 400;
                t.sendResponseHeaders(statusCode, res.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(res.getBytes());
                os.close();
                return null;
			}
			
    	    

			if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
    	        return "404";
    	    }

			if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
    	        return "404";	
    	    }

			boolean hasRel = nb.hasRelationship(actorId, movieId);
			JSONObject resObj = new JSONObject();
			resObj.put("actorId", actorId);
			resObj.put("movieId", movieId);
			resObj.put("hasRelationship", hasRel);
			if(!hasRel){
				return "404";
			}
			res = resObj.toString();
			return res;
		}

		private String handleDeleteMovie(HttpExchange t) throws IOException, JSONException{
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String movieId = null;
			String res = "";
			int statusCode = 0;

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("movieId")){
						movieId = pair[1];
						break;
					}
				}
			}

			if(movieId == null){
				res = "INVALID BODY";
				statusCode = 400;
				t.sendResponseHeaders(statusCode, res.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(res.getBytes());
				os.close();
				return null;
			}

			if(movieId.isEmpty() || !movieId.matches("^nm\\d+$")){
				System.out.println("IS EMPTY");
				return "404";
			}

			if(!nb.movieExists(movieId)){
				System.out.println("DOESNT EXIST");
				return "404";
			}

			res = nb.deleteMovie(movieId);
			System.out.println("ENTERS HERE");
			return res;
		}

		private String handleDeleteActor(HttpExchange t) throws IOException, JSONException{
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";
			int statusCode = 0;

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("actorId")){
						actorId = pair[1];
						break;
					}
				}
			}

			if(actorId == null){
				res = "INVALID BODY";
				statusCode = 400;
				t.sendResponseHeaders(statusCode, res.getBytes().length);
				OutputStream os = t.getResponseBody();
				os.write(res.getBytes());
				os.close();
				return null;
			}

			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")){
				return "404";
			}

			if(!nb.actorExists(actorId)){
				return "404";
			}

			res = nb.deleteActor(actorId);
			return res;
		}

		private String handleGetActor(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    		String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";
			int statusCode;

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("actorId")){
						actorId = pair[1];
						break;
					}
				}
			}

			if(actorId == null){
				res = "INVALD BODY";
				statusCode = 400;
                t.sendResponseHeaders(statusCode, res.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(res.getBytes());
                os.close();
                return null;
			}


			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				System.out.println("APPARTENLY ITS EMPTY?");
				return "404";
			}

			if(!nb.actorExists(actorId)){
				System.out.println("APPARTENLY ITS DOESNT EXIST?");
				return "404";
			}

			String actorName = nb.getActorName(actorId);
			String[] movieIds = nb.getActorMovies(actorId);

			Map<String, Object> resMap = new LinkedHashMap<>();
			resMap.put("actorId", actorId);
			resMap.put("name", actorName);
			resMap.put("movies", movieIds);

			JSONObject resObj = new JSONObject(resMap);
			res = resObj.toString();
			System.out.println(res);
			return res;
		}

		private String handleGetMovie(HttpExchange t)  throws IOException, JSONException{
			Connection nb = new Connection();
    		String query = t.getRequestURI().getQuery();
			String movieId = null;
			String res = "";
			int statusCode;

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("movieId")){
						movieId = pair[1];
						break;
					}
				}
			}

			if(movieId == null){
				res = "INVALD BODY";
				statusCode = 400;
                t.sendResponseHeaders(statusCode, res.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(res.getBytes());
                os.close();
                return null;
			}

			System.out.println(movieId);

			if(movieId.isEmpty() || !movieId.matches("^nm\\d+$")) {
				System.out.println("APPARENTLY ITS EMPTY");
				return "404";
			}

			if(!nb.movieExists(movieId)){
				System.out.println("MOvie doesnt exist");
				return "404";
			}

			String movieName = nb.getMovieName(movieId);
			String[] actorIds = nb.getMovieActors(movieId);

			Map<String, Object> resMap = new LinkedHashMap<>();
			resMap.put("movieId", movieId);
			resMap.put("name", movieName);
			resMap.put("actors", actorIds);

			JSONObject resObj = new JSONObject(resMap);
			res = resObj.toString();
			System.out.println(res);

			return res;
		}
		
		private String computeBaconNumber(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			JSONObject jsonObject = checkBody(t);
            String actorId = jsonObject.getString("actorId");
			String res = "";
			
			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				return "404";
			}

			if(!nb.actorExists(actorId)){
				System.out.println("APPARTENLY ITS DOESNT EXIST?");
				return "404";
			}
			
			int baconNumber = nb.getBaconNumber(actorId);
			JSONObject resObj = new JSONObject();
			resObj.put("baconNumber", baconNumber);
			res = resObj.toString();
			System.out.println(res);

			return res;
		}

		private String computeBaconPath(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			JSONObject jsonObject = checkBody(t);
            String actorId = jsonObject.getString("actorId");
			String res = "";

			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				return "404";
			}
			Object[] baconPath = nb.getBaconPath(actorId);
			JSONObject resObj = new JSONObject();
			resObj.put("baconPath", baconPath);
			res = resObj.toString();
			System.out.println(res);

			return res;

		}
	
		private String handleMostFreq(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			Map<String, Integer> pairFreq = nb.getActorPairFreq();
			List<String> mostFreqActors = new ArrayList<>();
			
			if(pairFreq.isEmpty()){
				System.out.println("EMPTY");
				return "404";
				// return mostFreqActors;
			}

			int maxFreq = Collections.max(pairFreq.values());
			for(Map.Entry<String, Integer> e: pairFreq.entrySet()){
				if(e.getValue() == maxFreq){
					String[] actors = e.getKey().split("-");
					mostFreqActors.add(actors[0]);
					mostFreqActors.add(actors[1]);
				}
			}
			System.out.println(mostFreqActors);
			return "200";
		}
	}
}