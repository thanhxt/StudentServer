package com.acme.ttx.controller;

import com.acme.ttx.controller.StudentDTO.OnCreate;
import com.acme.ttx.service.EmailExistsException;
import com.acme.ttx.service.StudentWriteServiceOld;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.acme.ttx.controller.StudentGetControllerOld.ID_PATTERN;
import static com.acme.ttx.controller.StudentGetControllerOld.REST_PATH;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

/**
 * Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Methoden der Klasse abgebildet werden.
 * <img src="../../../../../../../extras/doc/StudentWriteController.png" alt="Klassendiagramm">
 */
@Controller
@RequestMapping(REST_PATH)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ClassFanOutComplexity", "java:S1075", "java:S6856"})
class StudentWriteController {
    private static final String PROBLEM_PATH = "/problem/";
    private final StudentWriteServiceOld service;
    private final StudentMapper mapper;
    private final UriHelper uriHelper;

    /**
     * Einen neuen Studenten-Datensatz anlegen.
     *
     * @param studentDTO Das Studentenobjekt aus dem eingegangenen Request-Body.
     * @param request Das Request-Objekt, um 'Location' im Response-Header zu erstellen.
     * @return Response mit Statuscode 201 einschließlich Location-Header oder
     *         Statuscode 422, falls Constraints verletzt
     *         sind oder die E-Mail-Adresse bereits existiert oder Statuscode
     *         400, falls syntaktische Fehler im Request-Body
     *         vorliegen.
     */
    @SneakyThrows
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Studenten anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Studenten neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> post(
        @RequestBody @Validated ({Default.class, OnCreate.class})final StudentDTO studentDTO,
        final HttpServletRequest request) {
        log.debug("post: {}", studentDTO);

        final var studentInput = mapper.toStudent(studentDTO);
        final var studentDB = service.create(studentInput);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var location = URI.create(baseUri + '/' + studentDB.getId());
        return created(location).build();
    }

    /**
     * Einen vorhandenen Studenten-Datensatz überschreiben.
     *
     * @param id Matrikelnummer des zu aktualisierenden Studenten.
     * @param studentDTO Das Studentenobjekt aus dem eingegangenen Request-Body.
     */
    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Studenten mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Student nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    void put(
        @PathVariable final UUID id,
        @RequestBody @Valid final StudentDTO studentDTO) {
        log.debug("put: matrikelnummer={}, {}", id, studentDTO);
        final var studentInput = mapper.toStudent(studentDTO);
        service.update(studentInput, id);
    }

    @ExceptionHandler
    ProblemDetail onConstraintViolations(
        final ConstraintViolationException ex,
        final HttpServletRequest request
    ) {
        log.debug("onConstraintViolations: {}", ex.getMessage());

        final var problemDetail = ProblemDetail.forStatusAndDetail(
            UNPROCESSABLE_ENTITY,
            ex.getMessage().replace("create.student.", "").replace("update.student.", "")
        );
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
        log.debug("onEmailExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }


}
