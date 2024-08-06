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

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"nm101011011\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Actor Pass gave " + code);
            assertEquals(200, code);

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

            String jsonInputString = "{\"name\": \"salt\", \"movieId\": \"nm101010111\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Movie Pass gave " + code);
            assertEquals(200, code);

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
}
