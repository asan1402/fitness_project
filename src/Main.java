import java.util.*;
import java.sql.*;
import java.io.*;

class GlobalVariables {
    public static HashMap <String, String> userLoginRole = new HashMap<>();

    public static HashMap <String, ArrayList <String> > userRoleLogin = new HashMap<>();

    public static HashMap <String, String> userLoginPassword = new HashMap<>();

    public static HashMap <String, Integer> procedureTitleId = new HashMap<>();

    public static HashMap <String, Integer> clientLoginId = new HashMap<>();

    public static final String DB_URL = "jdbc:sqlite:FitnessCenterProject.db";

    public static ArrayList <String> procedureLists = new ArrayList<>();

    public static HashMap <String, ArrayList <String> > procedureSchedule = new HashMap<>();

    public static ArrayList <String> weekDays = new ArrayList<>();

    public static void insertDataToWeekDays(){
        weekDays.add("Понедельник");
        weekDays.add("Вторник");
        weekDays.add("Среда");
        weekDays.add("Четверг");
        weekDays.add("Пятница");
    }
}

class insertDataMethods extends DataBaseSQL {
    public static void insertDataToUsers(String role, String login, String password) {
        String sql = "INSERT INTO users(role, login, password) VALUES(?, ?, ?)";

        if (userLoginRole.get(login) == null) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, role);
                pstmt.setString(2, login);
                pstmt.setString(3, password);

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToProcedures(String title, int cost) {
        String sql = "INSERT INTO procedures(title, cost) VALUES(?, ?)";

        if (procedureTitleId.get(title) == null) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, title);
                pstmt.setInt(2, cost);

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToClients(String login, String name, String surname, Integer height, Integer weight, Integer bloodType, String dateOfBirth) {
        String sql = "INSERT INTO clients(login, name, surname, height, weight, bloodType, dateOfBirth) VALUES(?, ?, ?, ?, ?, ?, ?)";

        if (clientLoginId.get(login) == null) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, login);
                pstmt.setString(2, name);
                pstmt.setString(3, surname);
                pstmt.setInt(4, height);
                pstmt.setInt(5, weight);
                pstmt.setInt(6, bloodType);
                pstmt.setString(7, dateOfBirth);

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }
}

class DataBaseSQL extends GlobalVariables {
    public static void dataBaseSQL(){

        /* deleteTable(); */ createTable();

        if (factorySettings()) {
            insertDataMethods.insertDataToUsers("personal", "personal1", "pel1");
            insertDataMethods.insertDataToUsers("director", "director1", "dir1");
            insertDataMethods.insertDataToUsers("manager", "manager1", "mar1");
            insertDataMethods.insertDataToUsers("client", "client1", "clt1");

            insertDataMethods.insertDataToProcedures("Массаж", 2000);
            insertDataMethods.insertDataToProcedures("Йога", 5000);
            insertDataMethods.insertDataToProcedures("Бассейн", 4000);

            insertDataMethods.insertDataToClients("client1", "Мирлан", "Кыдыев", 182, 75, 3, "14.02.2007");
            insertDataMethods.insertDataToClients("client2", "Тагайбек", "Кубатов", 200, 150, 2, "20.10.2006");
        }

        readUsersData();
        readProceduresData();
        readClientsData();
    }

