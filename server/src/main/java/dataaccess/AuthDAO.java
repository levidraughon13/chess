package dataaccess;

import model.AuthData;


public interface AuthDAO {
    AuthData getAuth(String authToken) throws SQLDataAccessException;
    void deleteAuth(String authToken) throws SQLDataAccessException, UnauthorizedException;
    void clearAuths() throws SQLDataAccessException;
    String createAuth(String username) throws SQLDataAccessException;
}
