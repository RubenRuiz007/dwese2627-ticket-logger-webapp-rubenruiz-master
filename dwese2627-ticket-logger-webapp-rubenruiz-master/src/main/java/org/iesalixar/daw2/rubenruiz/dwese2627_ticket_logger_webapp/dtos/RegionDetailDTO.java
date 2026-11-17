package org.iesalixar.daw2.sergioruiz.dwese2627_ticket_logger_webapp.dtos;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegionDetailDTO {
    private long id;
    private String code;
    private String name;
    private List<ProvinceDTO> provinces;
}
