package org.spoutcraft.launcher.technic;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

//TODO: Bug SCT again when he is less busy
public class ModDeserializer extends JsonDeserializer<Mod> {

	@Override
	public Mod deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		@SuppressWarnings("unused")
		String tmp = jp.getText(); // { start bracket
		jp.nextToken();
		String key = jp.getText();
		jp.nextToken();
		String value = jp.getText();
		jp.nextToken();
		tmp = jp.getText(); // } end bracket
		
		return new Mod(key, value);
	}

}
