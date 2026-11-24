package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.ProvinceDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceUpdateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.mappers.ProvinceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa de servicio de Province.
 * Usa los DAOs (Hibernate / EntityManager) para el acceso a datos y
 * devuelve DTOs al controlador.
 */
@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceDAO provinceDAO;

    // Necesario para cargar la región real al crear/editar una provincia
    @Autowired
    private RegionDAO regionDAO;

    @Override
    @Transactional(readOnly = true)
    public Page<ProvinceDTO> list(Pageable pageable) {
        // Sacamos el campo y la dirección de ordenación del Pageable
        String sortField = "name";
        String sortDir = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            sortDir = order.getDirection().name().toLowerCase();
        }

        List<Province> provinces = provinceDAO.listProvincesPage(
                pageable.getPageNumber(), pageable.getPageSize(), sortField, sortDir);
        long total = provinceDAO.countProvinces();

        // Construimos el Page que espera la vista
        return new PageImpl<>(ProvinceMapper.toDTOList(provinces), pageable, total);
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinceUpdateDTO getForEdit(Long id) {
        Province province = provinceDAO.getProvinceById(id);
        if (province == null) {
            throw new ResourceNotFoundException("province", "id", id);
        }
        return ProvinceMapper.toUpdateDTO(province);
    }

    @Override
    public void create(ProvinceCreateDTO dto) {
        if (provinceDAO.existsProvinceByCode(dto.getCode())) {
            throw new DuplicateResourceException("province", "code", dto.getCode());
        }

        Province province = ProvinceMapper.toEntity(dto);
        province.setRegion(findRegion(dto.getRegionId()));
        provinceDAO.insertProvince(province);
    }

    @Override
    public void update(ProvinceUpdateDTO dto) {
        if (provinceDAO.existsProvinceByCodeAndNotId(dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException("province", "code", dto.getCode());
        }

        Province province = provinceDAO.getProvinceById(dto.getId());
        if (province == null) {
            throw new ResourceNotFoundException("province", "id", dto.getId());
        }

        ProvinceMapper.copyToExistingEntity(dto, province);
        // Sustituimos la región "vacía" del mapper por la región real de BD
        province.setRegion(findRegion(dto.getRegionId()));
        provinceDAO.updateProvince(province);
    }

    @Override
    public void delete(Long id) {
        if (provinceDAO.getProvinceById(id) == null) {
            throw new ResourceNotFoundException("province", "id", id);
        }
        provinceDAO.deleteProvince(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinceDetailDTO getDetail(Long id) {
        Province province = provinceDAO.getProvinceById(id);
        if (province == null) {
            throw new ResourceNotFoundException("province", "id", id);
        }
        // Dentro de la transacción, así la región (LAZY) se puede cargar
        return ProvinceMapper.toDetailDTO(province);
    }

    /**
     * Busca la región por id y lanza excepción si no existe.
     */
    private Region findRegion(Long regionId) {
        Region region = (regionId != null) ? regionDAO.getRegionById(regionId) : null;
        if (region == null) {
            throw new ResourceNotFoundException("region", "id", regionId);
        }
        return region;
    }
}
