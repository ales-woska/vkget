package cz.cuni.mff.vkgmt.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cz.cuni.mff.vkgmt.data.common.Type;

public class TypeJsonDeserializer extends JsonDeserializer<Type> {

	private static final String NAME = "name";
	private static final String PREFIX = "prefix";
	private static final String TYPE = "type";

	@Override
    public Type deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        String stringType = "";
        Type type = null;
        if (node.isTextual()) {
        	stringType = node.asText();
        	type = new Type(stringType);
        } else if (node.has(TYPE)) {
        	stringType = node.get(TYPE).asText();
        	type = new Type(stringType);
        } else {
        	String prefix = node.get(PREFIX).asText();
        	String name = node.get(NAME).asText();
        	type = new Type(prefix, name);
        }
        return type;
    }
	
}
