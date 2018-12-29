
import com.sun.net.httpserver.HttpServer;
import handlers.Login;
import handlers.WelcomePage;

import java.net.InetSocketAddress;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbPass = "4313284";
        String dbUser = "karol";
        Connection connection = new Connector().connect(dbUser, dbPass);
        // create a server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // set routes
         server.createContext("/login", new Login(connection));
         server.createContext("/welcomePage", new WelcomePage(connection));
         server.createContext("/static", new Static());
         server.setExecutor(null); // creates a default executor

        // start listening
        server.start();
    }
}
