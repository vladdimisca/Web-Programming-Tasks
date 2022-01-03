package ro.unibuc.springlab8example1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ro.unibuc.springlab8example1.domain.User;
import ro.unibuc.springlab8example1.domain.UserDetails;
import ro.unibuc.springlab8example1.domain.UserType;
import ro.unibuc.springlab8example1.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User save(User user) {
        String saveUserSql = "INSERT INTO users (username, full_name, user_type, account_created) VALUES (?,?,?,?)";
        jdbcTemplate.update(saveUserSql, user.getUsername(), user.getFullName(), user.getUserType().name(), LocalDateTime.now());

        User savedUser = getUserWith(user.getUsername());
        UserDetails userDetails = user.getUserDetails();

        if (userDetails != null) {
            String saveUserDetailsSql = "INSERT INTO user_details (cnp, age, other_information) VALUES (?, ?, ?)";
            jdbcTemplate.update(saveUserDetailsSql, userDetails.getCnp(), userDetails.getAge(), userDetails.getOtherInformation());

            UserDetails savedUserDetails = getUserDetailsWith(userDetails.getCnp());
            savedUser.setUserDetails(savedUserDetails);

            String saveUsersUserDetails = "INSERT INTO users_user_details (users, user_details) VALUES (?, ?)";
            jdbcTemplate.update(saveUsersUserDetails, savedUser.getId(), savedUserDetails.getId());
        }

        return savedUser;
    }

    public User get(String username) {
        // TODO : homework: use JOIN to fetch all details about the user
        String selectSql =
                "SELECT u.*, d.* " +
                "FROM users u " +
                "JOIN users_user_details ud ON u.id = ud.users " +
                "JOIN user_details d ON ud.user_details = d.id " +
                "WHERE u.username = ?";

        return jdbcTemplate.query(selectSql, getEntireUserRowMapper(), username).stream().findFirst().orElseThrow(() ->
                new UserNotFoundException("User not found!"));
    }

    private User getUserWith(String username) {
        String selectSql = "SELECT * from users WHERE users.username = ?";
        List<User> users = jdbcTemplate.query(selectSql, getUserRowMapper(), username);

        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        return users.get(0);
    }

    private UserDetails getUserDetailsWith(String cnp) {
        String selectSql = "SELECT * from user_details WHERE user_details.cnp = ?";
        List<UserDetails> details = jdbcTemplate.query(selectSql, getUserDetailsRowMapper(), cnp);

        if (details.isEmpty()) {
            throw new UserNotFoundException("User details not found!");
        }

        return details.get(0);
    }

    public User updateById(Long id, User user) {
        String updateUsersSql =
                "UPDATE users " +
                "SET username = ?, full_name = ? " +
                "WHERE id = ?";
        String updateUserDetailsSql =
                "UPDATE user_details d " +
                "JOIN users_user_details ud ON d.id = ud.user_details " +
                "JOIN users u ON ud.users = u.id " +
                "SET d.cnp = ?, d.age = ?, d.other_information = ? " +
                "WHERE u.id = ?";

        jdbcTemplate.update(updateUsersSql, user.getUsername(), user.getFullName(), id);

        UserDetails userDetails = user.getUserDetails();
        jdbcTemplate.update(
                updateUserDetailsSql, userDetails.getCnp(), userDetails.getAge(), userDetails.getOtherInformation(), id);

        return getById(id);
    }

    public void deleteById(Long id) {
        String deleteUsersUserDetailsSql = "DELETE FROM users_user_details WHERE users = ?";
        String deleteUsersSql = "DELETE FROM users WHERE id = ?";
        String deleteUserDetailsSql =
                "DELETE d FROM user_details d " +
                "LEFT JOIN users_user_details ud ON d.id = ud.user_details " +
                "WHERE ud.id IS NULL";

        jdbcTemplate.update(deleteUsersUserDetailsSql, id);
        jdbcTemplate.update(deleteUsersSql, id);
        jdbcTemplate.update(deleteUserDetailsSql);
    }

    public List<User> getByType(UserType type) {
        String selectSql =
                "SELECT u.*, d.* " +
                "FROM users u " +
                "JOIN users_user_details ud ON u.id = ud.users " +
                "JOIN user_details d ON ud.user_details = d.id " +
                "WHERE u.user_type = ?";

        return jdbcTemplate.query(selectSql, getEntireUserRowMapper(), type.name());
    }

    public Boolean existsById(Long id) {
        String selectSql = "SELECT EXISTS(SELECT 1 FROM users WHERE id = ?)";
        return jdbcTemplate.queryForObject(selectSql, Boolean.class, id);
    }

    public Boolean existsByUsername(String username) {
        String selectSql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
        return jdbcTemplate.queryForObject(selectSql, Boolean.class, username);
    }

    public Boolean existsByCnp(String cnp) {
        String selectSql = "SELECT EXISTS(SELECT 1 FROM user_details WHERE cnp = ?)";
        return jdbcTemplate.queryForObject(selectSql, Boolean.class, cnp);
    }

    public User getById(Long id) {
        String selectSql =
                "SELECT u.*, d.* " +
                "FROM users u " +
                "JOIN users_user_details ud ON u.id = ud.users " +
                "JOIN user_details d ON ud.user_details = d.id " +
                "WHERE u.id = ?";

        return jdbcTemplate.query(selectSql, getEntireUserRowMapper(), id).stream().findFirst().orElseThrow(() ->
                new UserNotFoundException("User not found!"));
    }

    private RowMapper<User> getUserRowMapper() {
        return (resultSet, rowNo) -> User.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .fullName(resultSet.getString("full_name"))
                .userType(UserType.valueOf(resultSet.getString("user_type")))
                .build();
    }

    private RowMapper<UserDetails> getUserDetailsRowMapper() {
        return (resultSet, rowNo) -> UserDetails.builder()
                .id(resultSet.getLong("id"))
                .cnp(resultSet.getString("cnp"))
                .age(resultSet.getInt("age"))
                .otherInformation(resultSet.getString("other_information"))
                .build();
    }

    private RowMapper<User> getEntireUserRowMapper() {
        return (resultSet, rowNo) -> {
            UserDetails userDetails = UserDetails.builder()
                    .id(resultSet.getLong("d.id"))
                    .cnp(resultSet.getString("d.cnp"))
                    .age(resultSet.getInt("d.age"))
                    .otherInformation(resultSet.getString("d.other_information"))
                    .build();

            return User.builder()
                    .id(resultSet.getLong("u.id"))
                    .username(resultSet.getString("u.username"))
                    .fullName(resultSet.getString("u.full_name"))
                    .userType(UserType.valueOf(resultSet.getString("u.user_type")))
                    .userDetails(userDetails)
                    .build();
        };
    }
}
