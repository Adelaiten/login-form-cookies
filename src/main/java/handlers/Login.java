package handlers;

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
            String sessionId = "";
            System.out.println(sessionId);
            boolean isSession = false;
            try{
                System.out.println("TIBIATIBIA22222");
                isSession = loginDAO.checkIfSessionPresent(sessionId);
            }catch(SQLException e){
                e.printStackTrace();
            }
            if(isSession) {
                System.out.println("TIBIATIBIA3333");
                httpExchange.getResponseHeaders().set("Location", "welcomePage");
                cookie = Optional.of(new HttpCookie(CookieHelper.getSessionCookieName(), sessionId));
                httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
            }else {
                response = generatePage();

            }
//            cookie.ifPresent(httpCookie -> loginDAO.deleteSessionId(httpCookie.getValue()));


        } else if (method.equals("POST") ) {
            response = redirectToWelcomePage(httpExchange, response);
        }
        System.out.println(response + "TIBIA");
        sendResponse(httpExchange, response);
    }

    private String redirectToWelcomePage(HttpExchange httpExchange, String response) throws IOException {
        Map inputs = formDataParser.getData(httpExchange);
        String providedMail = inputs.get("email").toString();
        String providedPassword = inputs.get("pass").toString();
        boolean loginData = false;
        try{
            loginData = loginDAO.checkProvidedNameAndPass(providedMail, providedPassword);
        }catch(SQLException e){
            e.printStackTrace();
        }

        if (loginData) {
            httpExchange.getResponseHeaders().set("Location", "welcomePage");
            String sessionId = String.valueOf(hash(providedMail + providedPassword + LocalDateTime.now().toString()));
            try{
                loginDAO.saveSessionId(sessionId, providedMail);
            }catch(SQLException e){
                e.printStackTrace();
            }

            cookie = Optional.of(new HttpCookie(CookieHelper.getSessionCookieName(), sessionId));
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.get().toString());
        } else {
            response = generatePage();
        }
        return response;
    }

    /**
     * Form data is sent as a urlencoded string. Thus we have to parse this string to get data that we want.
     * See: https://en.wikipedia.org/wiki/POST_(HTTP)
     */


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
