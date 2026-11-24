package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;

import java.util.List;

public interface RegionDAO {

    List<Region> listAllRegions();
    void insertRegion(Region region);
    void updateRegion(Region region);
    void deleteRegion(Long id);
    Region getRegionById(Long id);
    Region getRegionByIdWithProvinces(Long id);
    boolean existsRegionByCode(String code);
    boolean existsRegionByCodeAndNotId(String code, Long id);
    List<Region> listRegionPage(int page, int size, String sortfield, String sortdir);
    long countRegions();
}
