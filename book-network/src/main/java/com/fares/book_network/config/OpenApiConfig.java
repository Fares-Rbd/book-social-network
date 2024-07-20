package com.fares.book_network.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                contact = @Contact(
                        name="Fares",
                        email="fares.rabboudi@gmail.com",
                        url="https://www.linkedin.com/in/faresrbd/"
                ),
                description = "OpenAPI documentation for Spring Security",
                title = "OpenAPI specification - Fares",
                version = "1.0",
                license = @License(
                        name ="License_Name"
                ),
                termsOfService = "Terms_Of_Service"

        ),
        servers = {
                @Server(
                        description = "Local Env",
                        url = "http://localhost:8080/api/v1"
                ),
                @Server(
                        description = "Prod Env",
                        url = "https://ProdEnv/api/v1"
                )
        },
        security = {
                @SecurityRequirement(
                        name= "bearerAuthentication"
                )
        }
)
@SecurityScheme(
        name = "bearerAuthentication",
        description = "JWT authentication scheme",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
