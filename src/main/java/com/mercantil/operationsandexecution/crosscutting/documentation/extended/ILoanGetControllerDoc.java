package com.mercantil.operationsandexecution.crosscutting.documentation.extended;

import com.mercantil.operationsandexecution.crosscutting.documentation.examples.ApiErrorResponsesExamples;
import com.mercantil.operationsandexecution.crosscutting.documentation.response.LoanResponseDto;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiErrorResponse;
import com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.mercantil.operationsandexecution.crosscutting.constants.CommonConstants.UTILITY_HEADER_TRANSACTION_ID;

/**
 * Documentation contract for the Loan retrieval-by-ID endpoint.
 * <p>
 * Describes path and header parameters and all expected responses.
 *
 * @since 1.0
 */
public interface ILoanGetControllerDoc {

    /**
     * Retrieves the details of a loan by its identifier.
     *
     * @param transactionId transaction identifier for end-to-end traceability (header
     *                      {@value com.mercantil.operationsandexecution.crosscutting.constants.CommonConstants#UTILITY_HEADER_TRANSACTION_ID}).
     * @return a {@link org.springframework.http.ResponseEntity} with an {@link com.mercantil.operationsandexecution.crosscutting.exceptions.response.ApiResponseDto
     */
    @Operation(
            summary = "Obtain information on loans",
            description = "Returns information about a loan",
            tags = {"Loan - Archetype Java Spring Boot"},
            parameters = {
                    @Parameter(
                            name = "id",
                            in = ParameterIn.PATH,
                            required = true,
                            description = "Identifier of the loan in the table DB",
                            schema = @Schema(type = "string")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data details successfully retrieved.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoanResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_400_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_401_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_403_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_404_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "408",
                    description = "Request Timeout",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_408_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_409_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_500_EXAMPLE)
                    )
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Gateway Timeout",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(ApiErrorResponsesExamples.ERROR_504_EXAMPLE)
                    )
            )
    })
    ResponseEntity<ApiResponseDto<Object>> getLoanById(
            @RequestHeader(value = UTILITY_HEADER_TRANSACTION_ID, required = false)
            @NotBlank(message = UTILITY_HEADER_TRANSACTION_ID) String transactionId);

}
