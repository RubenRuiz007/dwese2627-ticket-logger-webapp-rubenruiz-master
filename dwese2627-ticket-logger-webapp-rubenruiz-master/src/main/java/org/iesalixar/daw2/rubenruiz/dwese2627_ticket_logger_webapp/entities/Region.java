package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Region {
    private Long id;
    @NotEmpty(message = "{msg.region.code.notEmpty}")
    @Size(max = 2, message = "{msg.region.code.size}")
    private String code;


    // Campo que almacena el nombre completo de la región, como "Andalucía" o "Cataluña".
    @NotEmpty(message = "{msg.region.name.notEmpty}")
    @Size(max = 100, message = "{msg.region.name.notEmpty}")
    private String name;

    public Region(String code, String name){
        this.code = code;
        this.name = name;
    }
}