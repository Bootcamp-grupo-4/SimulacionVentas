package com.capgeticket.simulacionventas.processor;

import com.capgeticket.simulacionventas.dto.VentaEntradaDto;
import com.capgeticket.simulacionventas.dto.VentaEntradaListDto;
import com.capgeticket.simulacionventas.model.EventoEntity;
import org.springframework.batch.item.ItemProcessor;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulacionVentasProcessor implements ItemProcessor<EventoEntity, VentaEntradaListDto> {

    private final Random random = new Random();

    @Override
    public VentaEntradaListDto process(EventoEntity evento) throws Exception {
        int numeroVentas = random.nextInt(11);
        List<VentaEntradaDto> ventas = new ArrayList<>();

        for (int i = 0; i < numeroVentas; i++) {
            VentaEntradaDto venta = VentaEntradaDto.builder()
                    .idEvento(evento.getId())
                    .nombreTitular("Titular " + random.nextInt(1000))
                    .numeroTarjeta("**** **** **** " + random.nextInt(9999))
                    .mesCaducidad(random.nextInt(12) + 1)
                    .yearCaducidad(2024 + random.nextInt(5))
                    .concepto("Compra Evento " + evento.getNombre())
                    .cantidad(evento.getPrecio())
                    .fechaCompra(LocalDate.now())
                    .build();

            ventas.add(venta);
        }

        return VentaEntradaListDto.builder()
                .ventas(ventas)
                .build();
    }
}

