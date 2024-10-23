package com.capgeticket.simulacionventas.config.batch;

import com.capgeticket.simulacionventas.dto.VentaEntradaDto;
import com.capgeticket.simulacionventas.dto.VentaEntradaListDto;
import com.capgeticket.simulacionventas.listener.SimulacionVentasListener;
import com.capgeticket.simulacionventas.model.EventoEntity;
import com.capgeticket.simulacionventas.processor.SimulacionVentasProcessor;
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
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfigurationSimulacionVentas {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<EventoEntity> reader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<EventoEntity>()
                .name("eventoItemReader")
                .dataSource(dataSource)
                .sql("SELECT id, nombre, descripcion, fechaEvento, precioMinimo, precioMaximo, localidad, " +
                        "nombreDelRecinto, genero, mostrar, precio FROM evento")
                .rowMapper(new BeanPropertyRowMapper<>(EventoEntity.class))
                .build();
    }
    @Bean
    public SimulacionVentasProcessor processor(){
        return new SimulacionVentasProcessor();
    }

    @Bean
    public ItemWriter<VentaEntradaDto> ventaEntradaWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<VentaEntradaDto>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO VentaEntrada (id_evento, nombreTitular, numeroTarjeta, mesCaducidad, yearCaducidad, concepto, cantidad, fechaCompra) " +
                        "VALUES (:idEvento, :nombreTitular, :numeroTarjeta, :mesCaducidad, :yearCaducidad, :concepto, :cantidad, :fechaCompra)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    public Job crearEntradaJob(SimulacionVentasListener listener, Step stepSimulacionVentas) {
        return jobBuilderFactory
                .get("crearEntradaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(stepSimulacionVentas)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<EventoEntity> reader,
                      ItemProcessor<EventoEntity, VentaEntradaListDto> processor,
                      ItemWriter<VentaEntradaDto> writer) {
        return stepBuilderFactory
                .get("step1")
                .<EventoEntity, VentaEntradaListDto>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(items -> {
                    for (VentaEntradaListDto ventaEntradaList : items) {
                        writer.write(ventaEntradaList.getVentas());
                    }
                })
                .build();
    }


}
