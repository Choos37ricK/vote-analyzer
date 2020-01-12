import java.sql.*;

public class DBConnection
{
    private static Connection connection;


    private static String dbName = "learn";
    private static String dbUser = "root";
    private static String dbPass = "45Norty92";

    private static PreparedStatement preparedStatement = null;
    private static Integer counter = 0;

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
                        "PRIMARY KEY(id), KEY(name(50)))");
                connection.setAutoCommit(false);
                preparedStatement = connection.prepareStatement("INSERT INTO voter_count (name, birthDate, `count`) VALUES (?, ?, ?)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void executeMultiInsert() throws SQLException {
        preparedStatement.executeBatch();
        connection.commit();
    }

    public static void countVoter(String name, String birthDay) throws SQLException
    {
        birthDay = birthDay.replace('.', '-');

        preparedStatement.setString(1, name);
        preparedStatement.setString(2, birthDay);
        preparedStatement.setInt(3, 1);
        preparedStatement.addBatch();
        counter++;

        if (counter > 40000) {
            executeMultiInsert();
            counter = 0;
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
