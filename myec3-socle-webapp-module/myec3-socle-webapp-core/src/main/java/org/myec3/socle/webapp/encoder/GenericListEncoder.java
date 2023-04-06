package org.myec3.socle.webapp.encoder;

import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.commons.services.PropertyAccess;
import org.apache.tapestry5.commons.util.CollectionFactory;

public class GenericListEncoder<T> implements ValueEncoder<T> {
	private List<T> _objectList;
	private final PropertyAccess _access;
	private String _valueFieldName = null;

	public GenericListEncoder(List<T> objectList) {
		this(objectList, null, null);
	}

	public GenericListEncoder(List<T> objectList, String valueFieldName, PropertyAccess propertyAccess) {
		assert objectList != null;

		// more carfully i think, so we copy the object list
		_objectList = CollectionFactory.newList(objectList);
		_valueFieldName = valueFieldName;
		_access = propertyAccess;
	}

	public String toClient(T serverValue) {
		return getServerValueAsString(serverValue);
	}

	public T toValue(String clientValue) {
		T serverValue = null;

		for (T obj : _objectList) {
			String value = getServerValueAsString(obj);
			if (value.equals(clientValue)) {
				serverValue = obj;
				break;
			}
		}

		return serverValue;
	}

	private String getServerValueAsString(T serverValue) {
		String clientValue = "";

		if (_valueFieldName != null && _access != null)
			clientValue = String.valueOf(_access.get(serverValue, _valueFieldName));
		else
			clientValue = String.valueOf(serverValue);

		return clientValue;
	}

}
