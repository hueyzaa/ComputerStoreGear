package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    private final Properties properties = new Properties();
    protected Connection connection;

    private void loadProperties() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            properties.load(input);
        }
    }

    public DBContext() {
        try {
            loadProperties();
            String url = properties.getProperty("DB_URL");
            String user = properties.getProperty("DB_USERNAME");
            String password = properties.getProperty("DB_PASSWORD");
            String driver = properties.getProperty("DB_DRIVER");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            Logger logger = Logger.getLogger("DBContext");
            logger.log(Level.SEVERE, "Unable to connect to database", e);
        }
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        String url = properties.getProperty("DB_URL");
        String user = properties.getProperty("DB_USERNAME");
        String password = properties.getProperty("DB_PASSWORD");
        String driver = properties.getProperty("DB_DRIVER");

        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    static {
        try {
            // Load the driver once when the class is loaded
            Properties staticProps = new Properties();
            try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    throw new IOException("Unable to find database.properties for static initialization");
                }
                staticProps.load(input);
            }
            Class.forName(staticProps.getProperty("DB_DRIVER"));
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Failed to load database driver", e);
            throw new ExceptionInInitializerError(e);
        }
    }
}
