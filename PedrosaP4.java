import java.sql.*;
import java.util.Scanner;

public class PedrosaP4 {
    private static String hostname = "localhost";
    private String port = "3306";
    private static String database = "CS331";
    private static String user = "root";
    private static String password = "root";
    private static String flags = "?noAccessToProcedureBodies=true";


    public static void main(String[] var0) throws Exception {
        printOptions();
        input();
    }

    private static void printOptions() {
        System.out.println("What would you like to do\n" +
            "1. Add an athlete.\n" +
            "2. Return results for a event.\n" +
            "3. Add a Result to a certain event.\n" +
            "4. Disqualify an athlete from ALL EVENTS.\n" +
            "5. Disqualify an athlete from ONE EVENT.\n" +
            "6. Score all events.\n" +
            "7. Get team scores (M and F).\n" +
            "8. Get individual scores (M or F).\n" +
            "9. Check for violations.");
    }

    private static void input() {
        Scanner in = new Scanner(System.in);
        while(in.hasNext()){
            String input = in.nextLine();
            switch(input) {
                case "1":
                    System.out.println("Enter First name:");
                    String fName = in.nextLine();
                    System.out.println("Enter Last name:");
                    String lName = in.nextLine();
                    System.out.println("Enter Gender (M OR F):");
                    String gender  = in.nextLine();
                    System.out.println("Enter School name:");
                    String school = in.nextLine();
                    addAthlete(fName,lName,school, gender.charAt(0));
                    break;
                case "2":
                    System.out.println("Event number:");
                    String eventID = in.nextLine();
                    System.out.println("Result type (M or F):");
                    String type = in.nextLine();
                    returnResults(Integer.parseInt(eventID),type.charAt(0));
                    break;
                case "3":
                    System.out.println("Event number:");
                    eventID = in.nextLine();
                    System.out.println("Enter athlete ID:");
                    String ID = in.nextLine();
                    System.out.println("Enter mark:");
                    String mark = in.nextLine();
                    addResult(Integer.parseInt(eventID),Integer.parseInt(ID),Double.parseDouble(mark));
                    break;
                case "4":
                    System.out.println("Enter athlete ID:");
                    ID = in.nextLine();
                    removeAll(Integer.parseInt(ID));
                    break;
                case "5":
                    System.out.println("Enter athlete ID:");
                    ID = in.nextLine();
                    System.out.println("Event number:");
                    eventID = in.nextLine();
                    removeOne(Integer.parseInt(ID), Integer.parseInt(eventID));
                    break;
                case "6":
                    scoreEvent();
                    break;
                case "7":
                    rateTeams();
                    break;
                case"8":
                    rateIndividialTeams('f');
                    rateIndividialTeams('m');
                    break;
                case"9":
                    checkViolation();
            }
            printOptions();
        }
    }

