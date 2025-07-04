# Configuration Management

This document explains how the ExpenseTracker application handles configuration and how to override settings for different environments.

## Configuration Hierarchy

The application uses Ktor's configuration system with the following precedence (highest to lowest):

1. **Environment Variables** (with `KTOR_` prefix)
2. **System Properties** (with `ktor.` prefix)
3. **Configuration Files**
4. **Default Values** (hardcoded in code)

## Configuration Files

- `src/main/resources/application.conf` - Default configuration (development)
- `src/main/resources/application-prod.conf` - Production configuration template
- `src/test/resources/application-test.conf` - Test configuration

## Environment Variable Overrides

You can override any configuration value using environment variables with the `KTOR_` prefix:

### Database Configuration
```bash
export KTOR_db.jdbcUrl="jdbc:postgresql://production-db:5432/expensetracker"
export KTOR_db.user="prod_user"
export KTOR_db.password="prod_password"
```

### JWT Configuration
```bash
export KTOR_jwt.secret="your-production-secret-key"
export KTOR_jwt.issuer="expensetracker"
export KTOR_jwt.audience="expensetracker-users"
export KTOR_jwt.realm="expensetracker-app"
```

### Server Configuration
```bash
export KTOR_ktor.deployment.port="8080"
export KTOR_ktor.deployment.host="0.0.0.0"
```

## System Property Overrides

You can also use system properties when starting the application:

```bash
java -Dktor.db.jdbcUrl="jdbc:postgresql://prod-db:5432/expensetracker" \
     -Dktor.jwt.secret="production-secret" \
     -jar expense-tracker.jar
```

## Docker Environment Variables

When running in Docker, you can pass environment variables:

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    image: expense-tracker
    environment:
      - KTOR_db.jdbcUrl=jdbc:postgresql://db:5432/expensetracker
      - KTOR_db.user=postgres
      - KTOR_db.password=password
      - KTOR_jwt.secret=your-secret-key
```

## Production Deployment Example

For production deployment, you should:

1. **Use strong secrets**:
   ```bash
   export KTOR_jwt.secret="$(openssl rand -hex 32)"
   ```

2. **Use production database**:
   ```bash
   export KTOR_db.jdbcUrl="jdbc:postgresql://prod-db.company.com:5432/expensetracker"
   export KTOR_db.user="app_user"
   export KTOR_db.password="secure_password"
   ```

3. **Disable development features**:
   ```bash
   export KTOR_ktor.deployment.watch="[]"
   export KTOR_initializeTestData="false"
   ```

## Configuration in Code

The application reads configuration using Ktor's `ApplicationConfig`:

```kotlin
// In DatabaseFactory.init(config: ApplicationConfig)
val jdbcURL = config.propertyOrNull("db.jdbcUrl")?.getString() ?: "default-url"
val username = config.propertyOrNull("db.user")?.getString() ?: "default-user"
val password = config.propertyOrNull("db.password")?.getString() ?: "default-password"

// In JwtConfig.init(config: ApplicationConfig)
secret = config.propertyOrNull("jwt.secret")?.getString() ?: "default-secret"
```

## Testing Configuration

For tests, the application automatically uses `application-test.conf` and skips database migrations when the test environment is detected.

## Security Best Practices

1. **Never commit secrets** to version control
2. **Use environment variables** for sensitive data
3. **Use strong, unique secrets** in production
4. **Rotate secrets** regularly
5. **Use different secrets** for different environments

## Troubleshooting

### Configuration not being picked up
- Ensure environment variables have the `KTOR_` prefix
- Check that the configuration path matches exactly (e.g., `db.jdbcUrl`)
- Verify the application is restarted after changing environment variables

### Database connection issues
- Check that database environment variables are set correctly
- Verify database is accessible from the application
- Check database credentials and permissions

### JWT authentication issues
- Ensure JWT secret is set and consistent across restarts
- Verify JWT issuer, audience, and realm settings
- Check token expiration settings 