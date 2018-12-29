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
import java.util.Optional;

public class WelcomePage implements HttpHandler {
    private CookieHelper cookieHelper;
    private LoginDao loginDAO;
    private FormDataParser formDataParser;
    private Optional<HttpCookie> cookie;
    public WelcomePage(Connection connection){
        this.loginDAO = new LoginDao(connection);
        formDataParser = new FormDataParser();
        cookieHelper = new CookieHelper();
    }


    public void sendResponse(HttpExchange httpExchange, String name, String site) throws IOException {
        // get a template file
        JtwigTemplate template = JtwigTemplate.classpathTemplate("HTML/codecoolerPages/" + site);

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
}
