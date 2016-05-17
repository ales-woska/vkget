package cz.cuni.mff.vkget.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cz.cuni.mff.vkget.data.common.Type;

public class TypeJsonDeserializer extends JsonDeserializer<Type> {

	@Override
    public Type deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        String stringType = "";
        Type type = null;
        if (node.isTextual()) {
        	stringType = node.asText();
        	type = new Type(stringType);
        } else {
        	String prefix = node.get("prefix").asText();
        	String name = node.get("name").asText();
        	type = new Type(prefix, name);
        }
        return type;
    }
	
}
