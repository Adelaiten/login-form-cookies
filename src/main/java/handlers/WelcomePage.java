package handlers;

import Exceptions.DatabaseException;
import Models.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDao;
import helpers.CookieHelper;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;

public class WelcomePage implements HttpHandler {
    private CookieHelper cookieHelper;
    private LoginDao loginDAO;

    public WelcomePage(Connection connection){
        this.loginDAO = new LoginDao(connection);
        cookieHelper = new CookieHelper();
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String sessionId = cookieHelper.getSessionIdCookie(httpExchange).get().getValue().replace("\"", "");

        String method = httpExchange.getRequestMethod();

        if(method.equals("GET")){
            handleGetMethod(httpExchange, sessionId);
        }else if(method.equals("POST")){
            redirectToLoginPage(httpExchange, sessionId);
        }
    }

    private void redirectToLoginPage(HttpExchange httpExchange, String sessionId) throws IOException {
        try {
            loginDAO.deleteSessionId(sessionId);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        httpExchange.getResponseHeaders().add("Location", "/login");
        httpExchange.sendResponseHeaders(303, 0);
        sendResponseLogin(httpExchange);
    }

    private void handleGetMethod(HttpExchange httpExchange, String sessionId) throws IOException {
        int userId = 0;
        String userName = "";
        try{
            User user = loginDAO.getUserFromDatabase(sessionId);
            userId = user.getId();
            userName = user.getName();

        }catch(DatabaseException e){
            e.printStackTrace();
        }
        if(userId >= 1){
            sendResponseWelcomePage(httpExchange, userName);

        }else{
            redirectToLoginPage(httpExchange, sessionId);
        }
    }


    private void sendResponseWelcomePage(HttpExchange httpExchange, String name) throws IOException {
        // get a template file
        JtwigTemplate template = JtwigTemplate.classpathTemplate("html/welcomePage.twig");
        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();
        model.with("name", name);
        // render a template to a string
        sendResponse(httpExchange, template, model);
    }


    private void sendResponseLogin(HttpExchange httpExchange) throws IOException{
        // get a template file
        JtwigTemplate template = JtwigTemplate.classpathTemplate("html/index.twig");
        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();
        // render a template to a string
        sendResponse(httpExchange, template, model);
    }


    private void sendResponse(HttpExchange httpExchange, JtwigTemplate template, JtwigModel model) throws IOException {
        String response = template.render(model);
        // send the results to a the client
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
