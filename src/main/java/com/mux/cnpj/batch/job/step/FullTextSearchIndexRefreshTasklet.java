package com.mux.cnpj.batch.job.step;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class FullTextSearchIndexRefreshTasklet implements Tasklet {

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		Object cnpj = null;
		try {
			em.getTransaction().begin();
			cnpj = em.createNativeQuery("select cnpj from cnpj_full_text_search limit 1").getSingleResult();
			em.getTransaction().commit();
			log.info("found cnpj: {} on materialized view", cnpj);
		} catch (Exception e) {
			log.warn("error on quering materializaed view, might be valid case", e);
			em.getTransaction().commit();
		}
		if (cnpj == null) {
			try {
				String refreshQuery = "REFRESH MATERIALIZED VIEW cnpj_full_text_search;";
				log.info("will refresh the view with: '{}'", refreshQuery);
				em.getTransaction().begin();
				em.createNativeQuery(refreshQuery)
						.executeUpdate();
				em.getTransaction().commit();
			} catch (Exception e) {
				log.error("error refreshing materialized view for the first time", e);
				em.getTransaction().rollback();
			}

		} else {
			try {
				String refreshQuery = "REFRESH MATERIALIZED VIEW CONCURRENTLY cnpj_full_text_search;";
				log.info("will refresh the view with: '{}'", refreshQuery);
				em.getTransaction().begin();
				em.createNativeQuery(refreshQuery)
						.executeUpdate();
				em.getTransaction().commit();
			} catch (Exception e) {
				em.getTransaction().commit();
				log.error("error refreshing materialized view with data (concurrently)", e);
			}

		}

		return RepeatStatus.FINISHED;
	}

}
