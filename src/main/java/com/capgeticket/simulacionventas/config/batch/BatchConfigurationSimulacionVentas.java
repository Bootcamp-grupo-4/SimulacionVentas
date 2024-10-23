package com.capgeticket.simulacionventas.config.batch;

import com.capgeticket.simulacionventas.dto.VentaEntradaDto;
import com.capgeticket.simulacionventas.dto.VentaEntradaListDto;
import com.capgeticket.simulacionventas.listener.SimulacionVentasListener;
import com.capgeticket.simulacionventas.model.EventoEntity;
import com.capgeticket.simulacionventas.processor.SimulacionVentasProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfigurationSimulacionVentas {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Crea un lector de eventos desde la base de datos utilizando JDBC.
     * Lee todos los eventos sin incluir datos de ventas.
     *
     * @param dataSource El origen de datos a usar para ejecutar la consulta.
     * @return Un lector de eventos que devuelve objetos EventoEntity.
     */
    @Bean
    public ItemReader<EventoEntity> reader(DataSource dataSource) {
        log.info("Creando lector de eventos...");
        return new JdbcCursorItemReaderBuilder<EventoEntity>()
                .name("eventoItemReader")
                .dataSource(dataSource)
                .sql("SELECT id, nombre, descripcion, fechaEvento, precioMinimo, precioMaximo, localidad, " +
                        "nombreDelRecinto, genero, mostrar, precio FROM evento")
                .rowMapper(new BeanPropertyRowMapper<>(EventoEntity.class))
                .build();
    }

    /**
     * Crea el procesador para generar ventas simuladas.
     *
     * @return SimulacionVentasProcessor.
     */
    @Bean
    public SimulacionVentasProcessor processor() {
        log.info("Creando procesador de simulación de ventas...");
        return new SimulacionVentasProcessor();
    }

    /**
     * Crea el escritor para insertar las ventas generadas en la tabla VentaEntrada.
     *
     * @param dataSource El origen de datos a usar para la inserción.
     * @return Un escritor de ventas (VentaEntradaDto).
     */
    @Bean
    public ItemWriter<VentaEntradaDto> ventaEntradaWriter(DataSource dataSource) {
        log.info("Creando escritor de ventas...");
        return new JdbcBatchItemWriterBuilder<VentaEntradaDto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO VentaEntrada (id_evento, nombreTitular, numeroTarjeta, mesCaducidad, yearCaducidad, concepto, cantidad, fechaCompra) " +
                        "VALUES (:idEvento, :nombreTitular, :numeroTarjeta, :mesCaducidad, :yearCaducidad, :concepto, :cantidad, :fechaCompra)")
                .dataSource(dataSource)
                .build();
    }

    /**
     * Crea y configura el Job de simulación de ventas.
     *
     * @param listener El listener que maneja eventos antes y después del Job.
     * @param stepSimulacionVentas El paso del Job que procesa los eventos y genera ventas.
     * @return Un Job que simula la creación de ventas.
     */
    @Bean
    public Job crearEntradaJob(SimulacionVentasListener listener, Step stepSimulacionVentas) {
        log.info("Creando job de simulación de ventas...");
        return jobBuilderFactory
                .get("crearEntradaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepSimulacionVentas)
                .end()
                .build();
    }

    /**
     * Crea el paso del Job que incluye lectura de eventos, procesamiento para generar ventas y escritura en la base de datos.
     *
     * @param reader El lector de eventos.
     * @param processor El procesador que genera ventas para cada evento.
     * @param writer El escritor que inserta las ventas en la base de datos.
     * @return El paso configurado del Job.
     */
    @Bean
    public Step step1(ItemReader<EventoEntity> reader,
                      ItemProcessor<EventoEntity, VentaEntradaListDto> processor,
                      ItemWriter<VentaEntradaDto> writer) {
        log.info("Creando paso del job para simulación de ventas...");
        return stepBuilderFactory
                .get("step1")
                .<EventoEntity, VentaEntradaListDto>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(items -> {
                    log.info("Escribiendo ventas generadas para {} eventos...", items.size());
                    for (VentaEntradaListDto ventaEntradaList : items) {
                        writer.write(ventaEntradaList.getVentas());
                    }
                })
                .build();
    }

}
