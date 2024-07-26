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
        
        Connection nb = new Connection();
        nb.insertAuthor("HUIHUIHU");
    }
    
    static class MyHandler implements HttpHandler{
    	@Override
    	public void handle(HttpExchange t) throws IOException{
//    		SETUP VARIABLES
    		String path = t.getRequestURI().getPath();
    		String res = "";
    		int statusCode = 200;
    		
//    		GET REQUEST BODY IN JSON FORMAT
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
                return;
			}
            
    		try {
    			if(path.equals("/api/v1/addActor")) {
    				res = handleAddActor(jsonObject);
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
    	
    	private String handleAddActor(JSONObject jsonObject) throws JSONException{
            
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("actorId");
            
//          IF INVALID REQUEST BODY 
            if(name.isEmpty() || id.isEmpty() || !id.matches("\\d+")) {
            	return "400";
            }
//    		ADD THE IF ALREADY IN DATABASE, RETURN 500
            return "200";
    	}
    }
}
