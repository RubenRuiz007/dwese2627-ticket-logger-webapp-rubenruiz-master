package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.sergioruiz.dwese2627_ticket_logger_webapp.mappers.RegionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Capa de servicio de Region.
 * Usa el DAO (Hibernate / EntityManager) para el acceso a datos y
 * devuelve DTOs al controlador.
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {

    @Autowired
    private RegionDAO regionDAO;

    @Override
    @Transactional(readOnly = true)
    public Page<RegionDTO> list(Pageable pageable) {
        // Sacamos el campo y la dirección de ordenación del Pageable
        String sortField = "name";
        String sortDir = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            sortDir = order.getDirection().name().toLowerCase();
        }

        List<Region> regions = regionDAO.listRegionPage(
                pageable.getPageNumber(), pageable.getPageSize(), sortField, sortDir);
        long total = regionDAO.countRegions();

        // Construimos el Page que espera la vista
        return new PageImpl<>(RegionMapper.toDTOList(regions), pageable, total);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionDTO> listAll() {
        return RegionMapper.toDTOList(regionDAO.listAllRegions());
    }

    @Override
    @Transactional(readOnly = true)
    public RegionUpdateDTO getForEdit(Long id) {
        Region region = regionDAO.getRegionById(id);
        if (region == null) {
            throw new ResourceNotFoundException("region", "id", id);
        }
        return RegionMapper.toUpdateDTO(region);
    }

    @Override
    public void create(RegionCreateDTO dto) {
        if (regionDAO.existsRegionByCode(dto.getCode())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }
        Region region = RegionMapper.toEntity(dto);
        regionDAO.insertRegion(region);
    }

    @Override
    public void update(RegionUpdateDTO dto) {
        if (regionDAO.existsRegionByCodeAndNotId(dto.getCode(), dto.getId())) {
            throw new DuplicateResourceException("region", "code", dto.getCode());
        }

        Region region = regionDAO.getRegionById(dto.getId());
        if (region == null) {
            throw new ResourceNotFoundException("region", "id", dto.getId());
        }

        RegionMapper.copyToExistingEntity(dto, region);
        regionDAO.updateRegion(region);
    }

    @Override
    public void delete(Long id) {
        if (regionDAO.getRegionById(id) == null) {
            throw new ResourceNotFoundException("region", "id", id);
        }
        regionDAO.deleteRegion(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionDetailDTO getDetail(Long id) {
        // Trae la región con sus provincias cargadas (JOIN FETCH)
        Region region = regionDAO.getRegionByIdWithProvinces(id);
        if (region == null) {
            throw new ResourceNotFoundException("region", "id", id);
        }
        return RegionMapper.toDetailDTO(region);
    }
}
