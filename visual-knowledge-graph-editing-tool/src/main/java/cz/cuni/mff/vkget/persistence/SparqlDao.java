package cz.cuni.mff.vkget.persistence;

import java.util.List;

import cz.cuni.mff.vkget.data.common.RdfEntity;
import cz.cuni.mff.vkget.data.common.Uri;

/**
 * DAO interface for @see RdfEntity objects
 * @author Ales Woska
 *
 * @param <T> Type of Dao object
 */
public interface SparqlDao<T extends RdfEntity> {
	
	/**
	 * Insert new entity.
	 * @param entity
	 */
	void insert(T entity);
	
	void update(T entity);
	
	void delete(T entity);
	
	boolean exists(T entity);
	
	/**
	 * Get entity by uri.
	 * @param uri
	 * @return
	 */
	T get(Uri uri);
	
	List<T> getAll();

}
