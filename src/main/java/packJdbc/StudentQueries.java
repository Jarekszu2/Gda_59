package packJdbc;

public interface StudentQueries {
    String INSERT_QUERY = "insert into students\n" +
            "(name, age, average, alive)\n" +
            "values(?, ?, ?, ?);";

    String CREATE_TABLE_QUERY = "create table if not exists `students` (\n" +
            "`id` int not null auto_increment primary key,\n" +
            "`name` varchar(255) not null,\n" +
            "`age` int not null,\n" +
            "`average` double not null,\n" +
            "alive tinyint not null);";

    String DELETE_QUERY = "delete from `students` where `id` = ?";

    String SELECT_ALL_QUERY = "select * from `students`";

    String SELECT_BY_ID_QUERY = "select * from `students` where `id` = ?";

    String SELECT_BY_NAME_QUERY = "select * from `students` where `name` = ?";

    String SELECT_BETWEEN_FIRST_SECOND_QUERY = "select * from `students` where `age` between ? and ?";

    String SELECT_WHERE_NAME_LIKE_QUERY = "select * from `students` where `name` like ?";
}
