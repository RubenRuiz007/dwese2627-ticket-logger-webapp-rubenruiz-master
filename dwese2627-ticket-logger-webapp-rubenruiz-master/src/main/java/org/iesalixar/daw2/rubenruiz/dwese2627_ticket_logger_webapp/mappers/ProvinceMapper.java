package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.mappers;

import lombok.experimental.UtilityClass;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.ProvinceUpdateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;


import java.util.List;

/**
 * Mapper utilitario entre la entidad Province y sus DTOs.
 * Implementación simple sin frameworks de mapeo.
 */
@UtilityClass
public class ProvinceMapper {

    // ─────────────────────────────────────
    // Entity → DTO (listado/tabla básico)
    // ─────────────────────────────────────

    public static ProvinceDTO toDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDTO dto = new ProvinceDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        // Establecemos solo el nombre de la región, que es lo único que mostramos en los listados.
        dto.setRegionName(entity.getRegion() != null ? entity.getRegion().getName() : null);
        return dto;
    }

    public static List<ProvinceDTO> toDTOList(List<Province> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(ProvinceMapper::toDTO).toList();
    }

    // ─────────────────────────────────────
    // Entity → DTO (detalle con región)
    // ─────────────────────────────────────

    public static ProvinceDetailDTO toDetailDTO(Province entity) {
        if (entity == null) return null;
        ProvinceDetailDTO dto = new ProvinceDetailDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegion(RegionMapper.toDTO(entity.getRegion()));
        return dto;
    }

    /**
     * Convierte una Province a ProvinceUpdateDTO para precargar el formulario de edición.
     */
    public static ProvinceUpdateDTO toUpdateDTO(Province entity) {
        if (entity == null) return null;
        ProvinceUpdateDTO dto = new ProvinceUpdateDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setRegionId(entity.getRegion() != null ? entity.getRegion().getId() : null);
        return dto;
    }

    // ─────────────────────────────────────
    // DTO (create/update) → Entity
    // ─────────────────────────────────────

    /**
     * Crea una nueva entidad Province desde un ProvinceCreateDTO.
     * (id se deja null para autogenerarse)
     */
    public static Province toEntity(ProvinceCreateDTO dto) {
        if (dto == null) return null;
        Province e = new Province();
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        // Creamos una region vacía y le pasamos solo el ID,
        // ya que para la inserción de una provincia solo necesitamos el ID de la región
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }

    /**
     * Crea una entidad Province desde un ProvinceUpdateDTO.
     * Útil si trabajas con update por reemplazo.
     * Si prefieres evitar perder relaciones/estado, usa copyToExistingEntity.
     */
    public static Province toEntity(ProvinceUpdateDTO dto) {
        if (dto == null) return null;
        Province e = new Province();
        e.setId(dto.getId());
        e.setCode(dto.getCode());
        e.setName(dto.getName());
        // Creamos una region vacía y le pasamos solo el ID,
        // ya que para la actualización de la provincia solo necesitamos el ID de región
        Region region = new Region();
        region.setId(dto.getRegionId());
        e.setRegion(region);
        return e;
    }

    /**
     * Copia campos editables desde un ProvinceUpdateDTO a una entidad existente.
     * Recomendado si quieres mantener relaciones y el estado de persistencia del contexto JPA.
     */
    public static void copyToExistingEntity(ProvinceUpdateDTO dto, Province entity) {
        if (dto == null || entity == null) return;
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        Region region = new Region();
        region.setId(dto.getRegionId());
        entity.setRegion(region);
        // No tocar entity.setId(...)
    }
}
