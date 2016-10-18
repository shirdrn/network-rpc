package cn.shiyanjun.platform.network.api;

import cn.shiyanjun.platform.api.common.Id;
import cn.shiyanjun.platform.api.common.Typeable;

public interface Message<T> extends Id<Long>, Typeable {

	void setBody(T body);
	T getBody();
}
