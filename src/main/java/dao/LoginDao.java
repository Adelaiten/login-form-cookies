package dao;

import Exceptions.DatabaseException;
import Models.User;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginDao implements LoginInterfaceDao {
    private Connection connection;
    public LoginDao(Connection connection){
        this.connection = connection;
    }

    public boolean checkProvidedNameAndPass(String name, String password) throws DatabaseException {
        String query = "SELECT * FROM user_info WHERE name=? AND password=?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return false;
    }

    public User getUserFromDatabase(String sessionId) throws DatabaseException{
        String query = "SELECT * FROM user_info WHERE session_id = ?;";
        User user = new User();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            fillUserObject(sessionId, user, resultSet);
        }catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

        return user;
    }

    private void fillUserObject(String sessionId, User user, ResultSet resultSet) throws SQLException {
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String password = resultSet.getString("password");
            user.setId(id);
            user.setName(name);
            user.setPassword(password);
            user.setSessionId(sessionId);
        }
    }

    public void saveSessionId(String sessionId, String name) throws DatabaseException {
        String query = "UPDATE user_info SET session_id=? WHERE name=?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sessionId);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        }catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

    }

    public void deleteSessionId(String sessionId) throws DatabaseException {
        String query = "UPDATE user_info SET session_id = null WHERE session_id=?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sessionId);
            preparedStatement.executeUpdate();
        }catch(SQLException e) {
            throw new DatabaseException(e.getMessage());
        }

    }

    public boolean checkIfSessionPresent(String sessionId) throws DatabaseException {
        String query = "SELECT session_id from user_info WHERE session_id = ?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sessionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                System.out.println(resultSet.getString("session_id"));
                return true;
            }
        }catch(SQLException e){
            throw new DatabaseException(e.getMessage());
        }

        return false;
    }
}
