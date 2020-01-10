package org.myec3.socle.core.service.impl;

import java.util.List;

import org.myec3.socle.core.domain.dao.IGenericDao;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.service.IGenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract class for Generic Service implementation
 * 
 * @param <T> {@link PE}.
 * @param <D> {@link IGenericDao}.
 */
public abstract class AbstractGenericServiceImpl<T extends PE, D extends IGenericDao<T>> implements IGenericService<T> {

	@Autowired
	protected D dao;

	public T findOne(Long id) {
		return dao.findOne(id);
	}

	public List<T> findAll() {
		return dao.findAll();
	}

	@Transactional(readOnly = false)
	public void create(T entity) {
		dao.create(entity);
	}

	@Transactional(readOnly = false)
	public T update(T entity) {
		return dao.update(entity);
	}

	@Transactional(readOnly = false)
	public void delete(T entity) {
		dao.delete(entity);
	}

	@Transactional(readOnly = false)
	public void deleteById(Long entityId) {
		dao.deleteById(entityId);
	}
}
