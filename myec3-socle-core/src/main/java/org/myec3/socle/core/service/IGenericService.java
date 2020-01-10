package org.myec3.socle.core.service;

import java.util.List;

import org.myec3.socle.core.domain.model.PE;

/**
 * Generic Service interface for Project Entities. This class defines generic
 * Service method.
 * 
 * @param <T> {@link PE}.
 */
public interface IGenericService<T extends PE> {

	T findOne(Long id);

	List<T> findAll();

	void create(T entity);

	T update(T entity);

	void delete(T entity);

	void deleteById(Long entityId);

}