    private static void deleteTable() {
        ArrayList <String> tableName = new ArrayList<>();

        tableName.add("users");
        tableName.add("procedures");
        tableName.add("clients");

        for (String x : tableName) {
            String sql = "DROP TABLE IF EXISTS " + x;

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);

            } catch (SQLException e) {
                System.err.println("Ошибка при удалении таблицы: " + e.getMessage());
            }
        }
    }

    private static void createTable() {
        String sqlUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                role TEXT NOT NULL,
                login TEXT NOT NULL,
                password TEXT NOT NULL
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
        }

        String sqlProcedures = """
            CREATE TABLE IF NOT EXISTS procedures (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                cost INTEGER NOT NULL
            );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProcedures);

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
        }

        String sqlClients = """
            CREATE TABLE IF NOT EXISTS clients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login TEXT NOT NULL,
                name TEXT NOT NULL,
                surname TEXT NOT NULL,
                height INTEGER NOT NULL,
                weight INTEGER NOT NULL,
                bloodType INTEGER NOT NULL,
                dateOfBirth TEXT NOT NULL
           );
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlClients);

        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    private static boolean factorySettings() {
        String sql = "SELECT 1 FROM users LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return !rs.next();

        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
        return false;
    }

    private static void readUsersData() {
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String role = rs.getString("role");
                String login = rs.getString("login");
                String password = rs.getString("password");

                userLoginRole.put(login, role);

                if (userRoleLogin.get(role) != null){
                    userRoleLogin.get(role).add(login);
                }
                else{
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(login);

                    userRoleLogin.put(role, AL);
                }

                userLoginPassword.put(login, password);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readProceduresData() {
        String sql = "SELECT * FROM procedures";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");

                procedureTitleId.put(title, id);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readClientsData() {
        String sql = "SELECT * FROM clients";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");

                clientLoginId.put(login, id);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }
}

class DataBaseTXT extends GlobalVariables {
    public static void dataBaseTXT(){
        deleteFile();

        createFile();

        if (factorySettings()){
            writeFileToProcedureList("Массаж", "Понедельник", "13:00");
            writeFileToProcedureList("Йога", "Вторник", "15:00");
            writeFileToProcedureList("Бассейн", "Среда", "12:00");
            writeFileToProcedureList("Массаж", "Среда", "10:00");
            writeFileToProcedureList("Йога", "Четверг", "14:00");
            writeFileToProcedureList("Бассейн", "Пятница", "15:00");
            writeFileToProcedureList("Бассейн", "Вторник", "09:00");
        }
        else{
            readFile();
        }

        insertToProcedureSchedule();

        rewriteFile();
    }

    private static void deleteFile(){
        new File("procedureList.txt").delete();
    }

    private static void createFile(){
        try {
            new File("procedureList.txt").createNewFile();

        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    private static boolean factorySettings(){
        return new File("procedureList.txt").length() == 0;
    }

    public static void writeFileToProcedureList(String procedureName, String weekDay, String time){
        String newProcedureList = procedureName + " " + weekDay + " " + time;

        if (procedureLists.contains(newProcedureList)){
            System.out.println("Произошло повторение");
            return;
        }
        procedureLists.add(newProcedureList);

        try {
            FileWriter fileWriter = new FileWriter("procedureList.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : procedureLists){
                bufferedWriter.write(x);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }

    private static void readFile(){
        try {
            FileReader fileReader = new FileReader("procedureList.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                procedureLists.add(line);
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    private static void insertToProcedureSchedule(){
        GlobalVariables.insertDataToWeekDays();

        for (String x : weekDays) {
            ArrayList<String> list = new ArrayList<>();

            for (String y : procedureLists) {
                String[] weekDay = y.split("\\s+");

                if (weekDay[1].equals(x)){
                    list.add(weekDay[2] + " " + weekDay[0]);
                }
            }

            Collections.sort(list);

            ArrayList <String> list2 = new ArrayList<>();

            for (String y : list) {
                String[] list3 = y.split("\\s");
                list2.add(list3[1] + " " + list3[0]);
            }

            procedureSchedule.put(x, list2);
        }
    }

    private static void rewriteFile(){
        try {
            FileWriter fileWriter = new FileWriter("procedureList.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : weekDays) {
                for (String y : procedureSchedule.get(x)) {
                    bufferedWriter.write(x + " " + y);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }
}

public class Main {
    public static void main(String[] args){
        DataBaseSQL.dataBaseSQL();

        DataBaseTXT.dataBaseTXT();
    }
}

class similarUserMethods extends DataBaseSQL {
    public static void procedureSchedule(){
        ArrayList <String> weekDays = new ArrayList<>();

        weekDays.add("Понедельник");
        weekDays.add("Вторник");
        weekDays.add("Среда");
        weekDays.add("Четверг");
        weekDays.add("Пятница");

        for (String x : weekDays) {
            System.out.println("\n" + x + ":");

            for (String y : procedureSchedule.get(x)) {
                System.out.println(y);
            }

        }
    }
}k