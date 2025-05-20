import java.util.*;
import java.sql.*;
import java.io.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

class GlobalVariables {
    /* Data Base SQL */
    public static final String DB_URL = "jdbc:sqlite:FitnessCenterProject.db";
    public static HashMap <String, String> userLoginRole = new HashMap<>();
    public static HashMap <String, ArrayList <String> > userRoleLogins = new HashMap<>();
    public static HashMap <String, String> userLoginPassword = new HashMap<>();
    public static HashMap <String, Integer> procedureTitleCost = new HashMap<>();
    public static HashMap <String, Integer> clientLoginId = new HashMap<>();

    /* Data Base TXT */
    public static ArrayList <String> procedureTitle = new ArrayList<>();
    public static ArrayList <String> procedureLists = new ArrayList<>();
    public static ArrayList <String> weekDays = new ArrayList<>();
    public static void insertDataToWeekDays(){
        weekDays.add("Понедельник");
        weekDays.add("Вторник");
        weekDays.add("Среда");
        weekDays.add("Четверг");
        weekDays.add("Пятница");
    }
    public static HashMap <String, ArrayList <String> > procedureSchedule = new HashMap<>();
    public static HashMap <String, ArrayList <String> > procedureWeekDayAndTime = new HashMap<>();

    /* Data Base TXTv2 */
    public static HashMap <String, ArrayList <String> > paidProcedureLists = new HashMap<>();
    public static HashMap <String, ArrayList <String> > paidProcedureTitleClients = new HashMap<>();
    public static HashMap <String, Integer> paidProcedureTitleCount = new HashMap<>();
    public static HashMap <String, ArrayList <String> > paidProcedureClientVisit = new HashMap<>();

    /* Data Base Time */
    public static String currentTime;
    public static String currentDate;
    public static String currentWeekDay;
    public static String currentYear;
    public static String currentMonth;
    public static String currentDay;

    /* Sign In */
    public static String currentRole;
    public static String currentLogin;
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

