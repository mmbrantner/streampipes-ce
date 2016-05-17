package de.fzi.proasense.hella.streams;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.proasense.hella.config.HellaVariables;

public class HumidityStream extends EnvironmentalDataStream {

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = prepareStream(HellaVariables.Humidity.topic());
		
		EventSchema schema = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.addAll(getPreparedProperties());
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "humidity", "", Utils.createURI(SO.Number)));
				
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setName(HellaVariables.Humidity.eventName());
		stream.setDescription(HellaVariables.Humidity.description());
		stream.setUri(sep.getUri() + "/humidity");
		
		return stream;
	}

}