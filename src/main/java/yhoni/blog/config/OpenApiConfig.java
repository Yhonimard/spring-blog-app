package yhoni.blog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(
    contact = @Contact(
        name = "yhonimard",
        email = "yhoni2103@gmail.com",
        url = "https://yhonimard.online"
    ),
    description = "open api documentation for my-blog-app application",
    title = "open api spesification - yhonimard",
    version = "1.0.0",
    termsOfService = "Terms of service"
),
servers = {
    @Server(
        description = "Local ENV",
        url = "http://localhost:5000"
    ),
    @Server(
        description = "prod ENV",
        url = "https://blog-app.yhonimard.online/api"
    )
    
}

// security = @SecurityRequirement(name = "bearerAuth")

)


@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer", 
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
