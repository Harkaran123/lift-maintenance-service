# Lift Maintenance - Backend

This project is a Spring Boot backend for managing lift installations and maintenance reminders.

Quick start:

1. Configure the database in `src/main/resources/application.properties` (Postgres).
2. Build with Maven:

```bash
mvn clean package
```

3. Run the application:

```bash
mvn spring-boot:run
```

APIs available:
- `POST /api/lifts` - create lift
- `GET /api/lifts` - list lifts
- `GET /api/lifts/{id}` - get lift
- `PUT /api/lifts/{id}` - update lift
- `DELETE /api/lifts/{id}` - delete lift
- `POST /api/lifts/{liftId}/reminders` - add reminder
- `GET /api/lifts/{liftId}/reminders` - list reminders

Notes:
- The scheduler currently prints notifications to the console. Integrate email/WhatsApp providers as needed.
