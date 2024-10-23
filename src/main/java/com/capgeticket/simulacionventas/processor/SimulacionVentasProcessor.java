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

    /**
     * Procesa cada evento para generar un número aleatorio de ventas de entradas.
     * Por cada evento, se genera entre 0 y 10 entradas, y se asignan valores aleatorios a ciertos atributos.
     *
     * @param evento El evento a procesar, a partir del cual se generan las ventas de entradas.
     * @return Un objeto VentaEntradaListDto que contiene la lista de entradas generadas.
     * @throws Exception Si ocurre algún error durante el procesamiento.
     */
    @Override
    public VentaEntradaListDto process(EventoEntity evento) throws Exception {
        int numeroVentas = random.nextInt(11);  // Genera un número aleatorio de ventas entre 0 y 10
        List<VentaEntradaDto> ventas = new ArrayList<>();

        // Crear entradas basadas en el número generado
        for (int i = 0; i < numeroVentas; i++) {
            VentaEntradaDto venta = VentaEntradaDto.builder()
                    .idEvento(evento.getId())  // ID del evento asociado
                    .nombreTitular("Titular " + random.nextInt(1000))  // Nombre aleatorio del titular
                    .numeroTarjeta("**** **** **** " + random.nextInt(9999))  // Número de tarjeta ficticio
                    .mesCaducidad(random.nextInt(12) + 1)  // Mes de caducidad aleatorio
                    .yearCaducidad(2024 + random.nextInt(5))  // Año de caducidad aleatorio (2024-2028)
                    .concepto("Compra Evento " + evento.getNombre())  // Concepto relacionado con el evento
                    .cantidad(evento.getPrecio())  // Precio del evento como cantidad
                    .fechaCompra(LocalDate.now())  // Fecha de compra actual
                    .build();

            ventas.add(venta);  // Añadir la venta a la lista
        }

        // Retornar la lista de ventas en un objeto VentaEntradaListDto
        return VentaEntradaListDto.builder()
                .ventas(ventas)
                .build();
    }
}
