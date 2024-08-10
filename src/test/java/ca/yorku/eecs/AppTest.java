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
            assertEquals(400, code);
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

            String jsonInputString = "{\"actorId\": \"nm101011011\", \"movieId\": \"nm2323\"}";
            System.out.println(jsonInputString);
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Add Relationship Pass gave " + code);

            URL urlDel = new URL("http://localhost:8080/api/v1/deleteRelationship?actorId=nm101011011&movieId=nm2323");
            HttpURLConnection conDel = (HttpURLConnection) urlDel.openConnection();
            conDel.setRequestMethod("DELETE");
            conDel.setRequestProperty("Accept", "application/json");
            int codeDel = conDel.getResponseCode();
            System.out.println("Deleted Relaionship " + codeDel);
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
            assertEquals(400, code);
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
    
    public void testhasRelationshipPass(){
        //todo
    }

    public void testHasRelationshipFail(){
        //todo
    }

    public void testcomputeBaconNumberPass(){
        try{
            URL url = new URL("http://localhost:8080/api/v1/addActor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"John Cena\", \"actorId\": \"nm313131313\"}";
            System.out.println(jsonInputString);
             try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            //Inserted an actor John Cena

            HttpURLConnection conKev = (HttpURLConnection) url.openConnection();
            conKev.setRequestMethod("PUT");
            conKev.setDoOutput(true);
            conKev.setRequestProperty("Content-Type", "application/json; utf-8");
            conKev.setRequestProperty("Accept", "application/json");

            String jsonInputStringKev = "{\"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\"}";
            System.out.println(jsonInputStringKev);
             try(OutputStream osKev = conKev.getOutputStream()){
                byte[] inputKev = jsonInputStringKev.getBytes(StandardCharsets.UTF_8);
                osKev.write(inputKev, 0, inputKev.length);
            }
            //Inserted Kevin Bacon



              URL urlmovie = new URL("http://localhost:8080/api/v1/addMovie");
            HttpURLConnection conmovie = (HttpURLConnection) urlmovie.openConnection();
            
            conmovie.setRequestMethod("PUT");
            conmovie.setDoOutput(true);
            conmovie.setRequestProperty("Content-Type", "application/json; utf-8");
            conmovie.setRequestProperty("Accept", "application/json");

            String jsonInputStringMovie = "{\"name\": \"SuperBad\", \"movieId\": \"nm200300400\"}";
            System.out.println(jsonInputStringMovie);
            try(OutputStream osMovie = conmovie.getOutputStream()){
                byte[] inputMovie = jsonInputStringMovie.getBytes(StandardCharsets.UTF_8);
                osMovie.write(inputMovie, 0, inputMovie.length);
            }
            //Inserted Movie

            URL urlRelKev = new URL("http://localhost:8080/api/v1/addRelationship");
            HttpURLConnection conRelKev = (HttpURLConnection) urlRelKev.openConnection();
            conRelKev.setRequestMethod("PUT");
            conRelKev.setDoOutput(true);
            conRelKev.setRequestProperty("Content-Type", "application/json; utf-8");
            conRelKev.setRequestProperty("Accept", "application/json");

            String jsonInputStringRelKev = "{\"actorId\": \"nm0000102\", \"movieId\": \"nm200300400\"}";
            System.out.println(jsonInputStringRelKev);
            try(OutputStream osRelKev = conRelKev.getOutputStream()){
                byte[] inputRelKev = jsonInputStringRelKev.getBytes(StandardCharsets.UTF_8);
                osRelKev.write(inputRelKev, 0, inputRelKev.length);
            }  
            //Inserted Relationship between Kevin Bacon and John Cena through 'SuperBad' movie (Kevin bacon -> Acted in -> Superbad)


            URL urlRelJohn = new URL("http://localhost:8080/api/v1/addRelationship");
            HttpURLConnection conRelJohn = (HttpURLConnection) urlRelJohn.openConnection();
            conRelJohn.setRequestMethod("PUT");
            conRelJohn.setDoOutput(true);
            conRelJohn.setRequestProperty("Content-Type", "application/json; utf-8");
            conRelJohn.setRequestProperty("Accept", "application/json");

            String jsonInputStringRelJohn = "{\"actorId\": \"nm313131313\", \"movieId\": \"nm200300400\"}";
            System.out.println(jsonInputStringRelJohn);
            try(OutputStream osRelJohn = conRelJohn.getOutputStream()){
                byte[] inputRelJohn = jsonInputStringRelJohn.getBytes(StandardCharsets.UTF_8);
                osRelJohn.write(inputRelJohn, 0, inputRelJohn.length);
            }  
            //Inserted Relationship between Kevin Bacon and John Cena through 'SuperBad' movie (John Cena -> Acted in -> Superbad)
            
            URL urlBacon = new URL("http://localhost:8080/api/v1/computeBaconNumber?actorId=nm313131313");
             HttpURLConnection conBacon = (HttpURLConnection) urlBacon.openConnection();
            conBacon.setRequestMethod("GET");
            conBacon.setRequestProperty("Accept", "application/json");
    
            int code = conBacon.getResponseCode();
            
            System.out.println("Get Bacon Number gave: " + code);
            assertEquals(200, code);
            //Here the response body could be checked if its equal to {baconNumber: 1} as an additional check
        }catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testcomputeBaconNumberFail(){
        //previous test inserted nodes, this code uses those nodes. However the nodes could be re-added here with their code
        try{
        URL urlBacon = new URL("http://localhost:8080/api/v1/computeBaconNumber?actorId=nm000000");//Un-existant actor in database
        HttpURLConnection conBacon = (HttpURLConnection) urlBacon.openConnection();
        conBacon.setRequestMethod("GET");
        conBacon.setRequestProperty("Accept", "application/json");

        int code = conBacon.getResponseCode();
        
        System.out.println("Get Bacon Number gave: " + code);
        assertEquals(404, code);        
        }catch(Exception e){
            fail("Exception Occured: " + e);
        }
        
    }


    public void testComputeBaconPathPass(){
        //Uses the nodes from testComputeBaconNumber, however code can be added here again if test is going to be runned alone
        try{
             URL urlBacon = new URL("http://localhost:8080/api/v1/computeBaconPath?actorId=nm313131313");
             HttpURLConnection conBacon = (HttpURLConnection) urlBacon.openConnection();
            conBacon.setRequestMethod("GET");
            conBacon.setRequestProperty("Accept", "application/json");
    
            int code = conBacon.getResponseCode();
            
            System.out.println("Get Bacon Path gave: " + code);
            assertEquals(200, code);
            //Here the response body could be checked if its equal to {baconPath: nm0000102 nm200300400 nm313131313} as an additional check
        }catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }

    }


    public void testComputeBaconPathFail(){
        //Uses the nodes from testComputeBaconNumber, however code can be added here again if test is going to be runned alone
        try{
             URL urlBacon = new URL("http://localhost:8080/api/v1/computeBaconPath?actorId=nm00000");//Actor that is not defined in the database
             HttpURLConnection conBacon = (HttpURLConnection) urlBacon.openConnection();
            conBacon.setRequestMethod("GET");
            conBacon.setRequestProperty("Accept", "application/json");
    
            int code = conBacon.getResponseCode();
            
            System.out.println("Get Bacon Path gave: " + code);
            assertEquals(404, code);
        }catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }

    }
}

