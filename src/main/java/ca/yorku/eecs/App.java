package ca.yorku.eecs;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray;
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
    		String res = "";
    		int statusCode = 200;
    		String body = new BufferedReader(new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            JSONObject jsonObject;
            try {
            	jsonObject = new JSONObject(body);
			} catch (JSONException e1) {
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
    
		public void returnResponse(HttpExchange t, int statusCode, String res) throws IOException, JSONException {
			t.sendResponseHeaders(statusCode, res.getBytes().length);
			OutputStream os = t.getResponseBody();
			os.write(res.getBytes());
			os.close();
		}

		@Override
    	public void handle(HttpExchange t) throws IOException{
    		String path = t.getRequestURI().getPath();
			String method = t.getRequestMethod();
    		String res = "";
    		int statusCode = 200;
            
    		try {
    			if(path.equals("/api/v1/addActor") && method.equals("PUT")) {
    				res = handleAddActor(t);
    				statusCode = Integer.parseInt(res);
    			}else if(path.equals("/api/v1/deleteActor") && method.equals("DELETE")){
					handleDeleteActor(t);
				}else if(path.equals("/api/v1/addMovie") && method.equals("PUT")) {
    				res = handleAddMovie(t);
    				statusCode = Integer.parseInt(res);
    			}else if(path.equals("/api/v1/deleteMovie") && method.equals("DELETE")){
					handleDeleteMovie(t);
				}else if(path.equals("/api/v1/addRelationship") && method.equals("PUT")){
					res = handleAddRelationship(t);
					statusCode = Integer.parseInt(res);
				}else if(path.equals("/api/v1/deleteRelationship") && method.equals("DELETE")){
					handleDeleteRelationship(t);
				}else if(path.equals("/api/v1/getActor") && method.equals("GET")){
					handleGetActor(t);
				}else if(path.equals("/api/v1/getMovie") && method.equals("GET")){
					handleGetMovie(t);
				}else if(path.equals("/api/v1/hasRelationship") && method.equals("GET")){
					handleHasRelationship(t);
				}else if(path.equals("/api/v1/computeBaconNumber") && method.equals("GET")) {
    				computeBaconNumber(t);
    			}else if(path.equals("/api/v1/computeBaconPath") && method.equals("GET")){
					computeBaconPath(t);
				}else if(path.equals("/api/v1/mostFrequent") && method.equals("GET")){
					handleMostFreq(t);
				}else if(path.equals("/api/v1/shortestPath") && method.equals("GET")){
					handleShortestPath(t);
				}else if(path.equals("/api/v1/getActorOverview") && method.equals("GET")){
					handleActorOverview(t);
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
            
            if(name.isEmpty() || id.isEmpty() || !id.matches("^nm\\d+$")) {
            	return "400";
            }

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
            
            if(name.isEmpty() || id.isEmpty() || !id.matches("^nm\\d+$")) {
            	return "400";
            }

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

    	    if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
    	        responseCode = "404";
				return responseCode;
    	    }

    	    boolean added = nb.addRelationship(actorId, movieId);
    	    if (!added) {
    	        responseCode = "400";
				return responseCode;
    	    }
    	    responseCode = "200";
			return responseCode;
    	}

		private void handleDeleteRelationship(HttpExchange t) throws JSONException, IOException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String movieId = null;
			String actorId = null;
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
				returnResponse(t, 400, "Invalid Body");
			}

			if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
				returnResponse(t, 400, "Invalid Body");
			}

			if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
				returnResponse(t, 404, "Actor or Movie Doesnt Exist");
			}

			if(!nb.hasRelationship(actorId, movieId)){
				returnResponse(t, 404, "No relationships exist");
			}

			res = nb.deleteRelationship(actorId, movieId);
			returnResponse(t, 200, res);
		}
		
		private void handleHasRelationship(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    	    String query = t.getRequestURI().getQuery();
			String actorId = null;
			String movieId = null;
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
				returnResponse(t, 400, "Invalid Body");
			}
			
    	    

			if (actorId.isEmpty() || movieId.isEmpty() || !actorId.matches("^nm\\d+$") || !movieId.matches("^nm\\d+$")) {
    	        returnResponse(t, 404, "Invalid Body");
    	    }

			if (!nb.actorExists(actorId) || !nb.movieExists(movieId)) {
    	        returnResponse(t, 404, "Actor Doesnt Exist");
    	    }

			boolean hasRel = nb.hasRelationship(actorId, movieId);
			JSONObject resObj = new JSONObject();
			resObj.put("actorId", actorId);
			resObj.put("movieId", movieId);
			resObj.put("hasRelationship", hasRel);
			if(!hasRel){
				returnResponse(t, 404, "No Relationship Exists");
			}
			res = resObj.toString();
			returnResponse(t, 200, res);
		}

		private void handleDeleteMovie(HttpExchange t) throws IOException, JSONException{
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String movieId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}

			if(movieId.isEmpty() || !movieId.matches("^nm\\d+$")){
				returnResponse(t, 400, "Invalid Body");
			}

			if(!nb.movieExists(movieId)){
				returnResponse(t, 404, "Movie Doesnt Exist");
			}

			res = nb.deleteMovie(movieId);
			returnResponse(t, 200, res);
		}

		private void handleDeleteActor(HttpExchange t) throws IOException, JSONException{
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}

			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")){
				returnResponse(t, 400, "Invalid Body");
			}

			if(!nb.actorExists(actorId)){
				returnResponse(t, 404, "Actor Doesnt Exist");
			}

			res = nb.deleteActor(actorId);
			returnResponse(t, 200, res);
		}

		private void handleGetActor(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
    		String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}


			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				returnResponse(t, 404, "Invalid Body");
			}

			if(!nb.actorExists(actorId)){
				returnResponse(t, 404, "Actor Doesnt Exist");
			}

			String actorName = nb.getActorName(actorId);
			String[] movieIds = nb.getActorMovies(actorId);

			Map<String, Object> resMap = new LinkedHashMap<>();
			resMap.put("actorId", actorId);
			resMap.put("name", actorName);
			resMap.put("movies", movieIds);

			JSONObject resObj = new JSONObject(resMap);
			res = resObj.toString();
			// System.out.println(res);
			returnResponse(t, 200,res);
		}

		private void handleActorOverview(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";

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
				res = "Invalid Body";
				returnResponse(t, 400, "Invalid Body");
			}

			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")){
				returnResponse(t, 404, "Invalid Body");
			}

			if(!nb.actorExists(actorId)){
				returnResponse(t, 404, "Actor Not Found");
			}

			Map<String, Object> actorOverview = nb.getActorOverview(actorId);
			JSONObject resObj = new JSONObject(actorOverview);
			res = resObj.toString();
			
			returnResponse(t, 200, res);
		}

		private void handleGetMovie(HttpExchange t)  throws IOException, JSONException{
			Connection nb = new Connection();
    		String query = t.getRequestURI().getQuery();
			String movieId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}


			if(movieId.isEmpty() || !movieId.matches("^nm\\d+$")) {
				returnResponse(t, 404, "Invalid Body");
			}

			if(!nb.movieExists(movieId)){
				returnResponse(t, 404, "Movie Doesnt Exist");
			}

			String movieName = nb.getMovieName(movieId);
			String[] actorIds = nb.getMovieActors(movieId);

			Map<String, Object> resMap = new LinkedHashMap<>();
			resMap.put("movieId", movieId);
			resMap.put("name", movieName);
			resMap.put("actors", actorIds);

			JSONObject resObj = new JSONObject(resMap);
			res = resObj.toString();
			returnResponse(t, 200, res);
		}
		
		private void computeBaconNumber(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}
			
			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				returnResponse(t, 404, "Invalid Body");
			}

			if(!nb.actorExists(actorId)){
				returnResponse(t, 404, "Actor doesnt exist");
			}
			
			int baconNumber = nb.getBaconNumber(actorId);
			JSONObject resObj = new JSONObject();
			resObj.put("baconNumber", baconNumber);
			res = resObj.toString();
			returnResponse(t, 200,res);
		}

		private String computeBaconPath(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actorId = null;
			String res = "";

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
				returnResponse(t, 400, "Invalid Body");
			}

			if(actorId.isEmpty() || !actorId.matches("^nm\\d+$")) {
				return "404";
			}

			Object[] baconPath = nb.getBaconPath(actorId);
			JSONObject resObj = new JSONObject();
			resObj.put("baconPath", baconPath);
			res = resObj.toString();
			System.out.println(res.split(":")[1]);
			if(res.split(":")[1].equals("[]}")){
				returnResponse(t, 404, "No Path Found");
			}
			System.out.println(res);
			return res;
		}

		private void handleMostFreq(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			List<List<String>> actorPairs = nb.getActorPairFreq();
			List<List<String>> mostFreqActors = new ArrayList<>();
			String res = "";
			
			if(actorPairs.isEmpty()){
				returnResponse(t, 404, "No pairs exist");
			}

			Map<List<String>, Integer> freqMap = new HashMap<>();
			for(List<String> pair: actorPairs){
				freqMap.put(pair, freqMap.getOrDefault(pair, 0)+ 1);
			}

			int maxFreq = Collections.max(freqMap.values());

			for (Map.Entry<List<String>, Integer> entry : freqMap.entrySet()) {
				if (entry.getValue() == maxFreq) {
					mostFreqActors.add(entry.getKey());
				}
			}

			JSONArray jsonArray = new JSONArray();
   			 for (List<String> pair : mostFreqActors) {
        		JSONArray jsonPair = new JSONArray();
        		for (String actorId : pair) {
            		jsonPair.put(actorId);
        		}
        	jsonArray.put(jsonPair);
    		}	

			res = jsonArray.toString();
			returnResponse(t, 200,res);
		}

		private void handleShortestPath(HttpExchange t) throws IOException, JSONException {
			Connection nb = new Connection();
			String query = t.getRequestURI().getQuery();
			String actor1Id = null;
			String actor2Id = null;
			String res = "";

			if(query != null){
				for(String param: query.split("&")){
					String[] pair = param.split("=");
					if(pair.length == 2 && pair[0].equals("actorId1")){
						actor1Id = pair[1];
					}else if(pair.length == 2 && pair[0].equals("actorId2")){
						actor2Id = pair[1];
						break;
					}
				}
			}

			if (actor1Id.isEmpty() || actor2Id.isEmpty() || !actor1Id.matches("^nm\\d+$") || !actor2Id.matches("^nm\\d+$")) {
				returnResponse(t, 404, "Invalid Body");
    	    }

			if(!nb.actorExists(actor1Id) || !nb.actorExists(actor2Id)){
				returnResponse(t, 404, "Actor Not Found");
			}

			List<String> path = nb.shortestPath(actor1Id, actor2Id);
			if(path == null || path.isEmpty()) {
				returnResponse(t, 404, "No path exists");
			}

			JSONArray jsonPath = new JSONArray();
			for(String nodeId : path){
				jsonPath.put(nodeId);
			}

			res = jsonPath.toString();
			returnResponse(t, 200, res);
		}

	}
}