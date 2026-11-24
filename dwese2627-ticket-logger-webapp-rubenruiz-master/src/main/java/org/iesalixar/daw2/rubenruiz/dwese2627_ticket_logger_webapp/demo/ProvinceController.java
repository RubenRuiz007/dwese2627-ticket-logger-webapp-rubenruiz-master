package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.demo;

import jakarta.validation.Valid;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.*;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services.ProvinceService;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.services.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador que maneja las operaciones CRUD para la entidad `Province`.
 * Utiliza `ProvinceService` para la lógica de negocio y `RegionService`
 * para cargar las regiones del desplegable del formulario.
 */
@Controller
@RequestMapping("/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);

    @Autowired
    private MessageSource messageSource;

    // Servicio para las operaciones del CRUD de Province
    @Autowired
    private ProvinceService provinceService;

    // Servicio de Region para el desplegable de regiones del formulario
    @Autowired
    private RegionService regionService;


    @GetMapping
    public String listProvinces(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            Locale locale) {

        logger.info("Listando provincias page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            // Ahora: delegamos en la capa de servicio
            Page<ProvinceDTO> listProvincesDTOs = provinceService.list(pageable);

            logger.info("Se han cargado {} provincias en la página {}.",
                    listProvincesDTOs.getNumberOfElements(), listProvincesDTOs.getNumber());

            model.addAttribute("page", listProvincesDTOs);

            // Para mantener el sort actual en los enlaces de la vista (sort=campo,asc|desc)
            String sortParam = "name,asc";
            if (listProvincesDTOs.getSort().isSorted()) {
                Sort.Order order = listProvincesDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las provincias: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.list.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-list";
    }


    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nueva provincia.");
        try {
            model.addAttribute("province", new ProvinceCreateDTO());
            model.addAttribute("listRegions", regionService.listAll());
        } catch (Exception e) {
            logger.error("Error al cargar las regiones para el formulario de provincia: {}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "views/province/province-form";
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        logger.info("Mostrando formulario de edición para la provincia con ID {}", id);

        try {
            // El service encapsula la búsqueda y el mapeo a DTO
            ProvinceUpdateDTO provinceDTO = provinceService.getForEdit(id);

            model.addAttribute("province", provinceDTO);
            model.addAttribute("listRegions", regionService.listAll());
            return "views/province/province-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la provincia con ID {}", id);
            String msg = messageSource.getMessage("msg.province-controller.edit.notfound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error al obtener la provincia con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.province-controller.edit.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }


    @PostMapping("/insert")
    public String insertProvince(@Valid @ModelAttribute("province") ProvinceCreateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Insertando nueva provincia con código {}", provinceDTO.getCode());

        try {
            // 1) Errores de validación del formulario (Bean Validation)
            if (result.hasErrors()) {
                // Volvemos a cargar las regiones para el select cuando hay errores
                model.addAttribute("listRegions", regionService.listAll());
                return "views/province/province-form";
            }
            // El service aplica la regla del código único y persiste
            provinceService.create(provinceDTO);

            logger.info("Provincia {} insertada con éxito.", provinceDTO.getCode());
            return "redirect:/provinces";

        } catch (DuplicateResourceException ex) {
            logger.warn("El código de la provincia {} ya existe.", provinceDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";

        } catch (Exception e) {
            logger.error("Error al insertar la provincia {}: {}", provinceDTO.getCode(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/new";
        }
    }


    @PostMapping("/update")
    public String updateProvince(@Valid @ModelAttribute("province") ProvinceUpdateDTO provinceDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 Locale locale) {

        logger.info("Actualizando provincia con ID {}", provinceDTO.getId());
        try {
            // 1) Errores de validación del formulario (Bean Validation)
            if (result.hasErrors()) {
                model.addAttribute("listRegions", regionService.listAll());
                return "views/province/province-form";
            }
            // El service aplica reglas (código único), comprueba existencia y actualiza
            provinceService.update(provinceDTO);

            logger.info("Provincia con ID {} actualizada con éxito.", provinceDTO.getId());
            return "redirect:/provinces";
        } catch (DuplicateResourceException ex) {
            logger.warn("El código de la provincia {} ya existe para otra provincia.", provinceDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.province-controller.update.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la provincia con ID {}", provinceDTO.getId());
            String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/provinces";
        } catch (Exception e) {
            logger.error("Error al actualizar la provincia con ID {}: {}", provinceDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces/edit?id=" + provinceDTO.getId();
        }
    }


    @PostMapping("/delete")
    public String deleteProvince(@RequestParam("id") Long id,
                                 RedirectAttributes redirectAttributes,
                                 Locale locale) {

        logger.info("Eliminando provincia con ID {}", id);

        try {
            // El service comprueba existencia y elimina (o lanza ResourceNotFoundException)
            provinceService.delete(id);

            logger.info("Provincia con ID {} eliminada con éxito.", id);
            return "redirect:/provinces";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la provincia con ID {}", id);
            String notFound = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error al eliminar la provincia con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.province-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/provinces";
        }
    }


    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        logger.info("Mostrando detalle de la provincia con ID {}", id);

        try {
            // El service se encarga de cargar la provincia (con su región) y mapear a DTO
            ProvinceDetailDTO provinceDTO = provinceService.getDetail(id);

            model.addAttribute("province", provinceDTO);
            return "views/province/province-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.province-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la provincia {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.province-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/provinces";
        }
    }
}
