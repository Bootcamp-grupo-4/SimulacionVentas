package com.capgeticket.simulacionventas.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job actualizarPreciosJob;

    /**
     * Ejecuta el Job "actualizarPreciosJob" cada 10 minutos.
     * Se generan nuevos parámetros de trabajo con la marca de tiempo actual.
     */
    @Scheduled(fixedRate = 600000) // Ejecuta cada 10 minutos
    public void runActualizarPreciosJob() {
        try {
            // Crear JobParameters usando la marca de tiempo actual para asegurar que el job se ejecuta cada vez
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // Ejecutar el Job y registrar el estado de la ejecución
            JobExecution execution = jobLauncher.run(actualizarPreciosJob, params);
            System.out.println("Job Status: " + execution.getStatus());

        } catch (Exception e) {
            // Manejar excepciones y mostrar traza de error en consola
            e.printStackTrace();
        }
    }
}
