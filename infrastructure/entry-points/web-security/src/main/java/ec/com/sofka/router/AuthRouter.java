package ec.com.sofka.router;

import ec.com.sofka.data.AuthRequest;
import ec.com.sofka.data.RegisterRequest;
import ec.com.sofka.exceptions.GlobalErrorHandler;
import ec.com.sofka.handler.AuthHandler;
import ec.com.sofka.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class AuthRouter {
    private final AuthHandler authHandler;
    private final GlobalErrorHandler globalErrorHandler;
    private final ValidationService validationService;

    public AuthRouter(AuthHandler authHandler, GlobalErrorHandler globalErrorHandler, ValidationService validationService) {
        this.authHandler = authHandler;
        this.globalErrorHandler = globalErrorHandler;
        this.validationService = validationService;
    }


    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/user/create",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            tags = {"Users"},
                            operationId = "create",
                            summary = "Create a new user",
                            description = "Creates a new user with firstname, lastname, email and password",
                            requestBody = @RequestBody(
                                    description = "User creation details",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RegisterRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User successfully created",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Bad request, validation error or missing required fields",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Username could already exist",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/user/authenticate",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            tags = {"authenticate"},
                            operationId = "login",
                            summary = "Login",
                            description = "Login with email and password",
                            requestBody = @RequestBody(
                                    description = "Login",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RegisterRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Login successfully",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterRequest.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Bad request, validation error or missing required fields",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Username could already exist",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRoutes() {
        return RouterFunctions
                .route(RequestPredicates.POST("/api/v1/user/create")
                        .and(accept(MediaType.APPLICATION_JSON)), this::register)
                .andRoute(RequestPredicates.POST("/api/v1/user/authenticate")
                        .and(accept(MediaType.APPLICATION_JSON)), this::login);
    }


    public Mono<ServerResponse> register(ServerRequest request) {

        return request.bodyToMono(RegisterRequest.class)
                .flatMap(dto -> validationService.validate(dto, RegisterRequest.class))
                .flatMap(authHandler::register)
                .flatMap(userDTO -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO))
                .onErrorResume(ex -> globalErrorHandler.handleException(request.exchange(), ex));

    }
    public Mono<ServerResponse> login(ServerRequest request) {

        return request.bodyToMono(AuthRequest.class)
                .flatMap(dto -> validationService.validate(dto, AuthRequest.class))
                .flatMap(authHandler::authenticate)
                .flatMap(authResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(authResponse))
                .onErrorResume(ex -> globalErrorHandler.handleException(request.exchange(), ex));

    }
}
