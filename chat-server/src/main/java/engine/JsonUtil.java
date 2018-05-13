/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
public class JsonUtil {

	public static <T> String objectToJson(T obj) {
		JSONSerializer serializer = new JSONSerializer();
		return serializer.serialize(obj);
	}

	public static <T> T jsonToObject(String json, Class<T> clazz) {
		JSONDeserializer<T> der = new JSONDeserializer<T>();
		return der.deserialize(json);
	}

}
