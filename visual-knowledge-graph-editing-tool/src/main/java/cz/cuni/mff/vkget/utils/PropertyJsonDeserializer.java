package cz.cuni.mff.vkget.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cz.cuni.mff.vkget.data.common.Property;

public class PropertyJsonDeserializer extends JsonDeserializer<Property> {

	@Override
    public Property deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        String stringProperty = "";
        Property property = null;
        if (node.isTextual()) {
        	stringProperty = node.asText();
        	property = new Property(stringProperty);
        } else {
        	String prefix = node.get("prefix").asText();
        	String name = node.get("name").asText();
        	property = new Property(prefix, name);
        }
        return property;
    }
	
}
