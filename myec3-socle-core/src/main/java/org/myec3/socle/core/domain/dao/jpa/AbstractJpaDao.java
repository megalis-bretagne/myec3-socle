package org.myec3.socle.core.domain.dao.jpa;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.myec3.socle.core.domain.model.PE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DAO class for Project Entities. This class implements generic DAO
 * method.
 * 
 * @param <T> {@link PE}.
 */
public abstract class AbstractJpaDao<T extends PE> {

	private Class<T> clazz;

	@PersistenceContext
	private EntityManager entityManager;

	@PostConstruct
	public void init() {
		clazz = getType();
	}

	public abstract Class<T> getType();

	/**
	 * logger for this class
	 */
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	/**
	 * This method allows to get the logger dynamically on runtime in order to
	 * associate it to the concrete service implementation
	 * 
	 * @return the current concrete logger
	 */
	public Logger getLog() {
		return LOG;
	}

	public EntityManager getEm() {
		return entityManager;
	}

	public void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public T findOne(Long id) {
		return entityManager.find(clazz, id);
	}

	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName()).getResultList();
	}

	public void create(T entity) {
		entityManager.persist(entity);
	}

	public T update(T entity) {
		return entityManager.merge(entity);
	}

	public void delete(T entity) {
		entityManager.remove(entity);
	}

	public void deleteById(Long entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}

	public Class<T> getDomainClass() {
		return clazz;
	}
}
