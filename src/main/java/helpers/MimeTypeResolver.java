package helpers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MimeTypeResolver {
    private File file;

    public MimeTypeResolver(File file) {
        this.file = file;
    }

    public String getMimeType() {
        return MimeTypes.mimeTypeMapping.get(getFileExtension().toLowerCase());
    }

    public String getFileExtension(){
        Pattern pattern = Pattern.compile("\\.(\\w+)$");
        Matcher matcher = pattern.matcher(file.getName());
        matcher.find();
        return matcher.group(1);
    }

    public static class MentorWelcomePage implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {


            // get a template file
            JtwigTemplate template = JtwigTemplate.classpathTemplate("HTML/mentorPages/mentorWelcomePage.twig");

            // create a model that will be passed to a template
            JtwigModel model = JtwigModel.newModel();

            // render a template to a string
            String response = template.render(model);

            // send the results to a the client
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        }
    }
}

