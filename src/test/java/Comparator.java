import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Comparator {
    public List<String> file1Req;
    public List<String> file2Req;
    public  Comparator(String file1,String file2){
        try {
            File f1= new File(getClass().getResource(file1).getFile());
            File f2= new File(getClass().getResource(file2).getFile());
            file1Req=readAllURLFromFiles(f1);
            file2Req=readAllURLFromFiles(f2);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private List<String> readAllURLFromFiles(File file){
        List<String> data=new ArrayList<>();
        String line;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            while ((line=bf.readLine())!=null){
                data.add(line);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return data;
    }

    public void compareResponses(){
        int size;
        if(file1Req.size() == file2Req.size()){
            size = file1Req.size();
        } else if (file1Req.size() > file2Req.size()){
            size=file2Req.size();
        } else{
            size=file1Req.size();
        }
        for(int i=0;i<size;i++) {
            if (file1Req.get(i).equalsIgnoreCase(file2Req.get(i))) {
                Response resp1 = getResponse(file1Req.get(i));
                Response resp2 = getResponse(file2Req.get(i));
                //Not verifying the status code and all assuming if error there might get some info in error
                if (resp1.contentType().equalsIgnoreCase(resp2.contentType())) {
                    if (resp1.contentType().contains("application/json")) {
                        if (comparedJSONResponse(resp1, resp2)) {
                            System.out.println(file1Req.get(i) + " equals " + file2Req.get(i));
                        } else {
                            System.out.println(file1Req.get(i) + " not equals " + file2Req.get(i));
                        }
                    }else if (resp1.contentType().contains("application/xml")) {
                        if (comparedXMLResponse(resp1, resp2)) {
                            System.out.println(file1Req.get(i) + " equals " + file2Req.get(i));
                        } else {
                            System.out.println(file1Req.get(i) + " not equals " + file2Req.get(i));
                        }
                    }
                } else {//If both content types are different then by default it is not equals
                    System.out.println(file1Req.get(i) + " not equals " + file2Req.get(i));
                }
            }else {//If both content types are different then by default it is not equals
                System.out.println(file1Req.get(i) + " not equals " + file2Req.get(i));
            }
        }
    }

    //This method is basically compare the result by converting to string it will dont care whether data is json or xml format
    private boolean comparedResponseByConvertingString(Response resp1,Response resp2){
        return resp1.getBody().asString().equals(resp2.getBody().asString());
    }

    private boolean comparedXMLResponse(Response resp1,Response resp2){
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xml1 = builder.parse(resp1.getBody().asString());
            Document xml2 = builder.parse(resp2.getBody().asString());
            return xml1.equals(xml2);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  false;
    }

    private boolean comparedJSONResponse(Response resp1,Response resp2){
        JSONParser jsonParser=new JSONParser();
        try {
            JSONObject resp1JsonObject = (JSONObject)jsonParser.parse(resp1.getBody().asString());
            JSONObject resp2JsonObject = (JSONObject)jsonParser.parse(resp2.getBody().asString());
            return resp1JsonObject.equals(resp2JsonObject);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    private Response getResponse(String url){
        return RestAssured.given().when().get(url);
    }

}
