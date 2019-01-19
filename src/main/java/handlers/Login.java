package handlers;

import Exceptions.DatabaseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.LoginDao;
import helpers.CookieHelper;
import helpers.FormDataParser;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class Login implements HttpHandler {
    private CookieHelper cookieHelper;
    private LoginDao loginDAO;
    private FormDataParser formDataParser;
    private Optional<HttpCookie> cookie;


    public Login(Connection connection) {
        this.loginDAO = new LoginDao(connection);
        formDataParser = new FormDataParser();
        cookieHelper = new CookieHelper();
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();

        if(method.equals("GET")) {
            cookie = cookieHelper.getSessionIdCookie(httpExchange);
            String sessionId= "";
            boolean isSession = false;

            if (cookie.isPresent()) {
                sessionId = getSessionId(httpExchange);
                isSession = checkIfSessionExist(sessionId, isSession);
            }

            if(isSession) {
                redirectToWelcomePageIfSessionPresent(httpExchange, sessionId);
            }else {
                response = generatePage();

            }
        } else if (method.equals("POST") ) {
            response = loginToWelcomePage(httpExchange, response);
        }

        sendResponse(httpExchange, response);
    }

    private void redirectToWelcomePageIfSessionPresent(HttpExchange httpExchange, String sessionId) {
        httpExchange.getResponseHeaders().set("Location", "welcomePage");
        cookie = Optional.of(new HttpCookie(CookieHelper.getSessionCookieName(), sessionId));
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
    }

    private boolean checkIfSessionExist(String sessionId, boolean isSession) {
        try {
            isSession = loginDAO.checkIfSessionPresent(sessionId);
        } catch(DatabaseException e) {
            e.printStackTrace();
        }
        return isSession;
    }

    private String getSessionId(HttpExchange httpExchange) {
        return cookieHelper.getSessionIdCookie(httpExchange).get().getValue().replace("\"", "");
    }


    private String loginToWelcomePage(HttpExchange httpExchange, String response) throws IOException {
        Map inputs = formDataParser.getData(httpExchange);
        String providedName = inputs.get("name").toString();
        String providedPassword = inputs.get("pass").toString();
        boolean loginData = false;
        try{
            loginData = loginDAO.checkProvidedNameAndPass(providedName, providedPassword);
        }catch(DatabaseException e){
            e.printStackTrace();
        }

        if (loginData) {
            httpExchange.getResponseHeaders().set("Location", "welcomePage");
            String sessionId = String.valueOf(hash(providedName + providedPassword + LocalDateTime.now().toString()));
            try{
                loginDAO.saveSessionId(sessionId, providedName);
            }catch(DatabaseException e){
                e.printStackTrace();
            }

            cookie = Optional.of(new HttpCookie(CookieHelper.getSessionCookieName(), sessionId));
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
        } else {
            response = generatePage();
        }
        return response;
    }


    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(301, response.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }


    private long hash(String string) {
        long h = 1125899906842597L; // prime
        int len = string.length();

        for (int i = 0; i < len; i++) {
            h = 31*h + string.charAt(i);
        }
        return h;
    }


    private String generatePage(){
        JtwigTemplate template = JtwigTemplate.classpathTemplate("html/index.twig");
        JtwigModel model = JtwigModel.newModel();

        return template.render(model);
    }


}
