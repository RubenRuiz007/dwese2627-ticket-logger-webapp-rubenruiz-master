package org.iesalixar.daw2.sergioruiz.dwese2627_ticket_logger_webapp.dtos;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceDetailDTO {
    private Long id;
    private String code;
    private String name;
    private RegionDTO region;
}
