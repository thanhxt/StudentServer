package com.acme.ttx.controller;

import com.acme.ttx.entity.Student;
import com.acme.ttx.service.StudentReadServiceOld;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.acme.ttx.controller.StudentGetController.REST_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    /**
     * Pfad um Matrikelnummer zu suchen.
     */
    private final StudentReadServiceOld service;
    private final UriHelper uriHelper;

    /**
     * Suche alle Studenten-Matrikelnummer als Pfadparameter.
     *
     * @return Alle Studenten
     */
    @GetMapping(value = "id", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Suche alle Studenten", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Studenten gefunden")
    @ApiResponse(responseCode = "404", description = "kein Studenten gefunden ")
    public Collection<Student> getAll() {
        return service.findAllStudents();
    }

    /**
     * Suche alle Studenten-Matrikelnummer als Queryparameter.
     *
     * @param suchkriterien Query-Parameter als Map.
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Gefundenen Studenten als Collection.
     */
    @GetMapping(produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit Suchkriterien", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Collection mit den Studenten")
    @ApiResponse(responseCode = "404", description = "Keine Studenten gefunden")
    public CollectionModel<StudentModel> get(
        @RequestParam @NonNull final MultiValueMap<String, String> suchkriterien,
        final HttpServletRequest request) {

        log.debug("get: suchkriterien={}", suchkriterien);

        final var baseUri = uriHelper.getBaseUri(request).toString();

        final var models = service.find(suchkriterien)
                .stream()
                .map(student -> {
                    final var model = new StudentModel(student);
                    model.add(Link.of(baseUri + '/' + student.getId()));
                    return model;
                })
                    .toList();

        log.debug("get: {}", models);
        return CollectionModel.of(models);
    }

    /**
     * Suche alle Studenten-Matrikelnummer als Pfadparameter.
     *
     * @param id Matrikelnummer des gesuchten Studenten
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Gefundener Student
     */
    @SuppressWarnings("java:S6856")
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche Studenten anhand der Id", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Student gefunden")
    @ApiResponse(responseCode = "404", description = "Student nicht gefunden")
    StudentModel getStudentByMatrikelnummer(@PathVariable final UUID id,
                                            final HttpServletRequest request) {
        log.debug("getStudentById: id={}, Thread={}", id, Thread.currentThread());

        //Geschäftslogik bzw. Anwendungskern
        final var student = service.findStudentById(id);

        //HATEOAS
        final var model = new StudentModel(student);
        // evtl. Forwarding von einem API-Gateway
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = baseUri + '/' + student.getId();
        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);

        log.debug("getById: {}", id);
        return model;
    }
}
