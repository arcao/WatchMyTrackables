package com.arcao.utils.marshalling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface Marshaller<Object> {
	Object from(ObjectInput in) throws IOException;
	void to(ObjectOutput out, Object data) throws IOException;
}
