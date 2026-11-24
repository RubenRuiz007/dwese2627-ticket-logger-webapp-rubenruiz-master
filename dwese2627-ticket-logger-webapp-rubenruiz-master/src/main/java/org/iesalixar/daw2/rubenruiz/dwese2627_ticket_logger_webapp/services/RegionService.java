package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RegionService {

    Page<RegionDTO> list(Pageable pageable);

    List<RegionDTO> listAll();

    RegionUpdateDTO getForEdit(Long id);

    void create(RegionCreateDTO dto);

    void update(RegionUpdateDTO dto);

    void delete(Long id);

    RegionDetailDTO getDetail(Long id);
}