    private static void checkViolation() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            boolean results = statement.execute("select compID from Athlete");
            ResultSet table = statement.getResultSet();
            table.first();
            while(results){
                int id = table.getInt("compID");
                boolean update = statement1.execute(String.format("select count(*) as number from Results where compID = %d",id));
                ResultSet table1 = statement1.getResultSet();
                table1.first();
                int number = table1.getInt("number");
                if(number > 4) {
                    statement2.executeUpdate(String.format("update Results set dq = true where compID = %d ", id));
                    System.out.print(String.format("Athlete number %d has cheated. DQ applied to all events!\n",id));
                }
                results = table.next();
            }
            System.out.println("All done. Please re-run #6 to get an updated results!");
        } catch(SQLException e){
            System.out.print("An error has occurred!");
        }

    }

    private static void rateIndividialTeams(char sex) {
        try {
            String gender;
            if(sex == 'm')
               gender = "Male";
            else
                gender = "Female";
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement3 = connection.createStatement();
            Statement statement1 = connection.createStatement();
            statement3.executeUpdate("update school set points = 0 where points > 0");
            Statement statement = connection.createStatement();
            boolean results = statement.execute(String.format("select A.compID, points from Results join Athlete A on Results.compID = A.compID where gender = '%c'",sex));
            ResultSet table = statement.getResultSet();
            System.out.println(String.format("%s Results\n| %-3s | %-35s | %-8s |",gender,"Place","School Name", "Points"));
            System.out.print("----------------------------------------------------------\n");
            table.first();
            while(results){
                int points = table.getInt("points");
                int id = table.getInt("compID");
                statement1.execute(String.format("select schoolName from Athlete where compID = %s",id));
                ResultSet table1 = statement1.getResultSet();
                table1.first();
                String name = table1.getString("schoolName");
                statement1.executeUpdate(String.format("update school set points = points + %d where schoolName = '%s'",points, name));
                results = table.next();
            }
            Statement statement2 = connection.createStatement();
            boolean place = statement2.execute("select * from school order by points desc");
            ResultSet table2 = statement2.getResultSet();
            table2.first();
            int i = 1;
            while(place){
                String name = table2.getString("schoolName");
                int points = table2.getInt("points");
                System.out.print(String.format("| %-5d | %-35s | %-8s |\n",i,name, points));
                i++;
                place = table2.next();
            }
            connection.close();
            System.out.print("----------------------------------------------------------\n");
        } catch (SQLException e) {
            System.out.println("No results for M or F!");
        }

    }

    private static void rateTeams() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            Statement statement3 = connection.createStatement();
            statement3.executeUpdate("update school set points = 0 where points > 0");
            boolean results = statement.execute("select compID, points from Results");
            ResultSet table = statement.getResultSet();
            table.first();
            System.out.println(String.format("| %-3s | %-35s | %-8s |","Place","School Name", "Points"));
            System.out.print("----------------------------------------------------------\n");
            while(results) {
                int points = table.getInt("points");
                int id = table.getInt("compID");
                statement1.execute(String.format("select schoolName from Athlete where compID = %s",id));
                ResultSet table1 = statement1.getResultSet();
                table1.first();
                String name = table1.getString("schoolName");
                statement1.executeUpdate(String.format("update school set points = points + %d where schoolName = '%s'",points, name));
                results = table.next();
            }
            Statement statement2 = connection.createStatement();
            boolean place = statement2.execute("select * from school order by points desc");
            ResultSet table2 = statement2.getResultSet();
            table2.first();
            int i = 1;
            while(place){
                String name = table2.getString("schoolName");
                int points = table2.getInt("points");
                System.out.print(String.format("| %-5d | %-35s | %-8s |\n",i,name, points));
                i++;
                place = table2.next();
            }
            connection.close();
            System.out.print("----------------------------------------------------------\n");
        } catch (SQLException e) {
            System.err.println("M or F results may not exist!");
        }
    }

    private static void removeOne(int id, int eventID) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("update Results set dq = true where eventNumber = %s and compID = %s", eventID, id));
            exists(id);
            existsID(eventID);
            connection.close();
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            String error = String.format("Event number = '%s', Athlete ID ='%s'", eventID, id);
            System.out.print("------------------------------------------------------->");
            System.out.println("\nAn error has occurred. Possible errors:\n" +
                "Athlete May not exist!\n" +
                "Event might not exist!\n" +
                "You entered: " + error);
            System.out.println("------------------------------------------------------->");
        }
    }

    private static void existsID(int eventID) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
        Statement statement = connection.createStatement();
        statement.execute(String.format("select eventNumber from Results where eventNumber = %s ",eventID));
        ResultSet table = statement.getResultSet();
        table.first();
        String name = table.getString("eventNumber");
    }

    private static void removeAll(int id) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("update Results set dq = true where compID = %s", id));
            exists(id);
            connection.close();
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            String error = String.format("Athlete ID ='%s'", id);
            System.out.print("------------------------------------------------------->");
            System.out.println("\nAn error has occurred. Possible error:\n" +
                "Athlete May not exist!\n" +
                "You entered: " + error);
            System.out.println("------------------------------------------------------->");
        }
    }

    private static void exists(int id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
        Statement statement = connection.createStatement();
        statement.execute(String.format("select compID from Athlete where compID = %s ",id));
        ResultSet table = statement.getResultSet();
        table.first();
        String name = table.getString("compID");

    }

    private static void addResult(int eventID, int id, double mark) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("insert into Results values(%s, null , false,%s,'%s',null, null)",mark,id,eventID));
            connection.close();
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            String error = String.format("Event #='%s', Athlete ID ='%s', Mark ='%s'", eventID, id, mark);
            System.out.print("------------------------------------------------------->");
            System.out.println("\nAn error has occurred. Possible causes:\n" +
                "You might have entered the incorrect athlete ID.\n" +
                "You might have entered the incorrect event number.\n" +
                 "You entered: " + error);
            System.out.println("------------------------------------------------------->");
        }
    }

    private static void addAthlete(String first, String last, String schoolName, char gender) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("insert into Athlete values(0,'%s','%s','%c','%s',false)",first,last,gender,schoolName));
            connection.close();
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            String error = String.format("First name ='%s', Last name ='%s', Gender = '%c', School ='%s'",first,last,gender,schoolName);
            System.out.print("------------------------------------------------------->");
            System.out.println("\nAn error has occurred. Possible causes:\n" +
                "School might not exist.\n" +
                "Athlete might not exist.\n" +
                "School name OR Athlete name might be spelled incorrectly.\n" +
                "You entered: " + error);
            System.out.println("------------------------------------------------------->");
        }
    }

    private static void scoreEvent() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            boolean eventNumberM = statement.execute("select distinct eventNumber from Results join Athlete A on Results.compID = A.compID where gender = 'M'");
            boolean eventNumberF = statement1.execute("select distinct eventNumber from Results join Athlete A on Results.compID = A.compID where gender = 'F'");
            ResultSet tableF = statement1.getResultSet();
            ResultSet tableM = statement.getResultSet();
            tableM.first();
            tableF.first();
            while(eventNumberM) {
                int id = tableM.getInt("eventNumber");
                returnResults(id,'M');
                eventNumberM = tableM.next();
            }
            while(eventNumberF) {
                int id = tableF.getInt("eventNumber");
                returnResults(id,'F');
                eventNumberF = tableF.next();
            }
            connection.close();
        } catch (SQLException e) {
            System.out.print("------------------------------------------------------->");
            System.out.println("\nAn error has occurred. Possible causes:\n" +
                "You might have entered the incorrect athlete ID.\n" +
                "You might have entered the incorrect event number.\n");
            System.out.println("------------------------------------------------------->");
        }
    }

    public static void returnResults(int eventID, char gender){
        try {
            String order;
            if(eventID == 9 || eventID == 10)
                order = "desc";
            else
                order = "asc";
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS331", user, password);
            Statement statement = connection.createStatement();
            Statement statement1 = connection.createStatement();
            boolean query = statement.execute("select entryNumber as id, placement as Place, firstName as First, lastName as Last, results as Results, schoolName as School, dq as DQ, points as Points from Athlete join Results R on Athlete.compID = R.compID where eventNumber =" + eventID + " and  gender = '"  + gender  + "' order by Results " + order);
            boolean query1 = statement1.execute("select eventName from Events where eventNumber = " + eventID);
            ResultSet table1 = statement1.getResultSet();
            ResultSet table = statement.getResultSet();
            table.first();
            table1.first();
            String name = table1.getString("eventName");
            System.out.println("Event #" + eventID + ": " + name);
            System.out.printf("| %-6s | %-8s | %-15s | %-15s | %-15s | %-40s | %-6s |\n","Place","Points","First","Last", "Results", "School", "DQ?");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            int i = 1;
            boolean Reorder = false;
            while(query) {
                int id = table.getInt("id");
                String first = table.getString("First");
                String last = table.getString("Last");
                double results = table.getDouble("Results");
                String school = table.getString("School");
                boolean dq = table.getBoolean("DQ");
                int points = table.getInt("Points");
                int point;
                point = i == 1 ? 10 : i == 2 ? 8 : i == 3 ? 6 : i == 4 ? 4 : i == 5 ? 2 : i == 6 ? 1 : i > 6 ? 0 : 0;
                if(Reorder) {
                    point = i == 1 ? 10 : i == 2 ? 10 : i == 3 ? 8 : i == 4 ? 6 : i == 5 ? 4 : i == 6 ? 2 : i == 7 ? 1 : i > 8 ? 0 : 0;
                    i = i == 1 ? 1  : i - 1;
                }
                int temp = i;
                if(dq) {
                    point = 0;
                    Reorder = true;
                }
                statement1.executeUpdate(String.format("update Results set placement = %d, points = %d where entryNumber = %s",i,point, id));
                System.out.printf("| %-6d | %-8d | %-15s | %-15s | %-15.2f | %-40s | %-6b |\n",i,point,first,last, results, school, dq);
                i = temp;
                i++;
                query = table.next();
            }
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            connection.close();
        } catch (SQLException e) {
            String error = String.format("You entered: Event %s and Gender %s",eventID,gender);
            System.out.println("------------------------------------------------------->");
            System.out.println("An Error has occurred. You might have entered an event number that doesn't exist!" + error);
            System.out.println("------------------------------------------------------->");
        }
    }
}
