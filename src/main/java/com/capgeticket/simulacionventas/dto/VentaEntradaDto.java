package com.capgeticket.simulacionventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaEntradaDto {
    private Long id;
    private Long idEvento;
    private String nombreTitular;
    private String numeroTarjeta;
    private Integer mesCaducidad;
    private Integer yearCaducidad;
    private String concepto;
    private BigDecimal cantidad;
    private LocalDate fechaCompra;
}

