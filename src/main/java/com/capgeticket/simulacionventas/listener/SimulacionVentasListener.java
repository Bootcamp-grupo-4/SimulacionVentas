package com.capgeticket.simulacionventas.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulacionVentasListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("--- Starting job to simulate ticket sales with id {}", jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("--- Job to simulate ticket sales with id {} has completed successfully", jobExecution.getId());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("--- Job to simulate ticket sales with id {} has failed", jobExecution.getId());
        }
    }
}