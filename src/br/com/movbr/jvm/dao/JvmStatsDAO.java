package br.com.movbr.jvm.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.movbr.jvm.model.BaseDAO;
import br.com.movbr.jvm.model.JvmStats;

@Repository
public class JvmStatsDAO extends BaseDAO<JvmStats> {
	public JvmStatsDAO() {
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(JvmStats jvmStats) {
		persist(jvmStats);
	}
}
