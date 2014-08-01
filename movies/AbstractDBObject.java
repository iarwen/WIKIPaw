import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(ignores = { "" })
public abstract class AbstractDBObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

}
