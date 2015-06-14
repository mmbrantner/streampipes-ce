package de.fzi.cep.sepa.actions.samples.histogram;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;

public class HistogramController implements SemanticEventConsumerDeclarer{

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("/histogram", "Histogram", "Generates a histogram for time-series data", "http://localhost:8080/img");
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI(SO.Number, MhWirth.RamVelSetpoint, MhWirth.RamPosSetpoint));
		eventProperties.add(e1);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);		
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		sec.addEventStream(stream1);
	
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Mapping", "Select Mapping"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public boolean invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean detachRuntime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		// TODO Auto-generated method stub
		return null;
	}

}