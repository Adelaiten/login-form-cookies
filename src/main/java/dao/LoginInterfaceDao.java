package dao;

import Exceptions.DatabaseException;
import Models.User;


public interface LoginInterfaceDao {
    boolean checkProvidedNameAndPass(String name, String password) throws DatabaseException;
    void saveSessionId(String sessionId, String name) throws DatabaseException;
    User getUserFromDatabase(String sessionId) throws DatabaseException;
    void deleteSessionId(String sessionId) throws DatabaseException;
    boolean checkIfSessionPresent(String sessionId) throws DatabaseException;
}
