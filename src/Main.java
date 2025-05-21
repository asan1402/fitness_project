import java.util.*;
import java.io.*;
import java.sql.*;

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
    public static ArrayList <String> procedureTitles = new ArrayList<>();
    public static HashMap <String, String> clientLoginNameAndSurname = new HashMap<>();

    /* Data Base TXT */
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
    public static HashMap <String, ArrayList <String> > procedureTitleWeekDayAndTime = new HashMap<>();

    /* Data Base TXTv2 */
    public static HashMap <String, ArrayList <String> > paidProcedureLists = new HashMap<>();
    public static HashMap <String, ArrayList <String> > paidProcedureTitleLogins = new HashMap<>();
    public static HashMap <String, ArrayList <String> > paidProcedureTitleLoginTimeAndDate = new HashMap<>();

    /* Data Base Time */
    public static String currentWeekDay, currentTime, currentDate;
    public static String currentDay, currentMonth, currentYear;

    /* Sign In */
    public static String currentRole, currentLogin;
}

class DataBaseSQL extends GlobalVariables{
    public static void dataBaseSQL(){
        createTable();

        if (factorySettings()){
            insertDataToUsers("personal", "p1", "p001");
            insertDataToUsers("director", "d1", "d001");
            insertDataToUsers("manager", "m1", "m001");
            insertDataToUsers("client", "c1", "c001");
            insertDataToUsers("client", "c2", "c002");

            insertDataToProcedures("Массаж", 3000);
            insertDataToProcedures("Йога", 2000);
            insertDataToProcedures("Бассейн", 4000);

            insertDataToClients("c1", "Мирлан", "Кыдыев", 182, 75, 3, "14.02.2007");
            insertDataToClients("c2", "Тагайбек", "Кубатов", 200, 150, 2, "20.10.2006");
        }

        readUsersData();
        readProceduresData();
        readClientsData();
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
                procedureTitles.add(title);
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
                String name = rs.getString("name");
                String surname = rs.getString("surname");

                clientLoginNameAndSurname.put(login, name + " " + surname);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при чтении данных: " + e.getMessage());
        }
    }
}

class DataBaseTXT extends GlobalVariables {
    public static void dataBaseTXT(){
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
        insertToProcedureTitleWeekDayAndTime();
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
        procedureLists.add(title + " " + weekDay + " " + time);
        rewrite();
    }

