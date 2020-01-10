import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*import org.xml.sax.SAXException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;*/

public class XMLHandler extends DefaultHandler {

    /*private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    private Voter voter;
    private static HashMap<Integer, WorkTime> voteStationWorkTimes = new HashMap<>();
    private static HashMap<Voter, Byte> voterCounts = new HashMap<>();

    public XMLHandler() {
        voterCounts = new HashMap<>();
    }*/

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            if (qName.equals("voter")) {
//                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
//                voter = new Voter(attributes.getValue("name"), birthDay);

                String name = attributes.getValue("name");
                String birthDay = attributes.getValue("birthDay");
                DBConnection.countVoter(name, birthDay);
            } /*else if (qName.equals("visit") && voter != null) {
                int count = voterCounts.getOrDefault(voter, 0);
                voterCounts.put(voter, count + 1);

                int station = Integer.parseInt(attributes.getValue("station"));
                Date time = visitDateFormat.parse(attributes.getValue("time"));
                WorkTime workTime = voteStationWorkTimes.get(station);
                if(workTime == null)
                {
                    workTime = new WorkTime();
                    voteStationWorkTimes.put(station, workTime);
                }
                workTime.addVisitTime(time.getTime());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
