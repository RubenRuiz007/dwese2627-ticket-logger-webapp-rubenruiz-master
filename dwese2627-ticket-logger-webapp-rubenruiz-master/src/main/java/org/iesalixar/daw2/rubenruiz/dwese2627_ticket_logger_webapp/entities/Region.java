package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "regions") // Especifica el nombre de la tabla asociada a esta entidad.
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    @Column(name = "code", nullable = false, length = 2) // Define la columna correspondiente en la tabla.
    private String code;

    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.notEmpty}")
    @Column(name = "name", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String name;

    /**
     * Lista de provincias pertenecientes a la región.
     * - LAZY: no se cargan hasta que se accede a 'provinces'.
     * - mappedBy: el dueño de la relación es Province.region.
     * - Con cascade ALL: Así se borrarían las provincias asociadas a la región si se elimina la región.
     */
    @OneToMany(
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = false
    )
    @ToString.Exclude                 // Evita bucles en toString()
    @EqualsAndHashCode.Exclude        // Evita ciclos en equals/hashCode
    private List<Province> provinces = new ArrayList<>();
}