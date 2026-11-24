package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProvinceService {

    Page<ProvinceDTO> list(Pageable pageable);

    ProvinceUpdateDTO getForEdit(Long id);

    void create(ProvinceCreateDTO dto);

    void update(ProvinceUpdateDTO dto);

    void delete(Long id);

    ProvinceDetailDTO getDetail(Long id);
}
