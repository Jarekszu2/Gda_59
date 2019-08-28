package packJdbc;

/*
Zasady postępowania.
0. tworzymy w MySQL Workbench database (create database Name;), (use Name;)
0. Intelij IDEA - zakładamy nowy projekt (+ repozytorium na GitHubie) i w POM'ie dodajemy dependencje: mysql i org.projectlombok
0. tworzymy klasę obiektu, który ma być przechowywany w database
   uwaga: jak tworzymy pole ID (pewnie będzie Primary Key, Auto_Increment) to robimy typ Integer, (Long), żeby można było dodawać null'a
   tworzymy konstruktor bez pola ID (żeby łatwo robić insert (jak jest Auto_Increment i null na ID, to ID się samo ustawi)
1. w katalogu resources zakładam plik jdbc.properties i zapisuję w nim parametry bazy danych
2. tworzę klasę MysqlConnectionParameters (pola, konstruktor, metody) - pamiętać o geterach (@Getter)
3. tworzę klasę MysqlConnection (pola, konstruktor, metody)
4. tworzę interface NameQueries (bo w interface wygodnie jest zapisać zmienne bo są od razu final)
   i zapisuję w nim zapytania do database
5. tworzę klasę NameDao(data access object) z polem MysqlConnection, konstruktorem, i metodami
   realizującymi zadania opisane w zapytaniach NameQueries
   - metody składają się z następujących części
   a) nawiązanie połączenia z database
   b) przygotowanie statement w powiązaniu z connection i query(NameQueries)
      możemy wybrać dwie opcje: 1 lub 2 parametry(np.)
      ... connection.prepareStatement(QUERY)
      ... connection.prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS)
   c) ustawienie parametrów statement
      statement.set...(setInt lub setString lub setDouble...) tzn. np. setInt(1, deletedId)
      ustawiamy takie parametry i tyle i w takiej kolejności ile mamy ? w QUERY
   d) wykonanie statement - execute:
      jeśli wybraliśmy:
      b) 1 parametr to wynikiem będzie czy otrzymaliśmy dane czy nie i realizujemy przez:
         - statement.execute();
         - boolean success = statement.execute(); (gdy nie ma wyniku np. insert)
         - ResultSet resultSet = statement.executeQery(); (gdy wynikiem jest np. lista - select)
      b) 2 parametry to wynikiem będzie int, który informuje ile rekordów zostało zmienionych; realizujemy przez:
         - int affectedRecords = statement.executeUpdate();
         - ResultSet resultSet = statement.getGeneratedKeys();
         tego inta możemy jakoś wykorzystać, np. wyświetlić jako potwierdzenie realizacji zadania

      nie ma znaczenia, krórą formę execute wybierzemy
   e) wyciągamy dane z resultSet i zapisujemy je (w zależności od treście QUERY) do objectu, listy, itp.
      (metoda: resultSet.next(), przechodzi do kolejnych pozycji w resultSet (startuje przed resultSet)
      i albo return albo void
 */

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main {


    // 1. stworzenie w workbenchu bazy danych(schema): jdbc_students (create database jdbc_students)
    public static void main(String[] args) {
        StudentDao studentDao;
        try {
            studentDao = new StudentDao();
        } catch (SQLException e) {
            System.err.println("Student dao cannot be created. Mysql error.");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("Configuration file error.");
            System.err.println("Error: " + e.getMessage());
            return;
        }

//        StudentDao studentDao = new StudentDao();
        System.out.println();
        System.out.println();
        System.out.println("Wybierz działanie:\n" +
                " a) dodaj studenta\n" +
                " b) usuń studenta\n" +
                " c) select all\n" +
                " d) select by id\n" +
                " e) select by name\n" +
                " f) select where age between\n" +
                " g) select where name like\n" +
                " w) koniec");

        ScannerWork scannerWork = new ScannerWork();
        boolean flag = false;
        do {
            System.out.println();
            System.out.println("Wybierz:\n a(add student)\n b(delete student)\n c(select all students)\n d(find student by id)\n e(select students by name)" +
                    "\n f(select students where age between)\n g(select students where name like)\n w(koniec)");
            char znak = scannerWork.wybierzChar();
            switch (znak) {
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
                        studentDao.insertStudent(student);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'b':
                    System.out.println("Podaj id do usunięcia:");
                    int id = scannerWork.getInt();
                    try {
                        studentDao.deleteStudentById(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'c':
                    try {
                        List<Student> studentListC = studentDao.listAllStudents();
                        scannerWork.printStudentList(studentListC);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'd':
                    System.out.println("Podaj id do selecta:");
                    int idSelect = scannerWork.getInt();
                    try {
                        Optional<Student> optSt = studentDao.getStudentById(idSelect);
                        if (optSt.isPresent()) {
                            Student stud = optSt.get();
                            System.out.println(stud);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'e':
                    System.out.println("Podaj name do selecta:");
                    String nameE = scannerWork.getString();
                    try {
                        List<Student> studentListE = studentDao.getListByStudentName(nameE);
                        scannerWork.printStudentList(studentListE);
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
                        List<Student> studentListF = studentDao.listWhereAgeBetween(first, second);
                        scannerWork.printStudentList(studentListF);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'g':
                    System.out.println("Podaj frazę do wyszukiwania imienia:");
                    String tekstG = scannerWork.getString();
                    try {
                        List<Student> studentListG = studentDao.listWhereNameLike(tekstG);
                        scannerWork.printStudentList(studentListG);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 'w':
                    flag = true;
                    break;
            }
        } while (!flag);
    }
}







//
//
//
//
//
//        for (Student student : studentList) {
//            System.out.println(student);
//        }
//    }
//
//    private static void listWhereNameLike(Connection connection, String nameLike) throws SQLException {
//        List<Student> studentList = new ArrayList<>();
//        try(PreparedStatement statement = connection.prepareStatement(SELECT_WHERE_NAME_LIKE_QUERY)) {
//            statement.setString(1, "%" + nameLike + "%");
//
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                Student student = new Student();
//
//                student.setId(resultSet.getInt(1));
//                student.setName(resultSet.getString(2));
//                student.setAge(resultSet.getInt(3));
//                student.setAverage(resultSet.getDouble(4));
//                student.setAlive(resultSet.getBoolean(5));
//
//                studentList.add(student);
//            }
//        }
//
//        for (Student student : studentList) {
//            System.out.println(student);
//        }
//    }
//}
//
//
