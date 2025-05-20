import java.util.*;
import java.sql.*;
import java.io.*;

public class Main{
    public static void main(String[] args){

        DataBaseSQL.dataBaseSQL();

        DataBaseTXT.dataBaseTXT();

        String current_role = SignIn.role();

        String current_login = SignIn.login(current_role);

        if (current_login.equals("exit")){
            System.exit(0);
        }

        switch(current_role) {
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
                Client.client(current_login);
                break;
        }
    }
}

class DataBaseSQL {
    public static HashMap <String, String> userLoginRole = new HashMap<>();

    public static HashMap <String, ArrayList <String> > userRoleLogin = new HashMap<>();

    public static HashMap <String, String> userLoginPassword = new HashMap<>();

    public static HashMap <String, Integer> procedureTitleId = new HashMap<>();

    public static HashMap <String, Integer> clientLoginId = new HashMap<>();

    public static final String DB_URL = "jdbc:sqlite:fitness_project.db";

    public static void dataBaseSQL(){

        /*
        dropTable("users");
        dropTable("procedures");
        dropTable("clients");
        */

        createTable();

        if (factorySettings()) {
            insertDataToUsers("personal", "personal1", "pel1");
            insertDataToUsers("director", "director1", "dir1");
            insertDataToUsers("manager", "manager1", "mar1");
            insertDataToUsers("client", "client1", "clt1");

            insertDataToProcedures("Массаж", 2000);
            insertDataToProcedures("Йога", 5000);
            insertDataToProcedures("Бассейн", 4000);

            insertDataToClients("client1", "Мирлан", "Кыдыев", 182, 75, 3, "14.02.2007");
            insertDataToClients("client2", "Тагайбек", "Кубатов", 200, 150, 2, "20.10.2006");
        }

        readUsersData();
        readProceduresData();
        readClientsData();
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении таблицы: " + e.getMessage());
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

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
        }

        String sqlProcedures = """
            CREATE TABLE IF NOT EXISTS procedures (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                cost INTEGER NOT NULL
            );
            """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProcedures);
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
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

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlClients);
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    private static boolean factorySettings() {
        String sql = "SELECT 1 FROM users LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return !rs.next();
        }
        catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
        return false;
    }

    public static void insertDataToUsers(String role, String login, String password) {
        String sql = "INSERT INTO users(role, login, password) VALUES(?, ?, ?)";

        if (userLoginRole.get(login) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, role);
                pstmt.setString(2, login);
                pstmt.setString(3, password);

                pstmt.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToProcedures(String title, int cost) {
        String sql = "INSERT INTO procedures(title, cost) VALUES(?, ?)";

        if (procedureTitleId.get(title) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title);
                pstmt.setInt(2, cost);

                pstmt.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    public static void insertDataToClients(String login, String name, String surname, Integer height, Integer weight, Integer bloodType, String dateOfBirth) {
        String sql = "INSERT INTO clients(login, name, surname, height, weight, bloodType, dateOfBirth) VALUES(?, ?, ?, ?, ?, ?, ?)";

        if (clientLoginId.get(login) == null) {
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, login);
                pstmt.setString(2, name);
                pstmt.setString(3, surname);
                pstmt.setInt(4, height);
                pstmt.setInt(5, weight);
                pstmt.setInt(6, bloodType);
                pstmt.setString(7, dateOfBirth);

                pstmt.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("❌ Ошибка при вставке данных: " + e.getMessage());
            }
        }
    }

    private static void readUsersData() {
        String sql = "SELECT * FROM users";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readProceduresData() {
        String sql = "SELECT * FROM procedures";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");

                procedureTitleId.put(title, id);
            }
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    private static void readClientsData() {
        String sql = "SELECT * FROM clients";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");

                clientLoginId.put(login, id);
            }
        }
        catch (SQLException e) {
            System.err.println("❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static boolean isRight(String role, String login, String password) {
        if (userLoginRole.get(login) == null){
            return false;
        }

        boolean isTrue = false;

        for (String x : userRoleLogin.get(role)){
            if (x.equals(login)){
                isTrue = true;
                break;
            }
        }

        if (!isTrue){
            return false;
        }

        return userLoginPassword.get(login).equals(password);
    }
}

