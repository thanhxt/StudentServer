package com.acme.ttx.repository;

import com.acme.ttx.entity.Adresse_;
import com.acme.ttx.entity.ModuleType;
import com.acme.ttx.entity.SemesterType;
import com.acme.ttx.entity.Student;
import com.acme.ttx.entity.Student_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
    public Optional<Specification<Student>> build(final Map<String, ? extends List<String>> queryParams) {
        log.debug("build: queryParams {}", queryParams);

        if (queryParams.isEmpty()) {
            // keine suchkriterien
            return Optional.empty();
        }

        final var specs = queryParams
            .entrySet()
            .stream()
            .map(this::toSpecification)
            .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return Optional.empty();
        }

        return Optional.of(Specification.allOf(specs));
    }

    private Specification<Student> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();
        if ("interesse".contentEquals(key)) {
            return toSpecificationModule(values);
        }

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "nachname" -> nachname(value);
            case "name" -> name(value);
            case "email" -> email(value);
            case "plz" -> plz(value);
            case "ort" -> ort(value);
            case "semester" -> semester(value);
            default -> null;
        };
    }

    private Specification<Student> toSpecificationModule(final Collection<String> module) {
        if(module == null || module.isEmpty()) {
            return null;
        }

        final var specsImmutable = module.stream()
            .map(this::modul)
            .toList();
        if (specsImmutable.isEmpty() || specsImmutable.contains(null)) {
            return null;
        }

        final List<Specification<Student>> specs = new ArrayList<>(specsImmutable);
        final var first = specs.removeFirst();
        return specs.stream().reduce(first, Specification::and);
    }

    private Specification<Student> nachname(final String teil) {
        // root ist jakarta.persistence.criteria.Root<Student>
        // query ist jakarta.persistence.criteria.CriteriaQuery<Student>
        // builder ist jakarta.persistence.criteria.CriteriaBuilder
        // https://www.logicbig.com/tutorials/java-ee-tutorial/jpa/meta-model.html
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Student_.nachname)),
            builder.lower(builder.literal("%" + teil + '%'))
        );
    }

    private Specification<Student> name(final String teil) {
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Student_.name)),
            builder.lower(builder.literal("%" + teil + '%'))
        );
    }

    private Specification<Student> email(final String teil) {
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Student_.email)),
            builder.lower(builder.literal("%" + teil + '%'))
        );
    }

    private Specification<Student> plz(final String prefix) {
        return (root, _, builder) -> builder.like(root.get(Student_.adresse).get(Adresse_.plz), prefix + '%');
    }

    private Specification<Student> ort(final String prefix) {
        return (root, _, builder) -> builder.like(
            builder.lower(root.get(Student_.adresse).get(Adresse_.ort)),
            builder.lower(builder.literal(prefix + '%'))
        );
    }

    private Specification<Student> semester(final String semester) {
        return (root, _, builder) -> builder.equal(
            root.get(Student_.semester),
            SemesterType.of(semester)
        );
    }


    private Specification<Student> modul(final String modul) {
        final var modulEnum = ModuleType.of(modul);
        if (modulEnum == null) {
            return null;
        }
        return (root, _ , builder) -> builder.like(
            root.get(Student_.moduleStr),
            builder.literal("%" + modulEnum.name() + "%")
        );
    }
}
