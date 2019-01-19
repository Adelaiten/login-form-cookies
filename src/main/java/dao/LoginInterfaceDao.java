package dao;

import Exceptions.DatabaseException;
import Models.User;

import java.sql.SQLException;

public interface LoginInterfaceDao {
    boolean checkProvidedNameAndPass(String name, String password) throws DatabaseException;
    void saveSessionId(String sessionId, String name) throws DatabaseException;
    public User getUserFromDatabase(String sessionId) throws DatabaseException;
    void deleteSessionId(String sessionId) throws DatabaseException;
    boolean checkIfSessionPresent(String sessionId) throws DatabaseException;
}
