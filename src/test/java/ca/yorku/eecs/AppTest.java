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
            Connection nb = new Connection();
            URL url = new URL("http://localhost:8080/api/v1/addActor");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"nm6767\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int codeCon = con.getResponseCode();

            nb.deleteActor("nm6767");
            assertEquals(200, codeCon);

        }catch(IOException e){
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddMoviePass(){
        try{
            Connection nb = new Connection();
            URL url = new URL("http://localhost:8080/api/v1/addMovie");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"name\": \"salt\", \"movieId\": \"nm1212\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int codeCon = con.getResponseCode();

            nb.deleteMovie("nm1212");
            assertEquals(200, codeCon);

        }catch(IOException e){
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

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"00001111\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            assertEquals(400, code);
        }catch(IOException e){
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

            String jsonInputString = "{\"name\": \"John Doe\", \"actorId\": \"11\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            assertEquals(500, code);
        }catch(IOException e){
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testaddRelationshipPass(){
        try{
            Connection nb = new Connection();
            if(!nb.actorExists("nm100")){
                String r = nb.insertActor("Test Actor", "nm100");
            }

            if(!nb.movieExists("nm101")){
                String r = nb.insertMovie("Test Movie", "nm101");
            }
            
            if(nb.hasRelationship("nm100", "nm101")){
                String r = nb.deleteRelationship("nm100","nm101");
            }

            URL url = new URL("http://localhost:8080/api/v1/addRelationship");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            String jsonInputString = "{\"actorId\": \"nm100\", \"movieId\": \"nm101\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();

            URL urlDel = new URL("http://localhost:8080/api/v1/deleteRelationship?actorId=nm100&movieId=nm101");
            HttpURLConnection conDel = (HttpURLConnection) urlDel.openConnection();
            conDel.setRequestMethod("DELETE");
            conDel.setRequestProperty("Accept", "application/json");

            String r = nb.deleteRelationship("nm100","nm101");
            r = nb.deleteActor("nm100");
            r = nb.deleteMovie("nm101");

            assertEquals(200, code);
        }catch(IOException e){
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

            String jsonInputString = "{\"actorId\": \"nm11\", \"movieId\": \"101010111\"}";
            try(OutputStream os = con.getOutputStream()){
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            assertEquals(404, code);
        }catch(IOException e){
            fail("Exception Occured" + e.getMessage());
        }
    }

    public void testgetActorPass() {
        try {
            Connection nb = new Connection();
            if(!nb.actorExists("nm100")){
                String r = nb.insertActor("Test Actor", "nm100");
            }
            URL url = new URL("http://localhost:8080/api/v1/getActor?actorId=nm100");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            String r = nb.deleteActor("nm100");
            assertEquals(200, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testgetActorFail() {
        try {
            Connection nb = new Connection();
            if(nb.actorExists("nm100")){
                String r = nb.deleteActor("nm100");
            }
            URL url = new URL("http://localhost:8080/api/v1/getActor?actorId=nm100");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(404, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
    public void testgetMoviePass() {
        try {
            Connection nb = new Connection();
            if(!nb.movieExists("nm101")){
                String r = nb.insertMovie("Test Actor", "nm101");
            }
            URL url = new URL("http://localhost:8080/api/v1/getMovie?movieId=nm101");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            String r = nb.deleteMovie("nm101");
            assertEquals(200, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testgetMovieFail() {
        try {
            Connection nb = new Connection();
            if(nb.movieExists("nm101")){
                String r = nb.deleteMovie("nm101");
            }
            URL url = new URL("http://localhost:8080/api/v1/getMovie?movieId=nm101");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(404, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
    public void testhasRelationshipPass(){
        Connection nb = new Connection();
        if(!nb.actorExists("nm100")){
            String r = nb.insertActor("Test Actor", "nm100");
        }

        if(!nb.movieExists("nm101")){
            String r = nb.insertMovie("Test Movie", "nm101");
        }
        
        if(!nb.hasRelationship("nm100", "nm101")){
            Boolean r = nb.addRelationship("nm100", "nm101");
        }
        try{
            URL url = new URL("http://localhost:8080/api/v1/hasRelationship?actorId=nm100&movieId=nm101");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();

            String r = nb.deleteRelationship("nm100","nm101");
            r = nb.deleteActor("nm100");
            r = nb.deleteMovie("nm101");

            assertEquals(200, code);
        }catch(Exception e){
            fail("Exception Occurred:" + e.getMessage());
        }   
    }

    public void testHasRelationshipFail(){
        Connection nb = new Connection();
        if(nb.actorExists("nm100")){
            nb.deleteActor("nm100");
        }
        try{
            URL url = new URL("http://localhost:8080/api/v1/hasRelationship?actorId=nm100&movieId=nm101");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();

            String r = nb.deleteActor("nm100");
            r = nb.deleteMovie("nm101");
            r = nb.deleteRelationship("nm100","nm101");

            assertEquals(404, code);
        }catch(Exception e){
            fail("Exception Occurred:" + e.getMessage());
        }   
    }

    public void testShortestPathPass(){
        Connection nb = new Connection();
        
        nb.insertActor("Test Actor","nm900");
        nb.insertMovie("TEST MOVIE", "nm800");
        nb.addRelationship("nm900", "nm800");
        nb.insertActor("Test Actor","nm901");
        nb.addRelationship("nm901", "nm800");

        try{
            URL url = new URL("http://localhost:8080/api/v1/shortestPath?actorId1=nm900&actorId2=nm901");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int code = con.getResponseCode();
            assertEquals(200, code);

            nb.deleteRelationship("nm900", "nm800");
            nb.deleteRelationship("nm901", "nm800");
            nb.deleteActor("nm900");
            nb.deleteActor("nm901");
            nb.deleteMovie("nm800");
        }catch(Exception e){
            fail("EXCEPTION OCCURRED" + e.getMessage());
        }
    }

    public void testShortestPathFail(){
        Connection nb = new Connection();
        
        nb.insertActor("Test Actor","nm900");
        nb.insertActor("Test Actor","nm901");

        try{
            URL url = new URL("http://localhost:8080/api/v1/shortestPath?actorId1=nm900&actorId2=nm901");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int code = con.getResponseCode();
            assertEquals(404, code);

            nb.deleteActor("nm900");
            nb.deleteActor("nm901");
        }catch(Exception e){
            fail("EXCEPTION OCCURRED" + e.getMessage());
        }
    }

    public void testGetBaconNumberPass() {
        try {
            Connection nb = new Connection();
            if (!nb.actorExists("nm0000102")) {
                nb.insertActor("Kevin Bacon", "nm0000102");
            }
            if (!nb.actorExists("nm9009009")) {
                nb.insertActor("Test Actor", "nm9009009");
            }
    
            if (!nb.movieExists("nm8008008")) {
                nb.insertMovie("Test Movie", "nm8008008");
            }
            nb.addRelationship("nm0000102", "nm8008008");
            nb.addRelationship("nm9009009", "nm8008008");
    
            URL url = new URL("http://localhost:8080/api/v1/computeBaconNumber?actorId=nm9009009");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(200, code);
    
            nb.deleteRelationship("nm0000102", "nm8008008");
            nb.deleteRelationship("nm9009009", "nm8008008");
            nb.deleteActor("nm9009009");
            nb.deleteMovie("nm8008008");
    
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testGetBaconNumberFail() {
        try {
            Connection nb = new Connection();
            if (nb.actorExists("nm9999999")) {
                nb.deleteActor("nm9999999");
            }
    
            URL url = new URL("http://localhost:8080/api/v1/computeBaconNumber?actorId=nm9999999");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(404, code);
    
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
    public void testComputeBaconPathPass() {
        try {
            Connection nb = new Connection();
            if (!nb.actorExists("nm0000102")) {
                nb.insertActor("Kevin Bacon", "nm0000102");
            }
            if (!nb.actorExists("nm313131313")) {
                nb.insertActor("Test Actor", "nm313131313");
            }
            if (!nb.movieExists("nm200300400")) {
                nb.insertMovie("Test Movie", "nm200300400");
            }

            nb.addRelationship("nm0000102", "nm200300400");
            nb.addRelationship("nm313131313", "nm200300400");
    
            URL url = new URL("http://localhost:8080/api/v1/computeBaconPath?actorId=nm313131313");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(200, code);
    
            nb.deleteRelationship("nm0000102", "nm200300400");
            nb.deleteRelationship("nm313131313", "nm200300400");
            nb.deleteActor("nm0000102");
            nb.deleteActor("nm313131313");
            nb.deleteMovie("nm200300400");
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testComputeBaconPathFail() {
        try {
            Connection nb = new Connection();
            if (nb.actorExists("nm9999999")) {
                nb.deleteActor("nm9999999");
            }
    
            URL url = new URL("http://localhost:8080/api/v1/computeBaconPath?actorId=nm9999999");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(404, code);
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testGetActorOverviewPass() {
        try {
            Connection nb = new Connection();
            if (!nb.actorExists("nm0000102")) {
                nb.insertActor("Kevin Bacon", "nm0000102");
            }
            if (!nb.movieExists("nm8008008")) {
                nb.insertMovie("Test Movie", "nm8008008");
            }
            nb.addRelationship("nm0000102", "nm8008008");
    
            URL url = new URL("http://localhost:8080/api/v1/getActorOverview?actorId=nm0000102");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(200, code);
    
            nb.deleteRelationship("nm0000102", "nm8008008");
            nb.deleteActor("nm0000102");
            nb.deleteMovie("nm8008008");
    
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }

    public void testGetActorOverviewFail() {
        try {
            Connection nb = new Connection();
            if (nb.actorExists("nm9999999")) {
                nb.deleteActor("nm9999999");
            }
    
            URL url = new URL("http://localhost:8080/api/v1/getActorOverview?actorId=nm9999999");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
    
            int code = con.getResponseCode();
            assertEquals(404, code);
    
        } catch (IOException e) {
            fail("Exception Occurred: " + e.getMessage());
        }
    }
    
}

