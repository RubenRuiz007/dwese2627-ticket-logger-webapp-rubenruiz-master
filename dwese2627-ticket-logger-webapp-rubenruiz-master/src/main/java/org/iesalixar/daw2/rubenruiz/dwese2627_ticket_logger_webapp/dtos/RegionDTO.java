package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico de lectura para Region (listar/detalle).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionDTO {
    private Long id;
    private String code;
    private String name;
}
