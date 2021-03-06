package cc.blynk.server.dao;

import cc.blynk.server.model.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * Helper class for holding info regarding registered users and profiles.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:02 PM
 */
public class UserRegistry {

    private static final Logger log = LogManager.getLogger(UserRegistry.class);
    private final Map<String, User> users;
    //init user DB if possible

    public UserRegistry(Map<String, User> users) {
        //reading DB to RAM.
        this.users = users;
    }

    public UserRegistry(Map<String, User> users, Map<String, User> usersFromAnotherSource) {
        this.users = users;
        this.users.putAll(usersFromAnotherSource);
    }

    public static Integer getDashIdByToken(User user, String token) {
        for (Map.Entry<Integer, String> dashToken : user.getDashTokens().entrySet()) {
            if (dashToken.getValue().equals(token)) {
                return dashToken.getKey();
            }
        }
        throw new RuntimeException("Error getting dashId for user. FIX/");
    }

    private static String generateNewToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public User getByName(String name) {
        return users.get(name);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    //todo optimize
    public User getUserByToken(String token) {
        for (User user : users.values()) {
            for (String userToken : user.getDashTokens().values()) {
                if (userToken.equals(token)) {
                    return user;
                }
            }
        }
        return null;
    }

    public String getToken(User user, Integer dashboardId) {
        Map<Integer, String> dashTokens = user.getDashTokens();
        String token = dashTokens.get(dashboardId);

        //if token not exists. generate new one
        if (token == null) {
            log.info("Token for user {} and dashId {} not generated yet.", user.getName(), dashboardId);
            token = generateNewToken();
            user.putToken(dashboardId, token);
            log.info("Generated token for user {} and dashId {} is {}.", user.getName(), dashboardId, token);
        } else {
            log.info("Token for user {} and dashId {} generated already. Token {}", user.getName(), dashboardId, token);
        }

        return token;
    }

    public String refreshToken(User user, Integer dashboardId) {
        String token = generateNewToken();
        user.putToken(dashboardId, token);
        log.info("Refreshed token for user {} and dashId {} is {}.", user.getName(), dashboardId, token);
        return token;
    }

    public void createNewUser(String userName, String pass) {
        User newUser = new User(userName, pass);
        users.put(userName, newUser);
    }

}
