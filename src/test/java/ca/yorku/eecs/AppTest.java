package ca.yorku.eecs;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testaddActorPass(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addActor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"nm6767\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int codeCon = con.getResponseCode();
            System.out.println("Add Actor Pass gave " + codeCon);

            URL urlDel = new URL("http://localhost:8080/api/v1/deleteActor?actorId=nm6767");
            HttpURLConnection conDel = (HttpURLConnection) urlDel.openConnection();
            conDel.setRequestMethod("DELETE");
            conDel.setRequestProperty("Accept", "application/json");
            int code = conDel.getResponseCode();
            System.out.println("Deleted Actor " + code);

            assertEquals(200, codeCon);

        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddMoviePass(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addMovie");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"salt\", \"movieId\": \"nm1212\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int codeCon = con.getResponseCode();
            System.out.println("Add Movie Pass gave " + codeCon);

            URL urlDel = new URL("http://localhost:8080/api/v1/deleteMovie?movieId=nm1212");
            HttpURLConnection conDel = (HttpURLConnection) urlDel.openConnection();
            conDel.setRequestMethod("DELETE");
            conDel.setRequestProperty("Accept", "application/json");
            int code = conDel.getResponseCode();
            System.out.println("Deleted Movie " + code);
            assertEquals(200, codeCon);

        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddActorFail(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addActor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"nm00001111\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Actor Fail gave " + code);
            assertEquals(500, code);
        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddMovieFail(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addMovie");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"nm11\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Actor Fail gave " + code);
            assertEquals(500, code);
        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddRelationshipPass(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addRelationship");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"actorId\": \"nm11\", \"movieId\": \"nm101010111\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Relationship Pass gave " + code);
            assertEquals(200, code);
        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddRelationshipFail(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addRelationship");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"actorId\": \"nm11\", \"movieId\": \"nm101010111\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Relationship Fail gave " + code);
            assertEquals(500, code);
        }catch(IOException e){
            // e.printStackTrace();
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testgetActorPass() {
        try {
            // Append the actorId as a query parameter
            URL url = new URL("http://localhost:8080/api/v1/getActor?actorId=nm10101011");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            System.out.println("Get Actor Pass Gave " + code);
            assertEquals(200, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testgetActorFail() {
        try {
            // Append the actorId as a query parameter
            URL url = new URL("http://localhost:8080/api/v1/getActor?actorId=nm128201011011");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            System.out.println("Get Actor Pass Gave " + code);
            assertEquals(404, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
    public void testgetMoviePass() {
        try {
            // Append the movieId as a query parameter
            URL url = new URL("http://localhost:8080/api/v1/getMovie?movieId=nm101010111");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            System.out.println("Get Movie Pass Gave " + code);
            assertEquals(200, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testgetMovieFail() {
        try {
            // Append the movieId as a query parameter
            URL url = new URL("http://localhost:8080/api/v1/getMovie?movieId=nm101012330111");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            System.out.println("Get Movie Pass Gave " + code);
            assertEquals(404, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
}
