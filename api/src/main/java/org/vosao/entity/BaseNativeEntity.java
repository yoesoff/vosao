package org.vosao.entity;

import java.io.Serializable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public interface BaseNativeEntity extends Serializable {

	Key getKey();

	void setKey(Key key);
	
	Long getId();
	
	String getIdString();

	void save(Entity entity);
	
	void load(Entity entity);
	
}