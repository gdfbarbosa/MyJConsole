package br.com.movbr.jvm.model;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class HibernateUtils {
	@SuppressWarnings("unchecked")
	public static <T> T initializeAndUnproxy(T entity) {
	    if (entity == null) {
	        throw new NullPointerException("Entity passed for initialization is null");
	    }
	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
	    }
	    return entity;
	}
}
