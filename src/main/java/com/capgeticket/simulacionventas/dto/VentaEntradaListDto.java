package com.capgeticket.simulacionventas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaEntradaListDto {
    private List<VentaEntradaDto> ventas;
}

