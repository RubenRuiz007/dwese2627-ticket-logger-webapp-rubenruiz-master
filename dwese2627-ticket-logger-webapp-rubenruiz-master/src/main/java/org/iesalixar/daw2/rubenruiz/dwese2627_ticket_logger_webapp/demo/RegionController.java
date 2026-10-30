package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.demo;

import jakarta.validation.Valid;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos.RegionDAO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;
import org.slf4j.Logger;
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
@RequestMapping("/regions")
public class RegionController {
    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private RegionDAO regionDAO;

    @Autowired
    private MessageSource messageSource;


    @GetMapping
    public String ListRegions(Model model) throws SQLException {
        logger.info("Solicitando la lista de todas las regiones...");
        List<Region> listRegions = null;
        try {
            listRegions = regionDAO.listAllRegions();
            logger.info("Se ha cargado {} regiones",listRegions.size());
        } catch (SQLException e){
            logger.error("Error al listas las regiones: {}",e.getMessage());
            model.addAttribute("errorMesage","Error al listar las regiones");
        }
        model.addAttribute("listRegions",listRegions);
        return  "views/region/region-list";
    }
    @GetMapping("/new")
    public String showNewForm(Model model){
        logger.info("Mostrando formulario para nueva región");
        model.addAttribute("region",new Region());
        return "views/region/region-form";
    }

    @PostMapping("/insert")
    public String insertRegion(@Valid @ModelAttribute("region") Region region, BindingResult result, RedirectAttributes redirectAttributes, Locale locale) {
        logger.info("Insertando nueva región con código {}", region.getCode());
        try {
            if (result.hasErrors()) {
                return "views/region/region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (regionDAO.existsRegionByCode(region.getCode())) {
                logger.warn("El código de la región {} ya existe.", region.getCode());
                String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/regions/new";
            }
            regionDAO.insertRegion(region);
            logger.info("Región {} insertada con éxito.", region.getCode());
        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", region.getCode(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model){
        logger.info("Mostrando el formulario de edición con el id:{}",id);
        Region region = null;
        try{
            region = regionDAO.getRegionById(id);
            if (region == null) {
                logger.warn("Error, No se encontró la región con el od:{}",id);
            }
        } catch (SQLException e) {
            logger.error("Error al obtener la región con el id:{}",id);
            model.addAttribute("erroMessage","Error al obtener la región");
        }
        model.addAttribute("region",region);
        return "/views/region/region-form";
    }
    @PostMapping("/update")
    public String updateRegion(@Valid @ModelAttribute("region") Region region,  BindingResult result, RedirectAttributes redirectAttributes,  Locale locale) {
        logger.info("Actualizando región con ID {}", region.getId());
        try {
            if (result.hasErrors()) {
                return "views/region/region-form";  // Devuelve el formulario para mostrar los errores de validación
            }
            if (regionDAO.existsRegionByCodeAndNotId(region.getCode(),(long)region.getId())) {
                logger.warn("El código de la región {} ya existe para otra región.", region.getCode());
                String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                return "redirect:/regions/edit?id=" + region.getId();
            }
            regionDAO.updateRegion(region);
            logger.info("Región con ID {} actualizada con éxito.", region.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID {}: {}", region.getId(), e.getMessage());
            String errorMessage = messageSource.getMessage("msg.region-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        return "redirect:/regions"; // Redirigir a la lista de regiones
    }

    @PostMapping("/delete")
    public String deleteRegion(@ModelAttribute("id") Long id,RedirectAttributes redirectAttributes){
        logger.info("Entrando al método para eliminar la region con el id{}",id);
        try{
            regionDAO.deleteRegion(id);
            logger.info("Region con id {} eliminada con éxito",id);
        } catch (SQLException e) {
            logger.error("Error al eliminar la región con id:{}",id);
            redirectAttributes.addFlashAttribute("errorMessage","Error al eliminar la región");
        }
        return "redirect:/regions";
    }

    /**
     * Muestra el detalle de una región específica, incluyendo su lista de provincias asociadas.
     *
     * @param id                 Identificador único de la región que se desea consultar.
     * @param model              Modelo de Spring MVC utilizado para pasar datos a la vista.
     * @param redirectAttributes Objeto para enviar mensajes flash de error o información al redirigir.
     * @param locale             Configuración regional actual del usuario (para internacionalización de mensajes).
     * @return El nombre de la plantilla Thymeleaf que muestra el detalle de la región
     *         ({@code views/region/region-detail}), o una redirección a {@code /regions} en caso de error.
     */
    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle de la región con ID {}", id);
        try {
            // Cargar la región junto con sus provincias (fetch) para evitar LazyInitializationException
            Region region = regionDAO.getRegionById(id);
            if (region == null) {
                String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
                redirectAttributes.addFlashAttribute("errorMessage", msg);
                return "redirect:/regions";
            }
            model.addAttribute("region", region);
            return "views/region/region-detail";
        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }
}