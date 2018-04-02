package org.ipph.separator;

import java.util.List;

import org.ipph.exception.SeparatorException;

public interface Separator {

	public List<Object> separate(String args,Object value) throws SeparatorException;
}
