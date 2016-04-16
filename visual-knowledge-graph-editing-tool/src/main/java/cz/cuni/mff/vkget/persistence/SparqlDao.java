package cz.cuni.mff.vkget.persistence;

import cz.cuni.mff.vkget.data.RdfEntity;

public interface SparqlDao<T extends RdfEntity> {
	
	void insert(T entity);
	T load(String uri);

}
