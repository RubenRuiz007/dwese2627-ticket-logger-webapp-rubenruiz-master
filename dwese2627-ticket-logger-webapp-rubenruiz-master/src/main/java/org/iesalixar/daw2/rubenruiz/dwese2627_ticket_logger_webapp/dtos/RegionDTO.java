package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegionDTO {
    private Long id;
    private String code;
    private String name;
}
