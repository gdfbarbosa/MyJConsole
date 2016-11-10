package br.com.movbr.jvm.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class BaseDAO<E extends BaseEntidade<?>> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(BaseDAO.class);

	@PersistenceContext(unitName = "jvmstats") 
	private EntityManager entityManager;
	
	public BaseDAO() {
	}
	
	public enum TipoQuery {
		NAMED_QUERY, NATIVE_QUERY, JPQL
	}
	
	protected void update(final E objeto) {
		entityManager.merge(objeto);
	}
	
	protected void save(final E objeto) {
		entityManager.persist(objeto);
	}
	
	protected void delete(final E objeto) {
		BaseEntidade<?> e = entityManager.find(objeto.getClass(), objeto.getId());
		entityManager.remove(e);
	}
	
	public void clear() {
		clear();
	}

	public Query criarNamedQuery(String nomeNamedQuery) {
		return entityManager.createNamedQuery(nomeNamedQuery);
	}

	public Query criarNamedQuery(Integer begin, Integer maxResults, String nomeNamedQuery) {
		Query query = criarNamedQuery(nomeNamedQuery);
		if ( begin != null && maxResults != null ) {
			query.setFirstResult(begin);
			query.setMaxResults(maxResults);
		}
		return query;
	}
	
	protected Integer getLastInsertId(EntityManager em) {
		Query query = em.createNativeQuery("SELECT LAST_INSERT_ID()");
		return ((BigInteger) query.getSingleResult()).intValue();
	}
	
	public Query createNativeQuery(final String sqlString) {
		return entityManager.createNativeQuery(sqlString);
	}
	
	public Query createNativeQuery( String nomeNamedQuery, Integer begin, Integer maxResults ) {
		Query query = entityManager.createNativeQuery(nomeNamedQuery);
		if ( begin != null && maxResults != null ) {
			query.setFirstResult(begin);
			query.setMaxResults(maxResults);
		}
		return query;
	}

	public Query createNativeQuery(final String sqlString, final Class<?> tipo) {
		return entityManager.createNativeQuery(sqlString, tipo);
	}

	public Query createNativeQuery(final String sqlString, String resultSetMapping ) {
		return entityManager.createNativeQuery(sqlString, resultSetMapping);
	}

	public Query criarQuery( String query ){
		return entityManager.createQuery(query);
	}  
	
	public Query criarQuery(Integer begin, Integer maxResults, String nomeNamedQuery) {
		Query query = criarQuery(nomeNamedQuery);
		if ( begin != null && maxResults != null ) {
			query.setFirstResult(begin);
			query.setMaxResults(maxResults);
		}
		return query;
	}

	public <ID extends Serializable, X extends BaseEntidade<ID>> Map<ID, X> obterMapEntidade(List<X> list) {
		Map<ID, X> mapEntidade = new HashMap<ID, X>();
		for (X entidade : list) {
			mapEntidade.put(entidade.getId(), entidade);
		}
		return mapEntidade;
	}

	@SuppressWarnings("unchecked")
	public List<E> obterTodos(Class<E> clazz) {
		Query query = entityManager.createQuery("select item from " + clazz.getName() + " item");
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	protected List<E> find(String queryStr) {
		Query query = entityManager.createQuery(queryStr);
		return (List<E>) query.getResultList();
	}
	
	protected E find(Class<E> clazz, Integer id) {
		return entityManager.find(clazz, id);
	}
	
	@SuppressWarnings("unchecked")
	protected List<Object[]> findObjectListByNamedParams(String queryStr, Map<String,Object> parameters) {
		Query query = entityManager.createQuery(queryStr);
		for(String key : parameters.keySet()) {
			query.setParameter(key, parameters.get(key));
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	protected <O extends BaseEntidade<?>> List<O> findEntityListByNamedParams(String queryStr, Map<String,Object> parameters) {
		Query query = entityManager.createQuery(queryStr);
		for(String key : parameters.keySet()) {
			query.setParameter(key, parameters.get(key));
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	protected List<E> findByNamedParams(String queryStr, Map<String,Object> parameters) {
		Query query = entityManager.createQuery(queryStr);
		for(String key : parameters.keySet()) {
			query.setParameter(key, parameters.get(key));
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	protected <O> List<O> findByNamedParams(String queryStr, Map<String,Object> parameters, Class<O> clazz) {
		Query query = entityManager.createQuery(queryStr);
		for(String key : parameters.keySet()) {
			query.setParameter(key, parameters.get(key));
		}
		return query.getResultList();
	}
	
	public Query criarNamedQuery(String nomeNamedQuery, Map<String, Object> parametros) {
		Query query = getEntityManager().createNamedQuery(nomeNamedQuery);
		for(String paramName : parametros.keySet()) {
			query.setParameter(paramName, parametros.get(paramName));
		}
		return query;
	}
	
	public Query criarQuery(String queryStr, Map<String, Object> parametros) {
		Query query = getEntityManager().createNativeQuery(queryStr);
		for(String paramName : parametros.keySet()) {
			query.setParameter(paramName, parametros.get(paramName));
		}
		return query;
	}
	
	protected E merge(E entity) {
		return entityManager.merge(entity);
	}
	
	protected void persist(E entity) {
		entityManager.persist(entity);
	}
	
	protected EntityManager getEntityManager() {
		return this.entityManager;
	}
	
	protected void remove(E entity) {
		entityManager.remove(entity);
	}
}