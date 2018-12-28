package dao;

import java.sql.SQLException;

public interface LoginInterfaceDao {
    boolean checkProvidedNameAndPass(String name, String password) throws SQLException;
    void saveSessionId(String sessionId, String name) throws SQLException;
    void deleteSessionId(String sessionId) throws SQLException;
    boolean checkIfSessionPresent(String sessionId) throws SQLException;
}
