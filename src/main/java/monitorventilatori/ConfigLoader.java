package monitorventilatori;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;

public class ConfigLoader {
    private Properties properties = new Properties();

    public ConfigLoader(ServletContext context) throws IOException {
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("File 'config.properties' non trovato in /WEB-INF.");
            }
            properties.load(input);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
