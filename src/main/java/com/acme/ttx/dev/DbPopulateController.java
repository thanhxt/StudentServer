package com.acme.ttx.dev;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static com.acme.ttx.dev.DevConfig.DEV;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Slf4j
@Profile(DEV)
public class DbPopulateController {
    private final Flyway flyway;

    /**
     * Die (Test-) DB wird bei einem POST-Request neu geladen.
     *
     * @return Response mit Statuscode 200 und Body "ok", falls keine Exception aufgetreten ist.
     */
    @PostMapping(value = "db_populate", produces = TEXT_PLAIN_VALUE)
    public String dbPopulate() {
        log.warn("Die DB wird neu geladen");
        flyway.clean();
        flyway.migrate();
        log.warn("Die DB wurde neu geladen");
        return "ok";
    }
}
