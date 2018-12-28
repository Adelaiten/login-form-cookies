package dao;

import java.sql.SQLException;

public interface LoginInterfaceDao {
    boolean checkProvidedEmailAndPass() throws SQLException;
    void saveSessionId(String sessionId, String email) throws SQLException;
    void deleteSessionId(String sessionId) throws SQLException;
    boolean checkIfSessionPresent(String sessionId) throws SQLException;
}
