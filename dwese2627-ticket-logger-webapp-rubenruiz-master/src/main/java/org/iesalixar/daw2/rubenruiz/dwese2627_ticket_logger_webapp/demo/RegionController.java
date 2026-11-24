package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.demo;

import jakarta.validation.Valid;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionCreateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionDetailDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.dtos.RegionUpdateDTO;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.exceptions.ResourceNotFoundException;
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
 * Controlador que maneja las operaciones CRUD para la entidad `Region`.
 * Utiliza `RegionService` para la lógica de negocio.
 */
@Controller
@RequestMapping("/regions")
public class RegionController {


    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private MessageSource messageSource;

    // Servicio para las operaciones del CRUD de Region
    @Autowired
    private RegionService regionService;


    @GetMapping
    public String listRegions(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        logger.info("Listando regiones page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        try {
            // Antes: repository + mapper en el controller
            // Page<RegionDTO> listRegionsDTOs = regionRepository.findAll(pageable).map(RegionMapper::toDTO);

            // Ahora: delegamos en la capa de servicio (mismo resultado para la vista)
            Page<RegionDTO> listRegionsDTOs = regionService.list(pageable);

            logger.info("Se han cargado {} regiones en la página {}.",
                    listRegionsDTOs.getNumberOfElements(), listRegionsDTOs.getNumber());

            model.addAttribute("page", listRegionsDTOs);

            // Para mantener el sort actual en los enlaces de la vista (sort=campo,asc|desc)
            String sortParam = "name,asc";
            if (listRegionsDTOs.getSort().isSorted()) {
                Sort.Order order = listRegionsDTOs.getSort().iterator().next();
                sortParam = order.getProperty() + "," + order.getDirection().name().toLowerCase();
            }
            model.addAttribute("sortParam", sortParam);
        } catch (Exception e) {
            logger.error("Error al listar las regiones: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error al listar las regiones.");
        }
        return "views/region/region-list";
    }


    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva región");
        model.addAttribute("region", new RegionCreateDTO());
        return "views/region/region-form";
    }


    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        logger.info("Mostrando formulario de edición para la región con ID {}", id);

        try {
            // Antes: repositorio + mapper en el controller
            // Ahora: el service encapsula la búsqueda y el mapeo a DTO
            RegionUpdateDTO regionDTO = regionService.getForEdit(id);

            model.addAttribute("region", regionDTO);
            return "views/region/region-form";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la región con ID {}", id);
            String msg = messageSource.getMessage("msg.region.error.notfound", new Object[]{id}, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";

        } catch (Exception e) {
            logger.error("Error al obtener la región con ID {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.region.error.load", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }


    @PostMapping("/insert")
    public String insertRegion(@Valid @ModelAttribute("region") RegionCreateDTO regionDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        logger.info("Insertando nueva región con código {}", regionDTO.getCode());

        try {
            // 1) Errores de validación del formulario (Bean Validation)
            if (result.hasErrors()) {
                return "views/region/region-form";
            }
            // Antes: comprobación + save en el controller
            // Ahora: el service aplica la regla del código único y persiste
            regionService.create(regionDTO);

            logger.info("Región {} insertada con éxito.", regionDTO.getCode());
            return "redirect:/regions";

        } catch (DuplicateResourceException ex) {
            // Caso "semántico": código duplicado
            logger.warn("El código de la región {} ya existe.", regionDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/new";

        } catch (Exception e) {
            logger.error("Error al insertar la región {}: {}", regionDTO.getCode(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.region-controller.insert.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/new";
        }
    }


    @PostMapping("/update")
    public String updateRegion(@Valid @ModelAttribute("region") RegionUpdateDTO regionDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        logger.info("Actualizando región con ID {}", regionDTO.getId());
        try {
            // 1) Errores de validación del formulario (Bean Validation)
            if (result.hasErrors()) {
                return "views/region/region-form";
            }
            // Antes: comprobación de duplicado + findById + save en el controller
            // Ahora: el service aplica reglas (código único), comprueba existencia y actualiza
            regionService.update(regionDTO);

            logger.info("Región con ID {} actualizada con éxito.", regionDTO.getId());
            return "redirect:/regions";
        } catch (DuplicateResourceException ex) {
            logger.warn("El código de la región {} ya existe para otra región.", regionDTO.getCode());
            String errorMessage = messageSource.getMessage("msg.region-controller.update.codeExist", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/edit?id=" + regionDTO.getId();
        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la región con ID {}", regionDTO.getId());
            String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/regions";
        } catch (Exception e) {
            logger.error("Error al actualizar la región con ID {}: {}", regionDTO.getId(), e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.region-controller.update.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions/edit?id=" + regionDTO.getId();
        }
    }


    @PostMapping("/delete")
    public String deleteRegion(@RequestParam("id") Long id,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {

        logger.info("Eliminando región con ID {}", id);

        try {
            // Antes: comprobar existencia con repository y borrar en el controller
            // Ahora: el service comprueba existencia y elimina (o lanza ResourceNotFoundException)
            regionService.delete(id);

            logger.info("Región con ID {} eliminada con éxito.", id);
            return "redirect:/regions";

        } catch (ResourceNotFoundException ex) {
            logger.warn("No se encontró la región con ID {}", id);
            String notFound = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", notFound);
            return "redirect:/regions";

        } catch (Exception e) {
            logger.error("Error al eliminar la región con ID {}: {}", id, e.getMessage(), e);
            String errorMessage = messageSource.getMessage("msg.region-controller.delete.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/regions";
        }
    }


    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {

        logger.info("Mostrando detalle de la región con ID {}", id);

        try {
            // Antes: repository + mapper en el controller
            // Optional<Region> regionOpt = regionRepository.findByIdWithProvinces(id);
            // ... RegionDetailDTO regionDTO = RegionMapper.toDetailDTO(region);

            // Ahora: el service se encarga de cargar con fetch (provinces) y mapear a DTO
            RegionDetailDTO regionDTO = regionService.getDetail(id);

            model.addAttribute("region", regionDTO);
            return "views/region/region-detail";

        } catch (ResourceNotFoundException ex) {
            String msg = messageSource.getMessage("msg.region-controller.detail.notFound", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";

        } catch (Exception e) {
            logger.error("Error al obtener el detalle de la región {}: {}", id, e.getMessage(), e);
            String msg = messageSource.getMessage("msg.region-controller.detail.error", null, locale);
            redirectAttributes.addFlashAttribute("errorMessage", msg);
            return "redirect:/regions";
        }
    }
}
