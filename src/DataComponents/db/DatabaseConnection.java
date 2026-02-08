package DataComponents.db;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL =
            "jdbc:postgresql://aws-1-ap-south-1.pooler.supabase.com:5432/postgres?sslmode=require";
    private static final String USER = "postgres.jrpmgnbpbnvbhnffmiol";


    private static volatile String password;

    private static String loadPassword() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null && !env.isBlank()) {
            return env;
        }

        Properties props = new Properties();

        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
                String value = props.getProperty("DB_PASSWORD");
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read DB_PASSWORD from classpath resource /config.properties", e);
        }

        File file = new File("config.properties");
        try (InputStream input = new FileInputStream(file)) {
            props.load(input);
            String value = props.getProperty("DB_PASSWORD");
            if (value == null || value.isBlank()) {
                throw new RuntimeException("DB_PASSWORD is not set in " + file.getAbsolutePath());
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Cannot load DB_PASSWORD. Tried:\n" +
                    " - env var DB_PASSWORD\n" +
                    " - classpath /config.properties\n" +
                    " - file " + file.getAbsolutePath(),
                    e
            );
        }
    }

    private static String getPassword() {
        String local = password;
        if (local == null) {
            synchronized (DatabaseConnection.class) {
                local = password;
                if (local == null) {
                    password = local = loadPassword();
                }
            }
        }
        return local;
    }

    public DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, getPassword());
    }
}