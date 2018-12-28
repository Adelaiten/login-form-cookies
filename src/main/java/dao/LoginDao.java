package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginDao implements LoginInterfaceDao {
    private Connection connection;
    public LoginDao(Connection connection){
        this.connection = connection;
    }
    public boolean checkProvidedNameAndPass(String name, String password) throws SQLException{
        String query = "SELECT * FROM user_info WHERE name=? AND password=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return true;
        }
        return false;
    }

    public void saveSessionId(String sessionId, String name) throws SQLException {
        String query = "UPDATE user_info SET session_id=? WHERE name=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, sessionId);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
    }

    public void deleteSessionId(String sessionId) throws SQLException {
        String query = "UPDATE user_info SET session_id = null WHERE session_id=?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, sessionId);
        preparedStatement.executeUpdate();
    }

    public boolean checkIfSessionPresent(String sessionId) throws SQLException {
        String query = "SELECT session_id from user_info WHERE session_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, sessionId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){
            return true;
        }
        return false;
    }
}