class DataBaseTXT {
    public static HashMap <String, Boolean> scheduleBoolean = new HashMap<>();

    public static ArrayList <String> proceduresSchedule = new ArrayList<>();

    public static HashMap <String, ArrayList <String> > scheduleList = new HashMap<>();

    public static void dataBaseTXT(){

        /* deleteFile("procedureSchedule"); */

        createFile("procedureSchedule");

        if (factorySettings()) {
            writeFileToSchedule("Массаж", "Понедельник", "13:00");
            writeFileToSchedule("Йога", "Вторник", "15:00");
            writeFileToSchedule("Бассейн", "Среда", "12:00");
            writeFileToSchedule("Массаж", "Среда", "10:00");
            writeFileToSchedule("Йога", "Четверг", "14:00");
            writeFileToSchedule("Бассейн", "Пятница", "15:00");
            writeFileToSchedule("Бассейн", "Вторник", "09:00");
        }

        readFileToSchedule();

        /* deleteFile("scheduleList"); */

        createFile("scheduleList");

        insertToScheduleList();
    }

    private static void deleteFile(String tableTitle){
        new File(tableTitle + ".txt").delete();
    }

    private static void createFile(String tableTitle){
        try {
            new File(tableTitle + ".txt").createNewFile();
        }
        catch (IOException e) {
            System.out.println("Ошибка при создании файла");
        }
    }

