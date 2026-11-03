package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear regiones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionCreateDTO {

    // En este DTO este campo siempre vendrá null porque es una inserción
    private Long id;

    @NotBlank(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;

    @NotBlank(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.notEmpty}")
    private String name;
}
