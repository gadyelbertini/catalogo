package br.com.fatec.catalogo.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Simple runner to apply an SQL migration file via JDBC.
 * Usage:
 *  - java -cp target/classes;target/dependency/* br.com.fatec.catalogo.tools.DbMigrationRunner [jdbcUrl] [user] [password] [sqlPathOrResource]
 * If jdbcUrl/user/password are omitted, it will try to read them from classpath application.properties
 * (spring.datasource.url, spring.datasource.username, spring.datasource.password).
 */
public class DbMigrationRunner {

    public static void main(String[] args) throws Exception {
        String url = null;
        String user = null;
        String pass = null;
        String sqlSource = "classpath:/db/migration/V2__add_quantidade_data_atualizacao.sql";

        if (args.length >= 1) url = args[0];
        if (args.length >= 2) user = args[1];
        if (args.length >= 3) pass = args[2];
        if (args.length >= 4) sqlSource = args[3];

        if (url == null || user == null || pass == null) {
            Properties p = new Properties();
            try (InputStream is = DbMigrationRunner.class.getResourceAsStream("/application.properties")) {
                if (is != null) {
                    p.load(is);
                    url = url == null ? p.getProperty("spring.datasource.url") : url;
                    user = user == null ? p.getProperty("spring.datasource.username") : user;
                    pass = pass == null ? p.getProperty("spring.datasource.password") : pass;
                }
            }
        }

        if (url == null || user == null) {
            System.err.println("Database URL/credentials not provided and not found in application.properties.");
            System.err.println("Usage: DbMigrationRunner [jdbcUrl] [user] [password] [sqlPathOrResource]");
            System.exit(2);
        }

        System.out.println("Connecting to: " + url);
        String sql = readSql(sqlSource);
        if (sql == null || sql.isBlank()) {
            System.err.println("SQL source not found or empty: " + sqlSource);
            System.exit(3);
        }

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            conn.setAutoCommit(false);
            try (Statement st = conn.createStatement()) {
                // naive split by semicolon; skip empty statements and SQL comments
                String[] parts = sql.split(";\n");
                for (String part : parts) {
                    String stmt = part.trim();
                    if (stmt.isEmpty()) continue;
                    // remove leading -- comments lines
                    stmt = java.util.Arrays.stream(stmt.split("\\n"))
                            .filter(line -> !line.trim().startsWith("--"))
                            .collect(Collectors.joining("\n")).trim();
                    if (stmt.isEmpty()) continue;
                    System.out.println("Executing SQL statement...\n" + (stmt.length() > 200 ? stmt.substring(0, 200) + "..." : stmt));
                    st.execute(stmt);
                }
                conn.commit();
                System.out.println("Migration applied successfully.");
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private static String readSql(String src) throws Exception {
        if (src.startsWith("classpath:")) {
            String resource = src.substring("classpath:".length());
            try (InputStream is = DbMigrationRunner.class.getResourceAsStream(resource)) {
                if (is == null) return null;
                try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    return r.lines().collect(Collectors.joining("\n"));
                }
            }
        } else {
            // treat as filesystem path
            try (InputStream is = java.nio.file.Files.newInputStream(java.nio.file.Paths.get(src))) {
                try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    return r.lines().collect(Collectors.joining("\n"));
                }
            }
        }
    }
}
