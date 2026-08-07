package server;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import vorlage.databaseConnection.JDBCDBConn;
import vorlage.model.Customer;
import vorlage.model.CustomerDAO;
import vorlage.model.ReadingDAO;
import java.sql.Connection;
import java.util.List;

public class Server {

	private static Javalin app;
	private static Connection conn; // static, чтобы был доступ в статическом контексте

	public static void start() {
		start(8080);
	}

	public static void start(int port) {
		JDBCDBConn db = new JDBCDBConn();
		try {
			conn = db.openConnection("db.properties");
			if (conn != null && !conn.isClosed()) {
				System.out.println("DB connected successfully");
			} else {
				System.err.println("DB connection failed");
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}

		try {
			app = Javalin.create((JavalinConfig config) -> {

				config.staticFiles.add(staticFiles -> {
					staticFiles.hostedPath = "/";
					staticFiles.directory = "frontend";
				});

			}).start(port);
			app.get("/", ctx -> ctx.redirect("/index.html"));
			// Create tables
			db.createAllTables();

			// Create DAO and Endpoints
			CustomerDAO customerDAO = new CustomerDAO(conn);
			ReadingDAO readingDAO = new ReadingDAO(conn);

			CustomerEndpoints customerEndpoints = new CustomerEndpoints(customerDAO);
			customerEndpoints.register(app);

			ReadingEndpoints readingEndpoints = new ReadingEndpoints(readingDAO);
			readingEndpoints.register(app);

			FrontendEndpoints frontendEndpoints = new FrontendEndpoints(customerDAO);
			frontendEndpoints.register(app);

		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	public static void stop() {
		if (app != null) app.stop();
	}

	public static Connection getConnection() {
		return conn;
	}

	public static void main(String[] args) {
		Server.start();
	}
}
