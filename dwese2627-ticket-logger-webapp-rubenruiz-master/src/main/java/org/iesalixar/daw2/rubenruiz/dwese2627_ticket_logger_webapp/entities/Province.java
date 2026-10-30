package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity // Marca esta clase como una entidad JPA.
@Table(name = "provinces") // Define el nombre de la tabla asociada a esta entidad.
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código único de la provincia (VARCHAR(10) NOT NULL UNIQUE).
    @NotEmpty(message = "{msg.province.code.notEmpty}")
    @Size(max = 10, message = "{msg.province.code.size}")
    @Column(name = "code", nullable = false, length = 2) // Define la columna correspondiente en la tabla.
    private String code;

    // Nombre de la provincia (VARCHAR(100) NOT NULL).
    @NotEmpty(message = "{msg.province.name.notEmpty}")
    @Size(max = 100, message = "{msg.province.name.size}")
    @Column(name = "name", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String name;

    // Relación con la entidad `Region`, representando la comunidad autónoma a la que pertenece la provincia.
    @NotNull(message = "{msg.province.region.notNull}")
    @ManyToOne(fetch = FetchType.LAZY) // Relación de muchas provincias a una región.
    @JoinColumn(name = "region_id", nullable = false) // Clave foránea en la tabla provinces que referencia a la tabla regions.
    private Region region;

}