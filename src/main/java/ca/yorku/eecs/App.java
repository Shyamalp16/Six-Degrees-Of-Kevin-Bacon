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
    			}
    			else if(path.equals("/api/v1/addMovie")) {
    				res = handleAddMovie(t);
    				statusCode = Integer.parseInt(res);
    			}else {
    				res = "Invalid Path";
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
            if(name.isEmpty() || id.isEmpty() || !id.matches("\\d+")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 500
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
            if(name.isEmpty() || id.isEmpty() || !id.matches("\\d+")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 500
            responseCode = nb.insertMovie(name, id);
            return responseCode;
    	}
    }
}
