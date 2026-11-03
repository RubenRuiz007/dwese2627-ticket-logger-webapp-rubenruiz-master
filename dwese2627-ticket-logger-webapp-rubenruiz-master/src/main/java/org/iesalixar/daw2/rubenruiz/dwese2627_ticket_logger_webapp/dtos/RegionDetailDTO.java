package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de detalle de Región. Incluye la lista de provincias asociadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDetailDTO {
    private Long id;
    private String code;
    private String name;
    private List<ProvinceDTO> provinces;
}