    public static void rewrite(){
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

    private static void readFile(){
        try {
            FileReader fileReader = new FileReader("procedureLists.txt");
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

    public static void insertToProcedureSchedule(){
        GlobalVariables.insertDataToWeekDays();

        for (String weekDay : weekDays) {
            ArrayList<String> list = new ArrayList<>();

            for (String y : procedureLists) {
                String[] list2 = y.split("\\s");

                if (list2[1].equals(weekDay)){
                    list.add(list2[2] + " " + list2[0]);
                }
            }
            Collections.sort(list);
            ArrayList <String> list2 = new ArrayList<>();

            for (String y : list) {
                String[] list3 = y.split("\\s");
                list2.add(list3[1] + " " + list3[0]);
            }
            procedureSchedule.put(weekDay, list2);
        }
    }

    private static void insertToProcedureTitleWeekDayAndTime(){
        for (String weekDay : weekDays){
            for (String titleAndTime : procedureSchedule.get(weekDay)){
                String title = titleAndTime.split("\\s")[0];
                String time = titleAndTime.split("\\s")[1];;

                if (procedureTitleWeekDayAndTime.get(title) == null){
                    ArrayList <String> AL = new ArrayList<>();
                    AL.add(weekDay + " " + time);

                    procedureTitleWeekDayAndTime.put(title, AL);
                }
                else{
                    procedureTitleWeekDayAndTime.get(title).add(weekDay + " " + time);
                }
            }
        }
    }
}

class DataBaseTXTv2 extends GlobalVariables {
    public static void dataBaseTXTv2(){
        for (String login : userRoleLogins.get("client")){
            createFile(login);

            if (factorySettings(login) && login.equals("c1")){
                writeFileToPaidProcedure(login, "Массаж", 3000, "Понедельник", "13:00", "19.05.2025", true);
                writeFileToPaidProcedure(login, "Бассейн", 4000, "Среда", "12:00", "21.05.2025", true);
                writeFileToPaidProcedure(login, "Йога", 2000, "Вторник", "15:00", "20.05.2025", true);
            }
            else{
                readFile(login);
            }
        }
    }

    private static void createFile(String login){
        try {
            new File(login + "PaidProcedures.txt").createNewFile();

        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    private static boolean factorySettings(String login){
        return new File(login + "PaidProcedures.txt").length() == 0;
    }

    public static void writeFileToPaidProcedure(String login, String title, int cost, String weekDay, String time, String date, Boolean rw){
        if (paidProcedureLists.get(login) == null){
            ArrayList <String> AL = new ArrayList<>();
            AL.add(title + " " + cost + " " + weekDay + " " + time + " " + date);

            paidProcedureLists.put(login, AL);
        }
        else {
            paidProcedureLists.get(login).add(title + " " + cost + " " + weekDay + " " + time + " " + date);
        }

        if (paidProcedureTitleLogins.get(title) == null){
            ArrayList <String> AL = new ArrayList<>();
            AL.add(login);

            paidProcedureTitleLogins.put(title, AL);
        }
        else{
            paidProcedureTitleLogins.get(title).add(login);
        }

        if (paidProcedureTitleLoginTimeAndDate.get(title + " " + login) == null){
            ArrayList <String> AL = new ArrayList<>();
            AL.add(time + " " + date);

            paidProcedureTitleLoginTimeAndDate.put(title + " " + login, AL);
        }
        else {
            paidProcedureTitleLoginTimeAndDate.get(title + " " + login).add(time + " " + date);
        }

        if (rw){
            rewrite(login);
        }
    }

    private static void rewrite(String login){
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
    }

    private static void readFile(String login) {
        try {
            FileReader fileReader = new FileReader(login + "PaidProcedures.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String list[] = line.split("\\s");
                writeFileToPaidProcedure(login, list[0], Integer.parseInt(list[1]), list[2], list[3], list[4], false);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }
}

class DataBaseTime extends GlobalVariables {
    public static void dataBaseTime(){
        ZoneId zone = ZoneId.of("Asia/Bishkek");
        ZonedDateTime now = ZonedDateTime.now(zone);

        insertWeekDay(now);
        insertTimeAndData(now);
        insertDayMonthYear(now);
    }

    private static void insertWeekDay(ZonedDateTime now){
        String weekDay = now.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru"));
        char firstLetter = weekDay.toUpperCase().charAt(0);

        currentWeekDay = firstLetter + weekDay.substring(1);
    }

    private static void insertTimeAndData(ZonedDateTime now){
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        currentTime = now.format(time);
        currentDate = now.format(date);
    }

    private static void insertDayMonthYear(ZonedDateTime now){
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");

        currentDay = now.format(day);
        currentMonth = now.format(month);
        currentYear = now.format(year);
    }

    public static String insertDateToPaidProcedure(String paidProcedureWithWeekDayAndTime){
        String weekDay = paidProcedureWithWeekDayAndTime.split("\\s")[0];
        String time = paidProcedureWithWeekDayAndTime.split("\\s")[1];

        int currentMinutes = 1440 * weekDayOrder(currentWeekDay);
        int minutes = 1440 * weekDayOrder(weekDay);

        currentMinutes += timeInMinutes(currentTime);
        minutes += timeInMinutes(time);

        int difference;

        if (currentMinutes >= minutes){
            difference = weekDayOrder(currentWeekDay) + 7 - weekDayOrder(weekDay);
        }
        else{
            difference = weekDayOrder(weekDay) - weekDayOrder(currentWeekDay);
        }

        return trueDate(difference);
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

    public static int timeInMinutes(String time){
        String hour = time.substring(0, 2);
        String minute = time.substring(3);

        return Integer.parseInt(hour) * 60 + Integer.parseInt(minute);
    }

    private static String trueDate(int difference){
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
            day = "0" + (countOfDaysInMonth() - dayInt);
        }

        return day + "." + month + "." + year;
    }

    private static int countOfDaysInMonth(){
        int currentMonthInt = Integer.parseInt(currentMonth);

        if (currentMonthInt == 2){
            return Integer.parseInt(currentYear) % 4 == 0 ? 29 : 28;
        }
        else if (currentMonthInt <= 7){
            return currentMonthInt % 2 == 0 ? 30 : 31;
        }
        else{
            return currentMonthInt % 2 == 0 ? 31 : 30;
        }
    }

    public static int dateInDays(String date){
        String day = date.substring(0, 2);
        String month = date.substring(3, 5);
        String year = date.substring(6);

        return Integer.parseInt(day) + 30 * Integer.parseInt(month) + 365 * Integer.parseInt(year);
    }
}

public class Main extends GlobalVariables{
    public static void main(String[] args){
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

class similarUserMethods extends GlobalVariables{
    public static void anyKeyMethod(Boolean anyKey){
        Scanner scan = new Scanner(System.in);
        if (anyKey){
            System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
            scan.nextLine();
        }
    }

    public static void listOfProcedures(){
        System.out.println("\nСписок процедур: ");

        for (String title : procedureTitles){
            System.out.println("-------------------");
            System.out.println(title + ":");

            for (String login : paidProcedureTitleLogins.get(title)){
                for (String timeAndDate : paidProcedureTitleLoginTimeAndDate.get(title + " " + login)){
                    System.out.println(login + " " + timeAndDate);
                }
            }
        }
        System.out.println("-------------------");
    }

    public static void searchClientOrMyInfo(String login, Boolean search){
        Scanner scan = new Scanner(System.in);

        if (search){
            while (true) {
                System.out.print("\nВведите логин посетителя, которого хотите найти: ");
                login = scan.next();

                if (userRoleLogins.get("client").contains(login)){
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
                    if (search) {
                        System.out.println("\nИнформация данного посетителя:");
                    }
                    else{
                        System.out.println("\nМоя информация: ");
                    }

                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Имя: " + rs.getString("name"));
                    System.out.println("Фамилия: " + rs.getString("surname"));
                    System.out.println("Рост: " + rs.getInt("height") + " см");
                    System.out.println("Вес: " + rs.getInt("weight") + " кг");
                    System.out.println("Группа крови: " + rs.getInt("bloodType"));
                    System.out.println("Дата рождения: " + rs.getString("dateOfBirth"));
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
        System.out.println("\nСписок посетителей: ");

        for (String login : userRoleLogins.get("client")){
            System.out.println(login + " " + clientLoginNameAndSurname.get(login));
        }
    }

    public static void clientPaymentHistory(String login, Boolean search){
        Scanner scan = new Scanner(System.in);

        while (search) {
            System.out.print("\nВведите логин посетителя, которого хотите найти: ");
            login = scan.next();

            if (userRoleLogins.get("client").contains(login)){
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
            int totalSpent = 0;

            System.out.println("\nИстория оплаты посетителя " + login + ":");
            while ((line = bufferedReader.readLine()) != null) {
                String[] list = line.split("\\s");
                String title = list[0];
                String cost = list[1];
                String date = list[4];

                totalSpent += Integer.parseInt(cost);

                System.out.println("-----------------------------------");
                System.out.println("Процедура: " + title);
                System.out.println("Стоимость: " + cost + ".00 сом");
                System.out.println("Дата: " + date);
            }
            System.out.println("-----------------------------------");
            System.out.println("Итого потрачено: " + totalSpent + ".00 сом.");

            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }
}

class Personal extends GlobalVariables{
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

            if (userRoleLogins.get("client").contains(login)){
                break;
            }
            System.out.println("Посетитель с логином '" + login + "' не найден, повторите.");
        }

        String title;

        while (true){
            System.out.print("\nВведите название процедуры: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Процедура с названием '" + title + "' не найден, повторите.");
        }

        String paidProcedureWithWeekDayAndTimeAndDate;

        while (true){
            System.out.println("\nРасписание процедуры " + title + ":");

            int index = 0;
            for (String x : procedureTitleWeekDayAndTime.get(title)){
                System.out.println(++index + ". " + x);
            }
            System.out.print("Ваш выбор: ");
            int choose = scan.nextInt();

            if (choose < 1 || choose > index){
                System.out.println("Неправильный выбор, повторите.");
            }
            else{
                paidProcedureWithWeekDayAndTimeAndDate = procedureTitleWeekDayAndTime.get(title).get(index-1);
                break;
            }
        }

        paidProcedureWithWeekDayAndTimeAndDate += " " + DataBaseTime.insertDateToPaidProcedure(paidProcedureWithWeekDayAndTimeAndDate);

        String[] list = paidProcedureWithWeekDayAndTimeAndDate.split("\\s");
        String weekDay = list[0];
        String time = list[1];
        String date = list[2];

        DataBaseTXTv2.writeFileToPaidProcedure(login, title, procedureTitleCost.get(title), weekDay, time, date, true);

        System.out.println("\nПроцедура успешно приобретена!");
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
        System.out.println("Количество записанных посетителей: " + paidProcedureTitleLogins.get(title).size());

    }
}

class Director extends GlobalVariables{
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

        DataBaseSQL.insertDataToUsers(role, login, password);

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

            DataBaseSQL.insertDataToClients(login, name, surname, height, weight, bloodType, dateOfBirth);

            clientLoginNameAndSurname.put(login, name + " " + surname);
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

            clientLoginNameAndSurname.remove(login);
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
            System.out.println("5. Изменить время процедуры.");
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
                    changeProcedureTime();
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
            System.out.print("\nВведите название процедуры для изменения её цены: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Название '" + title + "' не существует, повторите.");
        }

        System.out.print("Введите новую цену: ");
        int newCost = scan.nextInt();

        procedureLists.remove(title + " " + procedureTitleCost.get(title));
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

    private static void changeProcedureTime(){
        Scanner scan = new Scanner(System.in);

        String title;

        while (true) {
            System.out.print("\nВведите название процедуры для изменения её времени: ");
            title = scan.next();

            if (procedureTitleCost.get(title) != null){
                break;
            }
            System.out.println("Название '" + title + "' не существует, повторите.");
        }

        boolean running = true;

        while (running) {
            running = false;

            System.out.println("\nЧто вы хотите: ");

            System.out.println("1. Добавить время.");
            System.out.println("2. Удалить время.");

            System.out.print("Ваш выбор: ");

            switch (scan.next()) {
                case "1":
                    String weekDay;

                    while (true) {
                        System.out.print("\nВведите день недели: ");
                        weekDay = scan.next();

                        if (DataBaseTime.weekDayOrder(weekDay) > 0) {
                            break;
                        }
                        System.out.println("День недели введен неправильно, повторите.");
                    }

                    System.out.print("\nВведите время: ");
                    String time = scan.next();

                    if (procedureLists.contains(title + " " + weekDay + " " + time)) {
                        System.out.println("Такое время уже стоит в распианий.");
                        return;
                    }
                    DataBaseTXT.writeFileToProcedureList(title, weekDay, time);

                    System.out.println("\nВремя успешно добавлено!");

                    break;
                case "2":
                    while (true) {
                        System.out.println("\nВыберите из расписания время для удаления.");
                        System.out.println("Расписание процедуры '" + title + "':");

                        int index = 0;
                        for (String x : procedureTitleWeekDayAndTime.get(title)) {
                            System.out.println(++index + ". " + x);
                        }

                        System.out.print("Ваш выбор: ");
                        int choose = scan.nextInt();

                        if (choose > 0 && choose <= index) {
                            procedureLists.remove(title + " " + procedureTitleWeekDayAndTime.get(title).get(choose - 1));
                            break;
                        }
                        else {
                            System.out.println("Неправильный выбор, повторите.");
                        }
                    }

                    System.out.println("\nВремя успешно удалено!");

                    break;
                default:
                    running = true;
                    System.out.println("Неправльный выбор, повторите.");
            }
        }
        procedureSchedule.clear();
        procedureTitleWeekDayAndTime.clear();

        DataBaseTXT.insertToProcedureSchedule();
        DataBaseTXT.insertDataToWeekDays();
    }

    private static void clientWithMaxVisits(){
        int mx = 0;
        String clientLogin = "";

        for (String login : userRoleLogins.get("client")){
            if (paidProcedureLists.get(login) == null){
                continue;
            }

            if (mx < paidProcedureLists.get(login).size()){
                mx = paidProcedureLists.get(login).size();
                clientLogin = login;
            }
        }

        System.out.println("\nКлиент с максимальным количеством посещений: " + clientLogin);
    }

    private static void clientWithMinVisits(){
        int mn = Integer.MAX_VALUE;
        String clientLogin = "";

        for (String login : userRoleLogins.get("client")){
            if (paidProcedureLists.get(login) == null){
                continue;
            }

            if (mn > paidProcedureLists.get(login).size()){
                mn = paidProcedureLists.get(login).size();
                clientLogin = login;
            }
        }

        System.out.println("\nКлиент с минимальным количеством посещений: " + clientLogin);
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
        if (paidProcedureLists.get(currentLogin) == null){
            System.out.println("У вас нет никаких посещений.");
            return;
        }

        try {
            FileReader fileReader = new FileReader(currentLogin + "PaidProcedures.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            System.out.println("\nИстория посещения: ");
            while ((line = bufferedReader.readLine()) != null) {
                String[] list = line.split("\\s");
                String title = list[0];
                String weekDay = list[2];
                String time = list[3];
                String date = list[4];


                System.out.println("-----------------------------------");
                System.out.println("Процедура: " + title);
                System.out.println("День недели: " + weekDay);
                System.out.println("Время: " + time);
                System.out.println("Дата: " + date);
            }
            System.out.println("-----------------------------------");

            bufferedReader.close();

        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    private static void theLastDateOfVisit(){
        if (paidProcedureLists.get(currentLogin) == null){
            System.out.println("У вас нет никаких посещений.");
            return;
        }

        int mxTime = 0, mxDate = 0;

        String ansTitle = "";
        String ansTime = "";
        String ansDate = "";

        for (String title : procedureTitles){
            if (paidProcedureTitleLoginTimeAndDate.get(title + " " + currentLogin) == null){
                continue;
            }
            for (String timeAndDate : paidProcedureTitleLoginTimeAndDate.get(title + " " + currentLogin)){
                String time = timeAndDate.split("\\s")[0];
                String date = timeAndDate.split("\\s")[1];

                if (mxDate < DataBaseTime.dateInDays(date)){
                    mxDate = DataBaseTime.dateInDays(date);
                    mxTime = DataBaseTime.timeInMinutes(time);

                    ansTitle = title;
                    ansDate = date;
                    ansTime = time;
                }
                else if (mxDate == DataBaseTime.dateInDays(date)){
                    if (mxTime < DataBaseTime.timeInMinutes(time)){
                        mxTime = DataBaseTime.timeInMinutes(time);

                        ansTitle = title;
                        ansDate = date;
                        ansTime = time;
                    }
                }
            }
        }

        System.out.println("\nПоследняя дата посещения: ");
        System.out.println("-------------------------");
        System.out.println("Процедура: " + ansTitle);
        System.out.println("Время: " + ansTime);
        System.out.println("Дата: " + ansDate);
        System.out.println("-------------------------");
    }
}