import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.*;
import java.io.File;
/*import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;*/

public class Loader
{
    /*private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static HashMap<Integer, WorkTime> voteStationWorkTimes = new HashMap<>();
    private static HashMap<Voter, Byte> voterCounts = new HashMap<>();*/

    public static void main(String[] args) throws Exception
    {
        String fileName = "res/data-18M.xml";
        DBConnection.getConnection();

        System.out.println("Program started...");

        long startTime = System.currentTimeMillis();

        saxParsing(fileName);
        //domParsing(fileName);
        DBConnection.executeMultiInsert();

        System.out.println("Parsing and inserting duration: " + (System.currentTimeMillis() - startTime) + " ms\n");

//        startTime = System.currentTimeMillis();
//        DBConnection.printVoterCounts();
//        System.out.println("\nSelect voters query duration: " + (System.currentTimeMillis() - startTime) + " ms\n");
    }

    private static void printMemory() {
        System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
        System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
        System.out.println();
    }

    private static void saxParsing(String fileName) throws Exception {
        System.out.println("---SAX PARSER---");
        printMemory();
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory usage before SAX parsing: " + memoryUsage + " bytes\n");

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler handler = new XMLHandler();
        parser.parse(new File(fileName), handler);

        System.out.println("SAX parsing ended...");

        printMemory();
        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryUsage;
        System.out.println("Memory usage after SAX parsing: " + memoryUsage + " bytes\n");
    }

    private static void domParsing(String fileName) throws Exception {
        System.out.println("---DOM PARSER---");
        printMemory();
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory usage before DOM parsing: " + memoryUsage + " bytes\n");

        try {
            parseFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println("DOM parsing ended...");

        printMemory();
        memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryUsage;
        System.out.println("Memory usage after DOM parsing: " + memoryUsage + " bytes\n");
    }

    private static void parseFile(String fileName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        findEqualVoters(doc);
        //fixWorkTimes(doc);
    }

    private static void findEqualVoters(Document doc) throws Exception {
        NodeList voters = doc.getElementsByTagName("voter");
        int votersCount = voters.getLength();
        for(int i = 0; i < votersCount; i++)
        {
            Node node = voters.item(i);
            NamedNodeMap attributes = node.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();
            String birthDay = attributes.getNamedItem("birthDay").getNodeValue();

            DBConnection.countVoter(name, birthDay);
        }
    }

    /*private static void fixWorkTimes(Document doc) throws Exception {
        NodeList visits = doc.getElementsByTagName("visit");
        int visitCount = visits.getLength();
        for(int i = 0; i < visitCount; i++)
        {
            Node node = visits.item(i);
            NamedNodeMap attributes = node.getAttributes();

            Integer station = Integer.parseInt(attributes.getNamedItem("station").getNodeValue());
            Date time = visitDateFormat.parse(attributes.getNamedItem("time").getNodeValue());
            WorkTime workTime = voteStationWorkTimes.get(station);
            if(workTime == null)
            {
                workTime = new WorkTime();
                voteStationWorkTimes.put(station, workTime);
            }
            workTime.addVisitTime(time.getTime());
        }
    }*/
}