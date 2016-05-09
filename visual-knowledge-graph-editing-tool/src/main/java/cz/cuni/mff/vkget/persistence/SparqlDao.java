package cz.cuni.mff.vkget.persistence;

import cz.cuni.mff.vkget.data.RdfEntity;

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
	
	/**
	 * Get entity by uri.
	 * @param uri
	 * @return
	 */
	T load(String uri);

}
