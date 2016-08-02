package cz.cuni.mff.vkgmt.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import cz.cuni.mff.vkgmt.data.common.Property;

public class PropertyJsonDeserializer extends JsonDeserializer<Property> {

	private static final String NAME = "name";
	private static final String PREFIX = "prefix";
	private static final String PROPERTY = "property";

	@Override
    public Property deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        String stringProperty = "";
        Property property = null;
        if (node.isTextual()) {
        	stringProperty = node.asText();
        	property = new Property(stringProperty);
        } else if (node.has(PROPERTY)) {
        	stringProperty = node.get(PROPERTY).asText();
        	property = new Property(stringProperty);
        } else {
        	String prefix = node.get(PREFIX).asText();
        	String name = node.get(NAME).asText();
        	property = new Property(prefix, name);
        }
        return property;
    }
	
}
