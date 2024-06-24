package com.acme.ttx.controller;


import com.acme.ttx.entity.Student;
import com.acme.ttx.security.JwtService;
import com.acme.ttx.service.StudentReadService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.acme.ttx.controller.StudentGetControllerOld.REST_PATH;
import static org.apache.tomcat.websocket.Constants.UNAUTHORIZED;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

/**
 * Eine Controller-Klasse bildet die Rest-Schnittstelle.
 * <img src="../../../../../../../extras/doc/StudentGetController.png" alt="Klassendiagramm">
 */
@RestController
@RequestMapping(REST_PATH)
@OpenAPIDefinition(info = @Info(title = "Student API", version = "v1"))
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S1075")
public class StudentGetController {
    /**
     * Basispfad für die Rest-Schnittstelle.
     */
    public static final String REST_PATH = "/rest";

    /**
     * Muster für Matrikelnummer.
     */
    public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";

    private final StudentReadService service;
    private final JwtService jwtService;
    private final UriHelper uriHelper;

    /**
     * Suche anhand der Student-ID als Pfad-Parameter.
     *
     * @param id ID des suchenden Studenten
     * @param version Versionsnummer aus dem Header if-None-Match
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @param jwt JWT für Security
     * @return Ein Response mit dem Statuscode 200 und dem gefundenen Studenten mit Atom-Links oder Statuscode 404.
     */
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit der Student-ID", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Student gefunden")
    @ApiResponse(responseCode = "404", description = "Student nicht gefunden")
    ResponseEntity<StudentModel> getById(
        @PathVariable final UUID id,
        @RequestHeader final Optional<String> version,
        final HttpServletRequest request,
        @AuthenticationPrincipal final Jwt jwt
    ) {
        final var username = jwtService.getUsername(jwt);
        log.debug("getById: id={}, version={}, username={}", id, version, username);

        if(username == null) {
            log.error("Trotz Spring Security wurde getById() ohne Benutzername im JWT aufgerufen");
            return status(UNAUTHORIZED).build();
        }
        final var rollen = jwtService.getRollen(jwt);
        log.trace("getById: rollen={}", rollen);

        final var student = service.findById(id, username, rollen, false);
        log.trace("getById: {}", student);

        final var currentVersion = "\"" + student.getVersion() + '"';
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }

        final var model = studentToModel(student, request);
        log.debug("getById: model={}", model);
        return ok().eTag(currentVersion).body(model);
    }

    private StudentModel studentToModel(final Student student, final HttpServletRequest request) {
        final var model = new StudentModel(student);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = baseUri + '/' + student.getId();

        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);
        return model;
    }
}
