package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.demo;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.ProvinceDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/provinces")
public class ProvinceController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Province.class);
    @Autowired
    private RegionDAO regionDAO;

    @Autowired
    private ProvinceDAO provinceDAO;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listProvinces(Model model, Locale locale) {
        logger.info("Solicitando la lista de todas las provincias...");
        try {
            List<Province> listProvinces = provinceDAO.listAllProvinces();
            logger.info("Se han cargado {} provincias.", listProvinces.size());
            model.addAttribute("listProvinces", listProvinces);
        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-list";
    }

    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nueva provincia.");
        try {
            List<Region> listRegions = regionDAO.listAllRegions();
            model.addAttribute("province", new Province());
            model.addAttribute("listRegions", listRegions);
        } catch (Exception e) {
            logger.error("Error al cargar las regiones para el formulario de provincia: {}", e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }

    @PostMapping("/insert")
    public String insertProvince(@Valid @ModelAttribute("province") Province province,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {
        logger.info("Insertando nueva provincia con código {}", province.getCode());
        try {
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                List<Region> listRegions = regionDAO.listAllRegions();
                model.addAttribute("listRegions", listRegions);
                return "views/province/province-form";
            }

            if (provinceDAO.existsProvinceByCode(province.getCode())) {
                logger.warn("El código de la provincia {} ya existe.", province.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/new";
            }

            provinceDAO.insertProvince(province);
            logger.info("Provincia {} insertada con éxito.", province.getCode());

        } catch (Exception e) {
            logger.error("Error al insertar la provincia {}: {}", province.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, Locale locale) {
        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);
        try {
            Province province = provinceDAO.getProvinceById(id);
            if (province == null) {
                logger.warn("No se encontró la provincia con ID {}", id);
                String errorMessage = messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
                model.addAttribute("errorMessage", errorMessage);
            } else {
                List<Region> listRegions = regionDAO.listAllRegions();
                model.addAttribute("province", province);
                model.addAttribute("listRegions", listRegions);
            }
        } catch (Exception e) {
            logger.error("Error al obtener la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }

    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") Province province,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {
        logger.info("Actualizando provincia con ID {}", province.getId());
        try {
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                List<Region> listRegions = regionDAO.listAllRegions();
                model.addAttribute("listRegions", listRegions);
                return "views/province/province-form";
            }

            if (provinceDAO.existsProvinceByCodeAndNotId(province.getCode(), province.getId())) {
                logger.warn("El código de la provincia {} ya existe para otra provincia.", province.getCode());
                String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/provinces/edit?id=" + province.getId();
            }

            provinceDAO.updateProvince(province);
            logger.info("Provincia con ID {} actualizada con éxito.", province.getId());

        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID {}: {}", province.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }
    /**
     * Elimina una provincia de la base de datos.
     *
     * @param id                 ID de la provincia a eliminar.
     * @param redirectAttributes Atributos para mensajes flash.
     * @param locale             Configuración regional actual.
     * @return Redirección al listado de provincias.
     */
    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {
        logger.info("Eliminando provincia con ID {}", id);
        try {
            provinceDAO.deleteProvince(id);
            logger.info("Provincia con ID {} eliminada con éxito.", id);
        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage());
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/provinces";
    }

}