    private static boolean factorySettings(){
        boolean isTrue = false;

        try {
            FileReader fileReader = new FileReader("procedureSchedule.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                isTrue = true;
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }

        return !isTrue;
    }

    private static void readFileToSchedule(){
        try {
            FileReader fileReader = new FileReader("procedureSchedule.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                proceduresSchedule.add(line);
                scheduleBoolean.put(line, true);
            }

            bufferedReader.close();
        }
        catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
    }

    public static void writeFileToSchedule(String procedureName, String weekDay, String time){
        String newProcedureSchedule = procedureName + " " + weekDay + " " + time;

        if (scheduleBoolean.get(newProcedureSchedule) != null){
            System.out.println("В это время уже стоит эта же процедура");
            return;
        }

        scheduleBoolean.put(newProcedureSchedule, true);

        proceduresSchedule.add(newProcedureSchedule);

        try {
            FileWriter fileWriter = new FileWriter("procedureSchedule.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String x : proceduresSchedule){
                bufferedWriter.write(x);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
        }
    }

    private static void insertToScheduleList(){
        ArrayList <String> weekDays = new ArrayList<>();

        weekDays.add("Понедельник");
        weekDays.add("Вторник");
        weekDays.add("Среда");
        weekDays.add("Четверг");
        weekDays.add("Пятница");

        for (String x : weekDays) {
            ArrayList<String> list = new ArrayList<>();

            for (String y : proceduresSchedule) {
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

            scheduleList.put(x, list2);
        }
    }
}

class SignIn {
    public static String role(){
        Scanner scan = new Scanner(System.in);

        boolean running = true;
        String role = "";

        while (running) {
            running = false;

            System.out.println("\npersonal/director/manager/client");
            System.out.print("Введите тип аккаунта: ");

            role = scan.nextLine();

            switch (role) {
                case "personal": break;
                case "director": break;
                case "manager": break;
                case "client": break;
                default:
                    running = true;
                    System.out.println("Ошибка: такого типа аккаунта не существует");
            }
        }
        return role;
    }

    public static String login(String role){
        Scanner scan = new Scanner(System.in);

        System.out.print("\nЛогин: ");
        String login = scan.nextLine();

        System.out.print("Пароль: ");
        String password = scan.nextLine();

        if (!DataBaseSQL.isRight(role, login, password)) {
            System.out.println("\nЛогин или пароль введены неправильно.");

            login = "exit";
        }

        return login;
    }
}

class similarUserMethods extends DataBaseSQL {
    public static void listOfProcedures() {

    }

    public static void procedureSchedule(){
        ArrayList <String> weekDays = new ArrayList<>();

        weekDays.add("Понедельник");
        weekDays.add("Вторник");
        weekDays.add("Среда");
        weekDays.add("Четверг");
        weekDays.add("Пятница");

        for (String x : weekDays) {
            System.out.println("\n" + x + ":");

            for (String y : DataBaseTXT.scheduleList.get(x)) {
                System.out.println(y);
            }

        }
    }

    public static void searchClientOrMyInfo(String login, Boolean search){
        Scanner scan = new Scanner(System.in);
        if (search){
            boolean running = true;

            while (running) {
                System.out.print("\nВведите логин посетителя которого хотите найти: ");
                login = scan.nextLine();

                for (String x : userRoleLogin.get("client")){
                    if (login.equals(x)){
                        running = false;
                        break;
                    }
                }

                if (running) {
                    System.out.println("Такого логина не существует!");
                }
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
                    Integer height = rs.getInt("height");
                    Integer weight = rs.getInt("weight");
                    Integer bloodType = rs.getInt("bloodType");
                    String dateOfBirth = rs.getString("dateOfBirth");

                    System.out.println("\nПользователь найден:");
                    System.out.println("ID: " + id);;
                    System.out.println("Имя: " + name);
                    System.out.println("Фамилия: " + surname);
                    System.out.println("Рост: " + height + " см");
                    System.out.println("Вес: " + weight + " кг");
                    System.out.println("Группа крови: " + bloodType);
                    System.out.println("Дата рождения: " + dateOfBirth);

                }
                else{
                    System.out.println("\nПользователь с логином '" + login + "' не найден.");
                }
            }
        }
        catch (SQLException e) {
            System.err.println("\n❌ Ошибка при поиске посетителя: " + e.getMessage());
        }
    }

    public static void allProcedures(){
        String sql = "SELECT * FROM procedures";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nВсе процедуры:");

            while (rs.next()) {
                System.out.printf("%d. %s - %d.00 сом\n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("cost"));
            }
        } catch (SQLException e) {
            System.err.println("\n❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }

    public static void clientsList(){
        String sql = "SELECT * FROM clients";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nСписок посетителей:");

            while (rs.next()) {
                System.out.printf("%d. %s %s %s%n",
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getString("surname"));
            }
        } catch (SQLException e) {
            System.err.println("\n❌ Ошибка при чтении данных: " + e.getMessage());
        }
    }
}

class Personal extends DataBaseSQL {
    public static void personal(){
        Scanner scan = new Scanner(System.in);

        System.out.println("\nПриветствую уважаемый Персонал!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            if (anyKey){
                System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню персонала:");

            System.out.println("1. Показать список процедур");
            System.out.println("2. Найти посетителя");
            System.out.println("3. Показать все процедуры");
            System.out.println("4. Показать расписание к процедурам");
            System.out.println("5. Купить процедуру");
            System.out.println("6. Найти процедуры");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");

            switch (scan.nextLine()) {
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
                    System.out.println("\nНеверный выбор!");
            }
        }
    }

    private static void buyProcedure(){

    }

    private static void findProcedure(){

    }
}

class Director extends DataBaseSQL{
    public static void director(){
        Scanner scan = new Scanner(System.in);

        System.out.println("\nПриветствую дорогой Директор!");
    }
}

class Manager extends DataBaseSQL{
    public static void manager() {
        Scanner scan = new Scanner(System.in);

        System.out.println("\nПриветствую дорогой Менеджер!");

        boolean running = true;
        boolean anyKey = false;

        while (running) {
            if (anyKey){
                System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню менеджера:");

            System.out.println("1. Показать список посетителей");
            System.out.println("2. Показать количество посетителей");
            System.out.println("3. Поиск посетителя");
            System.out.println("4. Изменить цену для процедур");
            System.out.println("5. Изменить время - название процедур");
            System.out.println("6. Показать посетителя с максимальным количеством посещений");
            System.out.println("7. Показать посетителя с минимальным количеством посещений");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");

            switch (scan.nextLine()) {
                case "1":
                    similarUserMethods.clientsList();
                    break;
                case "2":
                    clientsCount();
                    break;
                case "3":
                    similarUserMethods.searchClientOrMyInfo("", true);
                    break;
                case "4":
                    changeCostToProcedure();
                    break;
                case "5":
                    changeTitleToProcedure();
                    break;
                case "6":
                    maxCountOfVisit();
                    break;
                case "7":
                    minCountOfVisit();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор!");
            }
        }
    }

    private static void clientsCount(){
        String sql = "SELECT COUNT(*) FROM clients";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.print("\nКоличество посетителей: ");
            System.out.println(rs.next() ? rs.getInt(1) : 0);

        }
        catch (SQLException e) {
            System.err.println("\n❌ Ошибка при подсчёте строк: " + e.getMessage());
        }
    }

    private static void changeCostToProcedure(){
        Scanner scan = new Scanner(System.in);

        String title;

        while (true) {
            System.out.print("\nВведите название процедуры для изменения её цены: ");
            title = scan.nextLine();

            if (procedureTitleId.get(title) != null){
                break;
            }
            System.out.println("\nТакой процедуры не существует");
        }

        System.out.print("\nВведите новую цену для этой процедуры: ");
        int newCost = scan.nextInt();

        String sql = "UPDATE procedures SET cost = ? WHERE title = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newCost);
            pstmt.setString(2, title);

        }
        catch (SQLException e) {
            System.out.println("\nОшибка при обновлении стоимости: " + e.getMessage());
        }
    }

    private static void changeTitleToProcedure(){
        Scanner scan = new Scanner(System.in);

        String title;

        while (true) {
            System.out.print("\nВведите название процедуры для изменения её названия: ");
            title = scan.nextLine();

            if (procedureTitleId.get(title) != null) {
                break;
            }
            System.out.println("\nТакой процедуры не существует.");
        }
        String newTitle;

        while (true) {
            System.out.print("\nВведите новое название для процедуры: ");
            newTitle = scan.nextLine();

            if (procedureTitleId.get(newTitle) == null) {
                break;
            }
            System.out.println("\nНазвание такой процедуры уже существует");
        }

        String sql = "UPDATE procedures SET title = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setInt(2, procedureTitleId.get(title));

        } catch (SQLException e) {
            System.out.println("\nОшибка при обновлении названия: " + e.getMessage());
        }
    }

    public static void maxCountOfVisit(){

    }

    public static void minCountOfVisit(){

    }
}

class Client extends DataBaseSQL{
    public static void client(String login){
        Scanner scan = new Scanner(System.in);

        System.out.println("\nПриветствую дорогой Посетитель!");

        boolean running = true;
        boolean anyKey = false;

        while (running){
            if (anyKey){
                System.out.print("\nНажмите любую клавишу чтобы продолжить. ");
                scan.nextLine();
            }
            anyKey = true;

            System.out.println("\nМеню клиента:");

            System.out.println("1. Показать историю посещений");
            System.out.println("2. Показать последнюю дату посещения");
            System.out.println("3. Показать историю оплаты");
            System.out.println("4. Показать расписание процедур");
            System.out.println("5. Показать мою информацию");
            System.out.println("0. Выход");

            System.out.print("Выбор: ");

            switch (scan.nextLine()){
                case "1":
                    historyOfVisit();
                    break;
                case "2":
                    theLastDateOfVisit();
                    break;
                case "3":
                    historyOfPayment();
                    break;
                case "4":
                    similarUserMethods.procedureSchedule();
                    break;
                case "5":
                    similarUserMethods.searchClientOrMyInfo(login, false);
                    break;
                case "0":
                    running = false;
                    System.out.println("\nПрограмма завершена, мы будем рады вашему возвращению!");
                    break;
                default:
                    System.out.println("\nНеверный выбор!");
            }
        }
    }

    public static void historyOfVisit(){

    }

    public static void theLastDateOfVisit(){

    }

    public static void historyOfPayment(){

    }
}