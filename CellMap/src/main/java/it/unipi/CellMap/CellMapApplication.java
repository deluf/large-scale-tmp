package it.unipi.CellMap;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition( // For swagger
		info = @Info(
				title = "CellMap API",
				description = "API for the CellMap application",
				version = "1.0"
		)
)
@SecurityScheme( // For swagger
		name = "basicAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "basic",
		description = "Basic Authentication using email and password"
)
public class CellMapApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(CellMapApplication.class, args);
	}

}
