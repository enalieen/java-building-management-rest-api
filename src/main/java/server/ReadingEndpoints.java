package server;

import io.javalin.Javalin;
import vorlage.model.Reading;
import vorlage.model.ReadingDAO;

import java.util.UUID;

public class ReadingEndpoints {

	private final ReadingDAO readingDao;

	public ReadingEndpoints(ReadingDAO readingDao) {
		this.readingDao = readingDao;
	}

	public void register(Javalin app) {

		app.get("/readings", ctx ->
			ctx.json(readingDao.showAllReadings())
		);

		app.get("/readings/{id}", ctx -> {
			try {
				UUID id = UUID.fromString(ctx.pathParam("id"));
				Reading reading = readingDao.findById(id);

				if (reading != null) {
					ctx.json(reading);
				} else {
					ctx.status(404).result("Reading not found");
				}

			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format");
			}
		});

		app.post("/readings", ctx -> {
			try {
				Reading reading = ctx.bodyAsClass(Reading.class);

				System.out.println("Reading received: " + reading);
				System.out.println("Customer: " + reading.getCustomer());

				boolean created = readingDao.create(reading);

				if (created) {
					ctx.status(201).json(reading);
				} else {
					ctx.status(400).result("Reading could not be created");
				}

			} catch (Exception e) {
				e.printStackTrace(); // ВАЖНО
				ctx.status(400).result("ERROR: " + e.getClass().getName() + " - " + e.getMessage());
			}
		});

		app.delete("/readings/{id}", ctx -> {
			try {
				UUID id = UUID.fromString(ctx.pathParam("id"));
				boolean deleted = readingDao.deleteReading(id);

				if (deleted) {
					ctx.status(204);
				} else {
					ctx.status(404).result("Reading not found");
				}

			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format");
			}
		});

		app.put("/readings/{id}", ctx -> {
			try {
				UUID id = UUID.fromString(ctx.pathParam("id"));
				Reading reading = ctx.bodyAsClass(Reading.class);

				// enforce ID from path
				reading.setId(id);

				boolean updated = readingDao.updateReading(reading);

				if (updated) {
					ctx.json(reading);
				} else {
					ctx.status(404).result("Reading not found");
				}

			} catch (IllegalArgumentException e) {
				ctx.status(400).result("Invalid UUID format");
			} catch (Exception e) {
				ctx.status(400).result("Invalid request body");
			}
		});
	}


}
