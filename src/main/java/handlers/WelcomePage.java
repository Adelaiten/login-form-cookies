package handlers;

import Models.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDao;
import helpers.CookieHelper;
import helpers.FormDataParser;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class WelcomePage implements HttpHandler {
    private CookieHelper cookieHelper;
    private LoginDao loginDAO;
    private FormDataParser formDataParser;
    public WelcomePage(Connection connection){
        this.loginDAO = new LoginDao(connection);
        formDataParser = new FormDataParser();
        cookieHelper = new CookieHelper();
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String sessionId = cookieHelper.getSessionIdCookie(httpExchange).get().getValue().replace("\"", "");

        String method = httpExchange.getRequestMethod();
        int userId = 0;
        String userName = "";
        if(method.equals("GET")){

            try{
                User user = loginDAO.getUserFromDatabase(sessionId);
                userId = user.getId();
                userName = user.getName();

            }catch(SQLException e){
                e.printStackTrace();
            }
            if(userId >= 1){
                String site = "welcomePage.twig";

                sendResponseWelcomePage(httpExchange, userName, site);

            }else{
                try{
                    loginDAO.deleteSessionId(sessionId);
                }catch(SQLException e){
                    e.printStackTrace();
                }

                httpExchange.getResponseHeaders().add("Location", "/login");

                httpExchange.sendResponseHeaders(303, 0);
                sendResponseLogin(httpExchange, "login");
            }
        }else if(method.equals("POST")){
            try{
                loginDAO.deleteSessionId(sessionId);
            }catch(SQLException e) {
                e.printStackTrace();
            }

            httpExchange.getResponseHeaders().add("Location", "/login");
            httpExchange.sendResponseHeaders(303, 0);
            sendResponseLogin(httpExchange, "login");
        }
    }

    private void sendResponseWelcomePage(HttpExchange httpExchange, String name, String site) throws IOException {
        // get a template file
        JtwigTemplate template = JtwigTemplate.classpathTemplate("html/" + site);

        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();
        model.with("name", name);
        // render a template to a string
        String response = template.render(model);
        // send the results to a the client
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void sendResponseLogin(HttpExchange httpExchange, String site) throws IOException{
        // get a template file
        JtwigTemplate template = JtwigTemplate.classpathTemplate("html/" + site);

        // create a model that will be passed to a template
        JtwigModel model = JtwigModel.newModel();
        // render a template to a string
        String response = template.render(model);
        // send the results to a the client
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}