package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos;

import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Province;

import java.util.List;

public interface ProvinceDAO {
    List<Province> listAllProvinces();

    void insertProvince(Province province);

    void updateProvince(Province province);

    void deleteProvince(Long id);

    Province getProvinceById(Long id);

    boolean existsProvinceByCode(String code);

    boolean existsProvinceByCodeAndNotId(String code, Long id);

    List<Province> listProvincesPage(int page, int size, String sortfield,String sortdir);

    long countProvinces();
}
