package kg.mega.library_app.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Library Application",
                description = """
                        MethodArgumentNotValidException -> BAD_REQUEST
                        
                        PSQLException -> FORBIDDEN
                        
                        DataNotFoundException -> NOT_FOUND
                        
                        DuplicateException -> CONFLICT
                        
                        MailSendException -> INTERNAL_SERVER_ERROR
                        
                        InadmissibleEditingException -> FORBIDDEN
                        """, version = "1.0.0",
                contact = @Contact(
                        name = "Abdykadyrov Aktan",
                        email = "abdykadyrovkarakol@gmail.com",
                        url = "https://www.instagram.com/_.abdykadyrov_/"
                )
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {
}
