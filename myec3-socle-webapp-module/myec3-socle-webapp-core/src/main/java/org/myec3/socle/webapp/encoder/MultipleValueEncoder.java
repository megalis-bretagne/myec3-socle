package org.myec3.socle.webapp.encoder;

import java.util.List;

public interface MultipleValueEncoder<V> {

	String toClient(V value);

	List<V> toValue(String[] clientValue);
}
