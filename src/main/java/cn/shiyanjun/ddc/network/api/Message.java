package cn.shiyanjun.ddc.network.api;

import cn.shiyanjun.ddc.api.common.Id;
import cn.shiyanjun.ddc.api.common.Typeable;

public interface Message<T> extends Id<Long>, Typeable {

	void setBody(T body);
	T getBody();
}
