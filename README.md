# LicenceForge

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-green.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)

## Modern Open Source Licensing SaaS

LicenceForge is an open-source platform for generating, managing, and verifying software license keys with hardware ID (HWID) locking, expiration date enforcement, and analytics. It's built for developers who want complete control over their license management system with a modern architecture and robust functionality.

## 🌟 Key Features

- **License Key Generation**: Create unique, pattern-based license keys
- **HWID Locking**: Bind licenses to specific hardware to prevent unauthorized use
- **Expiration Management**: Set and enforce license expiration dates
- **Activation Limits**: Control how many machines can use a single license
- **Revocation**: Instantly revoke licenses when needed
- **Detailed Responses**: Get comprehensive information about license status and errors
- **RESTful API**: Integrate with any application using the simple JSON API

## 🔧 Tech Stack

### Backend
- Spring Boot 3.4.4
- Java 21
- Spring Security for API protection
- Spring Data JPA with PostgreSQL
- Flyway for database migrations
- Swagger/OpenAPI for API documentation
- RabbitMQ for message processing
- Clean Architecture principles

### DevOps
- Docker & Docker Compose for development and deployment
- GitHub Actions for CI/CD

## 📚 API Documentation

The API is fully documented with OpenAPI/Swagger, accessible at the running application's `/swagger-ui.html` endpoint.

### Main Endpoints

- **POST /api/v1/licenses** - Create a new license
- **GET /api/v1/licenses/{id}** - Get license by ID
- **GET /api/v1/licenses/key/{licenseKey}** - Get license by key
- **POST /api/v1/licenses/verify** - Verify a license key for a hardware ID
- **POST /api/v1/licenses/activate** - Activate a license for a hardware ID
- **POST /api/v1/licenses/deactivate** - Deactivate a license for a hardware ID
- **PUT /api/v1/licenses/{id}/revoke** - Revoke a license
- **PUT /api/v1/licenses/{id}/reinstate** - Reinstate a revoked license

## 🚀 Getting Started

### Prerequisites
- Java 21
- Docker and Docker Compose
- Maven

### Running with Docker

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/licenceforge.git
   cd licenceforge
   ```

2. Start the application with Docker Compose:
   ```bash
   docker-compose up
   ```

3. The application will be available at http://localhost:8080
    - Swagger UI: http://localhost:8080/swagger-ui.html
    - API docs: http://localhost:8080/v3/api-docs

### Running locally for development

1. Start the required services (PostgreSQL, RabbitMQ) with Docker Compose:
   ```bash
   docker-compose up db rabbitmq
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

## 📝 License Verification Responses

The license verification API provides detailed responses to help you understand the exact status of any license.

### Status Values (successful verifications)

| Status | Description |
|--------|------------|
| `ALREADY_ACTIVATED` | The hardware ID is already activated for this license |
| `AVAILABLE_FOR_ACTIVATION` | The license is valid and can be activated for this hardware ID |

### Error Codes (failed verifications)

| Error Code | Description |
|------------|-------------|
| `LICENSE_NOT_FOUND` | The license key doesn't exist |
| `LICENSE_EXPIRED` | The license has expired |
| `LICENSE_REVOKED` | The license has been revoked |
| `MAX_ACTIVATIONS_REACHED` | Maximum number of activations has been reached |

## 🎯 Use Cases

LicenceForge is perfect for:

- **Independent Software Vendors**: Manage licenses for your desktop applications
- **SaaS Developers**: Control access to on-premise deployments of your software
- **Plugin/Extension Developers**: License your premium plugins for other platforms
- **Enterprise Software**: Manage internal or client licenses for your enterprise applications

## 🛠️ Extending LicenceForge

The project is designed to be highly extensible. Some ideas for extensions:

- Stripe or LemonSqueezy integration for payment processing
- Discord webhook support for license events
- SDKs for different languages (JavaScript, C#, Python)
- Multi-tenant support for license management as a service
- Custom license key pattern generation

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
