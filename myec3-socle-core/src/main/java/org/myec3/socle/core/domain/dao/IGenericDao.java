package org.myec3.socle.core.domain.dao;

import java.util.List;

import org.myec3.socle.core.domain.model.PE;

/**
 * Generic DAO interface for Project Entities. This class defines generic DAO
 * method.
 * 
 * @param <T> {@link PE}.
 */
public interface IGenericDao<T extends PE> {

	T findOne(Long id);

	List<T> findAll();

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	void deleteById(Long entityId);
}
