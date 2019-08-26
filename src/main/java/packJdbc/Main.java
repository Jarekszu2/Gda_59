package packJdbc;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String INSERT_QUERY = "insert into students\n" +
            "(name, age, average, alive)\n" +
            "values(?, ?, ?, ?);";

    private static final String CREATE_TABLE_QUERY = "create table if not exists `students` (\n" +
            "`id` int not null auto_increment primary key,\n" +
            "`name` varchar(255) not null,\n" +
            "`age` int not null,\n" +
            "`average` double not null,\n" +
            "alive tinyint not null);";

    private static final String DELETE_QUERY = "delete from `students` where `id` = ?";

    private static final String SELECT_ALL_QUERY = "select * from `students`";

    private static final String SELECT_BY_ID_QUERY = "select * from `students` where `id` = ?";

    private static final String SELECT_BY_NAME_QUERY = "select * from `students` where `name` = ?";

    private static final String SELECT_BETWEEN_FIRST_SECOND_QUERY = "select * from `students` where `age` between ? and ?";

    private static final String DB_HOST = "localhost"; // 127.0.0.1
    private static final String DB_PORT = "3306";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_NAME = "jdbc_students";


    // 1. stworzenie w workbenchu bazy danych(schema): jdbc_students (create database jdbc_students)
    public static void main(String[] args) {
        MysqlDataSource dataSource = new MysqlDataSource();

        dataSource.setPort(Integer.parseInt(DB_PORT));
        dataSource.setUser(DB_USERNAME);
        dataSource.setServerName(DB_HOST);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDatabaseName(DB_NAME);

        try {
            dataSource.setServerTimezone("Europe/Warsaw");
            dataSource.setUseSSL(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Connection connection;
        try {
            connection = dataSource.getConnection();
//            System.out.println("Hurra!");

//            Student student = new Student(null, "Janek", 20, 3.0, true);

//            try(PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
//                statement.execute();
//            }

//            insertStudent(connection, student);

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        System.out.println();
        System.out.println();
        System.out.println("Wybierz działanie:\n" +
                " a) dodaj studenta\n" +
                " b) usuń studenta\n" +
                " c) select all\n" +
                " d) select by id\n" +
                " e) select by name\n" +
                " f) select where age between\n" +
                " w) koniec");

        ScannerWork scannerWork = new ScannerWork();
        boolean flag = false;
        do {
            System.out.println();
            System.out.println("Wybierz:\n a(dodaj)\n b(usuń)\n c(select all)\n d(select by id)\n e(select by name)" +
                    "\n f(selectt where age between)\n w(koniec)");
            char znak = scannerWork.wybierzChar();
            switch(znak) {
                case 'a':
                    System.out.println("Podaj imię:");
                    String name = scannerWork.getString();
                    System.out.println("Podaj age:");
                    int age = scannerWork.getInt();
                    System.out.println("Podaj average:");
                    double average = scannerWork.getDouble();
                    System.out.println("Podaj boolean");
                    boolean flaga = scannerWork.getBoolean();

                    Student student = new Student(null, name, age, average, flaga);
                    try {
                        insertStudent(connection, student);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'b':
                    System.out.println("Podaj id do usunięcia:");
                    int id = scannerWork.getInt();
                    try {
                        deleteStudent(connection, id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'c':
                    try {
                        listAllStudents(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'd':
                    System.out.println("Podaj id do selecta:");
                    int idSelect = scannerWork.getInt();
                    try {
                        getByIdStudent(connection, idSelect);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'e':
                    System.out.println("Podaj name do selecta:");
                    String nameE = scannerWork.getString();
                    try {
                        getByNameStudent(connection, nameE);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'f':
                    System.out.println("Podaj dolny zakres wyszukiwania wieku studenta:");
                    int first = scannerWork.getInt();
                    System.out.println("Podaj górny zakrres wyszukiwania wieku studenta:");
                    int second = scannerWork.getInt();
                    try {
                        listWhereAgeBetween(connection, first, second);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'w':
                    flag = true;
                    break;
            }
        } while(!flag);
    }

    private static void insertStudent(Connection connection, Student student) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, student.getName());
            statement.setInt(2, student.getAge());
            statement.setDouble(3, student.getAverage());
            statement.setBoolean(4, student.isAlive());

            boolean success = statement.execute();

//            if(success) {
//                System.out.println("Sukces!");
//            }
        }
    }

    private static void deleteStudent(Connection connection, int deletedId) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, deletedId);

            boolean success = statement.execute();

            System.out.println("Usunięto studenta o id = " + deletedId);

//            if(success) {
//                System.out.println("Sukces!");
//            }
        }
    }

    private static void listAllStudents(Connection connection) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                 Student student = new Student();

                student.setId(resultSet.getInt(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                studentList.add(student);
            }
        }

        for (Student student : studentList) {
            System.out.println(student);
        }
    }

    private static void getByIdStudent(Connection connection, int searchedId) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            statement.setInt(1, searchedId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {// jeśli jest rekord
                Student student = new Student();

                student.setId(resultSet.getInt(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                System.out.println(student);
            }
            else {
                System.out.println("Nie udało sie odnależć studenta.");
            }
        }
    }

    private static void getByNameStudent(Connection connection, String searchedName) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_QUERY)) {
            // Uwaga: zapis do wyszukiwania Stringów like "tekst": %tekst%
            statement.setString(1, "%" + searchedName + "%");

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {// jeśli jest rekord
                Student student = new Student();

                student.setId(resultSet.getInt(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                System.out.println(student);
            }
            else {
                System.out.println("Nie udało sie odnależć studenta.");
            }
        }
    }

    private static void listWhereAgeBetween(Connection connection, int first, int second) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(SELECT_BETWEEN_FIRST_SECOND_QUERY)) {
            statement.setInt(1, first);
            statement.setInt(2, second);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Student student = new Student();

                student.setId(resultSet.getInt(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));

                studentList.add(student);
            }
        }

        for (Student student : studentList) {
            System.out.println(student);
        }
    }
}