        if (procedureTitleCost.get(title) == null) {
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

class DataBaseSQL extends GlobalVariables{
    public static void dataBaseSQL(){

        /* deleteTable(); */ createTable();

        if (factorySettings()) {
            insertDataMethods.insertDataToUsers("personal", "personal1", "pel1");
            insertDataMethods.insertDataToUsers("director", "director1", "dir1");
            insertDataMethods.insertDataToUsers("manager", "manager1", "mar1");
            insertDataMethods.insertDataToUsers("client", "client1", "clt1");
            insertDataMethods.insertDataToUsers("client", "client2", "clt2");

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

                if (userRoleLogins.get(role) == null){
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(login);

                    userRoleLogins.put(role, AL);
                }
                else{
                    userRoleLogins.get(role).add(login);
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
                String title = rs.getString("title");
                int cost = rs.getInt("cost");

                procedureTitleCost.put(title, cost);
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

        /* deleteFile(); */  createFile();

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

        insertToProcedureWeekDayAndTime();
    }

    private static void deleteFile(){
        new File("procedureLists.txt").delete();
    }

    private static void createFile(){
        try {
            new File("procedureLists.txt").createNewFile();

        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    private static boolean factorySettings(){
        return new File("procedureLists.txt").length() == 0;
    }

    public static void writeFileToProcedureList(String title, String weekDay, String time){
        String newProcedureList = title + " " + weekDay + " " + time;

        if (procedureLists.contains(newProcedureList)){
            System.out.println("Произошло повторение");
            return;
        }
        if (!procedureTitle.contains(title)){
            procedureTitle.add(title);
        }
        procedureLists.add(newProcedureList);

        try {
            FileWriter fileWriter = new FileWriter("procedureLists.txt");
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

    public static void readFile(){
        try {
            FileReader fileReader = new FileReader("procedureLists.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String title = line.split("\\s")[0];

                if (!procedureTitle.contains(title)){
                    procedureTitle.add(title);
                }
                procedureLists.add(line);
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    public static void insertToProcedureSchedule(){
        GlobalVariables.insertDataToWeekDays();

        for (String x : weekDays) {
            ArrayList<String> list = new ArrayList<>();

            for (String y : procedureLists) {
                String[] list2 = y.split("\\s");

                if (list2[1].equals(x)){
                    list.add(list2[2] + " " + list2[0]);
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
            FileWriter fileWriter = new FileWriter("procedureLists.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : weekDays) {
                for (String y : procedureSchedule.get(x)) {
                    String[] list = y.split("\\s");

                    bufferedWriter.write(list[0] + " " + x + " " + list[1]);
                    bufferedWriter.newLine();
                }
            }
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }

    public static void insertToProcedureWeekDayAndTime(){
        try {
            FileReader fileReader = new FileReader("procedureLists.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] list = line.split("\\s");

                if (procedureWeekDayAndTime.get(list[0]) == null){
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(list[1] + " " + list[2]);

                    procedureWeekDayAndTime.put(list[0], AL);
                }
                else{
                    procedureWeekDayAndTime.get(list[0]).add(list[1] + " " + list[2]);
                }
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }
}

class DataBaseTXTv2 extends GlobalVariables {
    public static void dataBaseTXTv2(){
        for (String x : userRoleLogins.get("client")) {
            /* deleteFile(x) */ createFile(x);

            if (x.equals("client1")){
                writeFileToPaidProcedures(x, "Бассейн", "4000 сом", "Вторник", "09:00", "2025.05.19");
            }

            readFile(x);
        }
    }

    private static void deleteFile(String login){
        new File(login + "PaidProcedures.txt").delete();
    }

    private static void createFile(String login){
        try {
            new File(login + "PaidProcedures.txt").createNewFile();

        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    public static void writeFileToPaidProcedures(String login, String title, String cost, String weekDay, String time, String date) {
        String line = title + " " + cost + " " + weekDay + " " + time + " " + date;

        try {
            FileWriter fileWriter = new FileWriter(login + "PaidProcedures.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(line);
            bufferedWriter.newLine();

            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }

    private static void readFile(String login){
        try {
            FileReader fileReader = new FileReader(login + "PaidProcedures.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (paidProcedureLists.get(login) == null){
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(line);

                    paidProcedureLists.put(login, AL);
                }
                else{
                    paidProcedureLists.get(login).add(line);
                }

                if (DataBaseTime.visitPassed(line.split("\\s")[5])) {
                    if (paidProcedureClientVisit.get(login) == null) {
                        ArrayList <String> AL = new ArrayList<>();
                        AL.add(line);

                        paidProcedureClientVisit.put(login, AL);
                    }
                    else {
                        paidProcedureClientVisit.get(login).add(line);
                    }
                }

                String title = line.split("\\s")[0];

                if (paidProcedureTitleClients.get(title) == null){
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(login);

                    paidProcedureTitleClients.put(title, AL);
                }
                else{
                    paidProcedureTitleClients.get(title).add(login);
                }

                if (paidProcedureTitleCount.get(login) == null){
                    paidProcedureTitleCount.put(login, 1);
                }
                else{
                    paidProcedureTitleCount.put(login, paidProcedureTitleCount.get(login) + 1);
                }
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }
}

class DataBaseTime extends GlobalVariables{
    public static void dataBaseTime(){
        ZoneId zone = ZoneId.of("Asia/Bishkek");
        ZonedDateTime now = ZonedDateTime.now(zone);

        insertTimeAndData(now);

        insertWeekDay(now);

        insertYearMonthDay();
    }

    private static void insertTimeAndData(ZonedDateTime now){
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        currentTime = now.format(time);
        currentDate = now.format(date);
    }

    private static void insertWeekDay(ZonedDateTime now){
        String weekDay = now.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru"));
        char firstLetter = weekDay.toUpperCase().charAt(0);

        currentWeekDay = firstLetter + weekDay.substring(1);
    }

    private static void insertYearMonthDay(){
        currentYear = currentDate.substring(0, 4);
        currentMonth = currentDate.substring(5, 7);
        currentDay = currentDate.substring(8);
    }

    public static String insertDateToPaidProcedure(String paidProcedureWeekDayAndTimeDate){
        String weekDay = paidProcedureWeekDayAndTimeDate.split("\\s")[0];

        int currentMinutes = 1440 * weekDayOrder(currentWeekDay);
        int minutes = 1440 * weekDayOrder(weekDay);

        String time = paidProcedureWeekDayAndTimeDate.split("\\s")[1];

        currentMinutes += timeInMinutes(currentTime);
        minutes += timeInMinutes(time);

        int difference;

        if (currentMinutes >= minutes){
            difference = weekDayOrder(currentWeekDay) + 7 - weekDayOrder(weekDay);
        }
        else{
            difference = weekDayOrder(weekDay) - weekDayOrder(currentWeekDay);
        }

        return currentDateMethod(difference);
    }

    public static int weekDayOrder(String weekDay){
        return switch (weekDay) {
            case "Понедельник" -> 1;
            case "Вторник" -> 2;
            case "Среда" -> 3;
            case "Четверг" -> 4;
            case "Пятница" -> 5;
            default -> 0;
        };
    }

    private static int timeInMinutes(String time){
        String hour = time.substring(0, 2);
        String minute = time.substring(3);

        return Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
    }

    private static String currentDateMethod(int difference){
        String year;
        String month;
        String day;

        int dayInt = Integer.parseInt(currentDay) + difference;

        if (countOfDaysInMonth() >= dayInt){
            year = currentYear;
            month = currentMonth;
            day = String.valueOf(dayInt);
        }
        else{
            if (currentMonth.equals("12")){
                year = String.valueOf(Integer.parseInt(currentYear) + 1);
                month = "01";
            }
            else{
                year = currentYear;
                month = String.valueOf(Integer.parseInt(currentMonth) + 1);
                if (month.length() == 1){
                    month = "0" + month;
                }
            }
            day = "0" + String.valueOf(countOfDaysInMonth() - dayInt);
        }

        return year + "-" + month + "-" + day;
    }

    private static int countOfDaysInMonth(){
        int currentMonthInt = Integer.parseInt(currentMonth);

        if (currentMonthInt == 2){
            return 28;
        }
        else if (currentMonthInt <= 7){
            return currentMonthInt % 2 == 0 ? 30 : 31;
        }
        else{
            return currentMonthInt % 2 == 0 ? 31 : 30;
        }
    }

    public static boolean visitPassed(String date){
        int currentDateSize = (currentDay == null ? 0 : Integer.parseInt(currentDay));
        currentDateSize += (currentMonth == null ? 0 : Integer.parseInt(currentMonth) * 30);
        currentDateSize += (currentYear == null ? 0 : Integer.parseInt(currentYear) * 365);

        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8);

        int dateSize = Integer.parseInt(day);
        dateSize += Integer.parseInt(month) * 30;
        dateSize += Integer.parseInt(year) * 365;

        return currentDateSize <= dateSize;
    }

    public static int theLastDateOfVisit(String time, String date){
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8);

        int dateSize = Integer.parseInt(day);
        dateSize += Integer.parseInt(month) * 30;
        dateSize += Integer.parseInt(year) * 365;

        return 1440 * dateSize + timeInMinutes(time);
    }
}

class similarUserMethods extends DataBaseSQL {
    public static void anyKeyMethod(Boolean anyKey){
        Scanner scan = new Scanner(System.in);
        if (anyKey){
            System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
            scan.nextLine();
        }
    }

    public static void listOfProcedures() {
        if (paidProcedureTitleClients.isEmpty()){
            System.out.println("\nНикто из посетителей не покупал процедуры.");
            return;
        }

        System.out.println("\nСписок процедур:");

        for (String x : procedureTitle){
            if (paidProcedureTitleClients.get(x) == null){
                continue;
            }

            System.out.println("------------");
            System.out.println(x + ":");

            for (String y : paidProcedureTitleClients.get(x)){
                System.out.println(y);
            }
        }
        System.out.println("------------");
    }

    public static void searchClientOrMyInfo(String login, Boolean search){
        Scanner scan = new Scanner(System.in);

        if (search){
            while (true) {
                System.out.print("\nВведите логин посетителя, которого хотите найти: ");
                login = scan.next();

                if (clientLoginId.get(login) != null){
                    break;
                }
                System.out.println("Посетитель с логином '" + login + "' не найден, повторите.");
            }
        }

        String query = "SELECT * FROM clients WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    int height = rs.getInt("height");
                    int weight = rs.getInt("weight");
                    int bloodType = rs.getInt("bloodType");
                    String dateOfBirth = rs.getString("dateOfBirth");

                    System.out.println("\nПосетитель найден:");
                    System.out.println("ID: " + id);;
                    System.out.println("Имя: " + name);
                    System.out.println("Фамилия: " + surname);
                    System.out.println("Рост: " + height + " см");
                    System.out.println("Вес: " + weight + " кг");
                    System.out.println("Группа крови: " + bloodType);
                    System.out.println("Дата рождения: " + dateOfBirth);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске посетителя: " + e.getMessage());
        }
    }

    public static void allProcedures(){
        String sql = "SELECT * FROM procedures";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nВсе процедуры:");

            while (rs.next()) {
                String title = rs.getString("title");
                int cost = rs.getInt("cost");

                System.out.println(title + " - " + cost + ".00 сом");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static void procedureSchedule(){
        System.out.println("\nРасписание к процедурам:");

        for (String x : weekDays) {
            System.out.println("------------------------");
            System.out.println(x + ":");

            for (String y : procedureSchedule.get(x)) {
                System.out.println(y);
            }
        }
        System.out.println("------------------------");
    }

    public static void listOfClients(){
        String sql = "SELECT * FROM clients";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nСписок посетителей: ");

            while (rs.next()) {
                String login = rs.getString("login");
                String name = rs.getString("name");
                String surname = rs.getString("surname");

                System.out.println(login + " " + name + " " + surname);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static void clientPaymentHistory(String login, Boolean search){
        Scanner scan = new Scanner(System.in);

        while (search) {
            System.out.print("\nВведите логин посетителя, которого хотите найти: ");
            login = scan.next();

            if (clientLoginId.get(login) != null){
                search = false;
            }
            else {
                System.out.println("Посетитель с логином '" + login + "' не найден, повторите.");
            }
        }

        try {
            FileReader fileReader = new FileReader(login + "PaidProcedures.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            System.out.println("История оплаты посетителя " + login + ":");
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }
}

public class Main extends GlobalVariables{
    public static void main(String[] args) {
        DataBaseMethodCall();

        SignInMethodCall();

        PDMC_MethodCall();

        System.exit(0);
    }

    private static void DataBaseMethodCall(){
        DataBaseSQL.dataBaseSQL();
        DataBaseTXT.dataBaseTXT();
        DataBaseTXTv2.dataBaseTXTv2();
        DataBaseTime.dataBaseTime();
    }

    private static void SignInMethodCall(){
        currentRole = SignIn.role();
        currentLogin = SignIn.login();
    }

    private static void PDMC_MethodCall(){
        switch (currentRole){
            case "personal":
                Personal.personal();
                break;
            case "director":
                Director.director();
                break;
            case "manager":
                Manager.manager();
                break;
            case "client":
                Client.client();
                break;
        }
    }
}

class SignIn extends GlobalVariables {
    public static String role(){
        Scanner scan = new Scanner(System.in);

        boolean running = true;
        String role = "";

        while (running) {
            running = false;

            System.out.println("\npersonal/director/manager/client");
            System.out.print("Введите тип аккаунта: ");

            role = scan.next();

            switch (role){
                case "personal": break;
                case "director": break;
                case "manager": break;
                case "client": break;
                default:
                    running = true;
                    System.out.println("Неправильный тип аккаунта, повторите.");
            }
        }
        return role;
    }

    public static String login(){
        Scanner scan = new Scanner(System.in);

        System.out.print("Логин: ");
        String login = scan.next();

        System.out.print("Пароль: ");
        String password = scan.next();

        if (!loginPasswordProof(login, password)){
            System.out.println("\nНеправильный логин или пароль.");
            System.exit(0);
        }
        return login;
    }

    private static boolean loginPasswordProof(String login, String password){
        return userRoleLogins.get(currentRole).contains(login) && userLoginPassword.get(login).equals(password);
    }
}

class Personal extends GlobalVariables {
    public static void personal(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\nПриветствую уважаемый Персонал!");

        boolean running = true;
        boolean anyKey = false;

        while (running){
            similarUserMethods.anyKeyMethod(anyKey);
            anyKey = true;

            System.out.println("\nМеню персонала:");

            System.out.println("1. Показать список процедур.");
            System.out.println("2. Найти посетителя.");
            System.out.println("3. Показать все процедуры.");
            System.out.println("4. Показать расписание к процедурам.");
            System.out.println("5. Купить процедуру.");
            System.out.println("6. Найти процедуры.");
            System.out.println("0. Выход.");

            System.out.print("Выбор: ");

            switch (scan.next()) {
                case "1":
                    similarUserMethods.listOfProcedures();
                    break;
                case "2":
                    similarUserMethods.searchClientOrMyInfo("", true);
                    break;
                case "3":
                    similarUserMethods.allProcedures();
                    break;
                case "4":
                    similarUserMethods.procedureSchedule();
                    break;
                case "5":
                    buyProcedure();
                    break;
                case "6":
                    findProcedure();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор, повторите.");
            }
        }
    }

    private static void buyProcedure(){
        Scanner scan = new Scanner(System.in);
        String login;

        while (true) {
            System.out.print("\nВведите логин посетителя, которому хотите купить процедуру: ");
            login = scan.next();

            if (clientLoginId.get(login) != null){
                break;
            }
            System.out.println("Посетитель с логином '" + login + "' не найден, повторите.");
        }

        String title;

        while (true){
            System.out.print("\nВведите процедуру, которую хотите купить посетителю: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Процедура с названием '" + title + "' не найден, повторите.");
        }

        String paidProcedureWeekDayAndTimeDate;

        while (true){
            System.out.println("\nРасписание процедуры '" + title + "':");

            int cnt = 0;
            for (String x : procedureWeekDayAndTime.get(title)){
                System.out.println(++cnt + ". " + x);
            }
            System.out.print("Ваш выбор: ");
            int choose = scan.nextInt();

            if (choose < 1 || choose > cnt){
                System.out.println("Неправильный выбор, повторите.");
            }
            else{
                paidProcedureWeekDayAndTimeDate = procedureWeekDayAndTime.get(title).get(cnt-1);
                break;
            }
        }

        paidProcedureWeekDayAndTimeDate += " " + DataBaseTime.insertDateToPaidProcedure(paidProcedureWeekDayAndTimeDate);

        if (paidProcedureLists.get(login) == null){
            ArrayList <String> AL = new ArrayList<>();
            AL.add(title + " " + procedureTitleCost.get(title) + ".00 сом " + paidProcedureWeekDayAndTimeDate);

            paidProcedureLists.put(login, AL);
        }
        else{
            paidProcedureLists.get(login).add(title + " " + procedureTitleCost.get(title) + ".00 сом " + paidProcedureWeekDayAndTimeDate);
        }

        if (paidProcedureTitleCount.get(login) == null){
            paidProcedureTitleCount.put(login, 1);
        }
        else{
            paidProcedureTitleCount.put(login, paidProcedureTitleCount.get(login) + 1);
        }

        if (paidProcedureTitleClients.get(title) == null){
            ArrayList <String> AL = new ArrayList<>();
            AL.add(login);

            paidProcedureTitleClients.put(title, AL);
        }
        else{
            paidProcedureTitleClients.get(title).add(login);
        }

        try {
            FileWriter fileWriter = new FileWriter(login + "PaidProcedures.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : paidProcedureLists.get(login)){
                bufferedWriter.write(x);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }

        System.out.println("Покупка процедуры прошла успешно!");
    }

    private static void findProcedure(){
        Scanner scan = new Scanner(System.in);
        String title;

        while (true) {
            System.out.print("\nВведите название процедуры: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Процедура с названием '" + title + "' не найден, повторите.");
        }

        System.out.println("Стоимость процедуры: " + procedureTitleCost.get(title) + ".00 сом");
        System.out.println("Количество записанных посетителей: " +
                (paidProcedureTitleCount.get(title) == null ? 0 : paidProcedureTitleCount.get(title)));
    }
}

class Director extends GlobalVariables {
    public static void director(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\nПриветствую дорогой Директор!");

        boolean running = true;
        boolean anyKey = false;

        while (running){
            similarUserMethods.anyKeyMethod(anyKey);
            anyKey = true;

            System.out.println("\nМеню директора:");

            System.out.println("1. Показать список посетителей.");
            System.out.println("2. Найти посетителя.");
            System.out.println("3. Добавить пользователя.");
            System.out.println("4. Удалить пользователя.");
            System.out.println("5. Показать список процедур.");
            System.out.println("6. Показать все процедуры.");
            System.out.println("7. Показать расписание к процедурам.");
            System.out.println("8. Показать историю оплаты посетителя.");
            System.out.println("0. Выход.");

            System.out.print("Ваш выбор: ");

            switch (scan.next()){
                case "1":
                    similarUserMethods.listOfClients();
                    break;
                case "2":
                    similarUserMethods.searchClientOrMyInfo("", true);
                    break;
                case "3":
                    addUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "5":
                    similarUserMethods.listOfProcedures();
                    break;
                case "6":
                    similarUserMethods.allProcedures();
                    break;
                case "7":
                    similarUserMethods.procedureSchedule();
                    break;
                case "8":
                    similarUserMethods.clientPaymentHistory("", true);
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор, повторите.");
            }
        }
    }

    private static void addUser(){
        Scanner scan = new Scanner(System.in);

        String role = SignIn.role();
        String login;

        while (true){
            System.out.print("\nПридумайте новый логин: ");
            login = scan.next();

            if (userLoginRole.get(login) == null){
                break;
            }
            System.out.println("Логин '" + login + "' уже существует, повторите.");
        }
        String password;

        while (true){
            System.out.println("\nРазмер пароля должен быть больше 4 символов");
            System.out.print("Придумайте новый пароль: ");
            password = scan.next();

            if (password.length() >= 4){
                break;
            }
            System.out.println("Пароль не удовлетворяет условием, повторите.");
        }

        insertDataMethods.insertDataToUsers(role, login, password);

        userLoginRole.put(login, role);
        userRoleLogins.get(role).add(login);
        userLoginPassword.put(login, password);

        if (role.equals("client")){
            System.out.println("\nВы добавляете посетителя, поэтому введите информацию о данном посетителе.");

            System.out.print("Имя: ");
            String name = scan.next();

            System.out.print("Фамилия: ");
            String surname = scan.next();

            System.out.print("Рост: ");
            int height = scan.nextInt();

            System.out.print("Вес: ");
            int weight = scan.nextInt();

            System.out.print("Группа крови: ");
            int bloodType = scan.nextInt();

            System.out.print("Дата рождения: ");
            String dateOfBirth = scan.next();

            insertDataMethods.insertDataToClients(login, name, surname, height, weight, bloodType, dateOfBirth);

            clientLoginId.put(login, userRoleLogins.get(role).size());
        }

        System.out.println("\nПользователь успешно добавлен!");
    }

    private static void deleteUser(){
        Scanner scan = new Scanner(System.in);

        boolean running = true;
        String role = "";

        while (running) {
            running = false;

            System.out.println("\npersonal/director/manager/client");
            System.out.print("Введите тип аккаунта пользователя которого хотите удалить: ");

            role = scan.next();

            switch (role){
                case "personal": break;
                case "director": break;
                case "manager": break;
                case "client": break;
                default:
                    running = true;
                    System.out.println("Неправильный тип аккаунта, повторите.");
            }
        }
        String login;

        while (true){
            System.out.print("\nВведите логин пользователя: ");
            login = scan.next();

            if (userLoginRole.get(login) != null){
                break;
            }
            System.out.println("Логин '" + login + "' не найден, повторите.");
        }

        String sql = "DELETE FROM users WHERE login = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
        }

        userLoginRole.remove(login);
        userRoleLogins.get(role).remove(login);
        userLoginPassword.remove(login);

        if (role.equals("client")) {
            String sql2 = "DELETE FROM clients WHERE login = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(sql2)) {
                pstmt.setString(1, login);

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Ошибка при удалении данных: " + e.getMessage());
            }

            clientLoginId.remove(login);
        }

        System.out.println("\nПользователь успешно удалён!");
    }
}

class Manager extends GlobalVariables {
    public static void manager(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\nПриветствую дорогой Менеджер!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            similarUserMethods.anyKeyMethod(anyKey);
            anyKey = true;

            System.out.println("\nМеню менеджера:");

            System.out.println("1. Показать список посетителей.");
            System.out.println("2. Показать количество посетителей.");
            System.out.println("3. Поиск посетителя.");
            System.out.println("4. Изменить цену процедуры.");
            System.out.println("5. Изменить время или название процедуры.");
            System.out.println("6. Показать посетителя с максимальным количеством посещений.");
            System.out.println("7. Показать посетителя с минимальным количеством посещений.");
            System.out.println("0. Выход.");

            System.out.print("Ваш выбор: ");

            switch (scan.next()){
                case "1":
                    similarUserMethods.listOfClients();
                    break;
                case "2":
                    countOfClients();
                    break;
                case "3":
                    similarUserMethods.searchClientOrMyInfo("", true);
                    break;
                case "4":
                    changeProcedureCost();
                    break;
                case "5":
                    changeProcedureTimeOrTitle();
                    break;
                case "6":
                    clientWithMaxVisits();
                    break;
                case "7":
                    clientWithMinVisits();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор, повторите.");
            }
        }
    }

    private static void countOfClients(){
        System.out.println("\nКоличество посетителей: " + userRoleLogins.get("client").size());
    }

    private static void changeProcedureCost(){
        Scanner scan = new Scanner(System.in);

        String title;
        while (true) {
            System.out.print("\nВведите название процедуры, для которой хотите изменить цену: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Название '" + title + "' не существует, повторите.");
        }

        System.out.print("Введите новую цену: ");
        int newCost = scan.nextInt();

        procedureTitleCost.put(title, newCost);

        String sql = "UPDATE procedures SET cost = ? WHERE title = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newCost);
            pstmt.setString(2, title);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка при обновлении данных: " + e.getMessage());
        }

        System.out.println("\nЦена процедуры успешно изменена! ");
    }

    private static void changeProcedureTimeOrTitle(){
        Scanner scan = new Scanner(System.in);

        String title;
        while (true) {
            System.out.print("\nВведите название процедуры, для которой хотите изменить время или название: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Название '" + title + "' не существует, повторите.");
        }

        boolean running = true;

        while (running){
            running = false;

            System.out.println("\nЧто хотите изменить: ");

            System.out.println("1. Время.");
            System.out.println("2. Название.");

            System.out.print("Ваш выбор: ");

            switch (scan.next()){
                case "1":
                    boolean running2 = true;

                    while (running2) {
                        running2 = false;

                        System.out.println("\nИзменение времени: ");

                        System.out.println("1. Добавить время.");
                        System.out.println("2. Удалить время.");

                        System.out.print("Ваш выбор: ");

                        switch (scan.next()) {
                            case "1":
                                String weekDay;

                                while (true){
                                    System.out.print("\nВведите день недели: ");
                                    weekDay = scan.next();

                                    if (DataBaseTime.weekDayOrder(weekDay) > 0){
                                        break;
                                    }
                                    System.out.println("День недели введен неправильно, повторите.");
                                }

                                System.out.print("\nВведите время: ");
                                String time = scan.next();

                                DataBaseTXT.writeFileToProcedureList(title, weekDay, time);

                                System.out.println("\nВремя успешно добавлено!");

                                break;

                            case "2":
                                while (true) {
                                    System.out.println("\nВыберите из расписание время что вы хотите удалить.");
                                    System.out.println("Расписание процедуры '" + title + "':");

                                    int cnt = 0;
                                    for (String x : procedureWeekDayAndTime.get(title)) {
                                        System.out.println(++cnt + ". " + x);
                                    }

                                    System.out.print("Ваш выбор: ");
                                    int choose = scan.nextInt();

                                    if (choose > 0 && choose <= cnt){
                                        String weekDay2 = procedureWeekDayAndTime.get(title).get(choose-1).split("\\s")[0];
                                        String time2 = procedureWeekDayAndTime.get(title).get(choose-1).split("\\s")[1];

                                        System.out.println(title + " " + weekDay2 + " " + time2);
                                        procedureLists.remove(title + " " + procedureWeekDayAndTime.get(title).get(choose-1) + " ");
                                        procedureSchedule.get(weekDay2).remove(title + " " + time2 + " ");
                                        procedureWeekDayAndTime.get(title).remove(choose-1);

                                        break;
                                    }
                                    else{
                                        System.out.println("Неправильный выбор, повторите.");
                                    }
                                }

                                System.out.println("\nВремя успешно удалено!");

                                break;

                            default:
                                running2 = true;
                                System.out.println("Неправльный выбор, повторите.");
                        }
                    }

                    break;

                case "2":
                    String newTitle;

                    while (true) {
                        System.out.print("\nВведите новое название для процедуры: ");
                        newTitle = scan.next();

                        if (procedureTitleCost.get(newTitle) == null){
                            break;
                        }
                        System.out.println("Название '" + title + "' уже существует, повторите.");
                    }

                    String sql = "UPDATE procedures SET title = ? WHERE cost = ?";

                    try (Connection conn = DriverManager.getConnection(DB_URL);
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        pstmt.setString(1, newTitle);
                        pstmt.setInt(2, procedureTitleCost.get(title));

                        pstmt.executeUpdate();

                    } catch (SQLException e) {
                        System.out.println("Ошибка при обновлении данных: " + e.getMessage());
                    }

                    procedureTitleCost.put(newTitle, procedureTitleCost.get(title));
                    procedureTitleCost.remove(title);

                    for (int i=0; i<procedureLists.size(); ++i){
                        String[] list = procedureLists.get(i).split("\\s");

                        if (!list[0].equals(title)){
                            continue;
                        }
                        procedureLists.set(i, newTitle + " " + list[1] + " " + list[2]);
                    }

                    DataBaseTXT.readFile();

                    procedureSchedule.clear();
                    DataBaseTXT.insertToProcedureSchedule();

                    procedureWeekDayAndTime.clear();
                    DataBaseTXT.insertToProcedureWeekDayAndTime();

                    System.out.println("\nНазвание процедуры успешно изменена! ");

                    break;

                default:
                    running = true;
                    System.out.println("Неправильный выбор, повторите.");
            }
        }
    }

    private static void clientWithMaxVisits(){
        System.out.print("\nКлиент с максимальным количеством посещений: ");

        int mx = 0;
        String clientLogin = "";

        for (String x : userRoleLogins.get("client")){
            int cnt = (paidProcedureClientVisit.get(x) == null ? 0 : paidProcedureClientVisit.get(x).size());
            if (mx < cnt){
                mx = cnt;
                clientLogin = x;
            }
        }

        System.out.println(clientLogin);
    }

    private static void clientWithMinVisits(){
        System.out.print("\nКлиент с минимальным количеством посещений: ");

        int mn = Integer.MAX_VALUE;
        String clientLogin = "";

        for (String x : userRoleLogins.get("client")){
            int cnt = (paidProcedureClientVisit.get(x) == null ? 0 : paidProcedureClientVisit.get(x).size());
            if (mn > cnt){
                mn = cnt;
                clientLogin = x;
            }
        }

        System.out.println(clientLogin);
    }
}

class Client extends GlobalVariables {
    public static void client(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\nПриветствую дорогой Посетитель!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            similarUserMethods.anyKeyMethod(anyKey);
            anyKey = true;

            System.out.println("\nМеню клиента:");

            System.out.println("1. Показать историю посещений.");
            System.out.println("2. Показать последнюю дату посещения.");
            System.out.println("3. Показать историю оплаты.");
            System.out.println("4. Показать расписание к процедурам.");
            System.out.println("5. Показать мою информацию.");
            System.out.println("0. Выход.");

            System.out.print("Ваш выбор: ");

            switch (scan.next()){
                case "1":
                    historyOfVisit();
                    break;
                case "2":
                    theLastDateOfVisit();
                    break;
                case "3":
                    similarUserMethods.clientPaymentHistory(currentLogin, false);
                    break;
                case "4":
                    similarUserMethods.procedureSchedule();
                    break;
                case "5":
                    similarUserMethods.searchClientOrMyInfo(currentLogin, false);
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор, повторите.");
            }
        }
    }

    private static void historyOfVisit(){
        System.out.println("\nИстория посещения: ");

        for (String x : paidProcedureClientVisit.get(currentLogin)){
            System.out.println(x);
        }
    }

    private static void theLastDateOfVisit(){
        System.out.print("\nПоследняя история посещения: ");

        int mx = 0;
        String line = "";

        for (String x : paidProcedureClientVisit.get(currentLogin)){
            String date = x.split("\\s")[5];
            String time = x.split("\\s")[4];

            if (mx < DataBaseTime.theLastDateOfVisit(time, date)){
                mx = DataBaseTime.theLastDateOfVisit(time, date);
                line = x;
            }
        }

        System.out.println(line);
    }
}