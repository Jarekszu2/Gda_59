package packJdbc;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static packJdbc.StudentQueries.*;

public class StudentDao {// data access object
    private MysqlConnection mysqlConnection;

    public StudentDao() throws SQLException, IOException {
        mysqlConnection = new MysqlConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.execute();
            }
        }
    }

    public void insertStudent(Student student) throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, student.getName());
                statement.setInt(2, student.getAge());
                statement.setDouble(3, student.getAverage());
                statement.setBoolean(4, student.isAlive());

//                boolean success = statement.execute(); // jeśli interesuje nas czy w wyniku otrzymaliśmy dane
                int affectedRecords = statement.executeUpdate();  // jeśli interesuje nas ile rekordów zostało zmienionych

                ResultSet resultSet = statement.getGeneratedKeys();

                if (resultSet.next()) {
                    Integer generatedId = resultSet.getInt(1);
                    System.out.println("Został utworzony rekord o identyfikatorze: " + generatedId);
                }
            }
        }
    }

    public boolean deleteStudentById(int deletedId) throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, deletedId);

//                boolean success = statement.execute();
//
//                System.out.println("Usunięto studenta o id = " + deletedId);

                int affectedRecords = statement.executeUpdate();

                if (affectedRecords > 0) {
                    // usuneliśmy rekord
                    System.out.println("Usunięto studenta o id: " + deletedId);
                    System.out.println("Ilość zmienionych rekordów: " + affectedRecords);
                    return true;
                }
            }
        }
        return false;
    }

    public List<Student> listAllStudents() throws SQLException {
        List<Student> studentList = new ArrayList<>();

        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
                ResultSet resultSet = statement.executeQuery();

                loadMultipleStudentsFromResultSet(studentList, resultSet);
            }
        }
        return studentList;
    }

    public Optional<Student> getStudentById(int searchedId) throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
                statement.setInt(1, searchedId);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {// jeśli jest rekord
                    Student student = loadStudentFromResultSet(resultSet);
                    return Optional.of(student);
                } else {
                    System.out.println("Nie udało sie odnależć studenta.");
                }
            }
        }
        return Optional.empty();
    }

    public List<Student> getListByStudentName(String searchedName) throws SQLException {
        List<Student> studentList = new ArrayList<>();

        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_QUERY)) {
                // Uwaga: zapis do wyszukiwania Stringów like "tekst": %tekst%
                statement.setString(1, searchedName);

                ResultSet resultSet = statement.executeQuery();
                loadMultipleStudentsFromResultSet(studentList, resultSet);
            }
        }
        return studentList;
    }

    private Student loadStudentFromResultSet(ResultSet resultSet) throws SQLException {
        Student student = new Student();

        student.setId(resultSet.getInt(1));
        student.setName(resultSet.getString(2));
        student.setAge(resultSet.getInt(3));
        student.setAverage(resultSet.getDouble(4));
        student.setAlive(resultSet.getBoolean(5));
        return student;
    }

    private void loadMultipleStudentsFromResultSet(List<Student> studentList, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            Student student = loadStudentFromResultSet(resultSet);

            studentList.add(student);
        }
    }

    public List<Student> listWhereAgeBetween(int first, int second) throws SQLException {
        List<Student> studentList = new ArrayList<>();

        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BETWEEN_FIRST_SECOND_QUERY)) {
                statement.setInt(1, first);
                statement.setInt(2, second);

                ResultSet resultSet = statement.executeQuery();

                loadMultipleStudentsFromResultSet(studentList, resultSet);
            }
        }
        return studentList;
    }

    public List<Student> listWhereNameLike(String nameLike) throws SQLException {
        List<Student> studentList = new ArrayList<>();

        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_WHERE_NAME_LIKE_QUERY)) {
                statement.setString(1, "%" + nameLike + "%");

                ResultSet resultSet = statement.executeQuery();

                loadMultipleStudentsFromResultSet(studentList, resultSet);
            }
        }
        return studentList;
    }
}

