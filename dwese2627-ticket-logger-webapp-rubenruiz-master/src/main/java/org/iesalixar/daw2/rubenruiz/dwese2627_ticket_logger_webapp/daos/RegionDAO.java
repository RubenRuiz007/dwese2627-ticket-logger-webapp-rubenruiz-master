package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;

import java.sql.SQLException;
import java.util.List;

public interface RegionDAO {

    List<Region> listAllRegions() throws SQLException;
    List<Region> listRegionsPage(int page, int size, String sortField, String sortDir);
    long countRegions();
    void insertRegion(Region region) throws SQLException;
    void updateRegion(Region region) throws SQLException;
    void deleteRegion(Long id) throws SQLException;
    Region getRegionById(Long id) throws SQLException;
    boolean existsRegionByCode(String code) throws SQLException;
    boolean existsRegionByCodeAndNotId(String code, Long id) throws SQLException;
}