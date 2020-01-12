import java.sql.*;

public class DBConnection
{
    private static Connection connection;


    private static String dbName = "learn";
    private static String dbUser = "root";
    private static String dbPass = "45Norty92";

    private static StringBuilder insertQuery = new StringBuilder();

    private static String errstr;

    public static Connection getConnection()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass +
                                "&useSSL=false"+
                                "&requireSSL=false"+
                                "&useLegacyDatetimeCode=false"+
                                "&amp"+
                                "&serverTimezone=UTC");
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "`count` INT NOT NULL, " +
                        "PRIMARY KEY(id), " +
                        "UNIQUE KEY name_date(name(50), birthDate))");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void executeMultiInsert() throws SQLException {
        String builderQuery = insertQuery.toString();
        System.out.println("builderQuery.length = " + builderQuery.length());
        String sqlQuery = "INSERT INTO voter_count(name, birthDate, `count`) " +
                "VALUES" + builderQuery +
                " ON DUPLICATE KEY UPDATE `count`=`count` + 1";
        System.out.println("start: " + sqlQuery + " :end");
        DBConnection.getConnection().createStatement().execute(sqlQuery);
    }

    public static void countVoter(String name, String birthDay) throws SQLException
    {
        birthDay = birthDay.replace('.', '-');

        insertQuery.append((insertQuery.length() == 0 ? "" : ",") + "('" + name + "', '" + birthDay + "', 1)");

        if (insertQuery.length() > 100000) {
            new Thread(() ->
            {
                try {
                    executeMultiInsert();
                } catch (SQLException e) {
                    errstr = insertQuery.toString();
                    e.printStackTrace();
                }
            }).start();

            System.out.println("insertQuery.length = " + insertQuery.length());
            insertQuery = new StringBuilder();
            System.out.println("insertQuery.length after = " + insertQuery.length());
        }
    }

    public static void printVoterCounts() throws SQLException
    {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while(rs.next())
        {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getByte("count"));
        }
    }
}
