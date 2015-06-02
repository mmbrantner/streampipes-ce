package de.fzi.cep.sepa.manager.matching.output;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ListOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;

public class SchemaOutputCalculator {

	private OutputStrategy outputStrategy;
	private boolean propertyUpdated;
		
	public EventSchema calculateOutputSchema(EventStream outputStream, List<OutputStrategy> strategies)
	{
		EventSchema outputSchema = outputStream.getEventSchema();
		for(OutputStrategy strategy : strategies)
		{
			if (strategy instanceof AppendOutputStrategy)
			{
				List<EventProperty> existingProperties = outputSchema.getEventProperties();
				AppendOutputStrategy thisStrategy = (AppendOutputStrategy) strategy;
				List<EventProperty> properties = rename(existingProperties, thisStrategy.getEventProperties());
				//List<EventProperty> properties = thisStrategy.getEventProperties();
				properties.addAll(existingProperties);
				return generateSchema(properties);
			}
			else if (strategy instanceof RenameOutputStrategy)
			{
				return outputSchema;
			}
			else if (strategy instanceof FixedOutputStrategy)
			{
				FixedOutputStrategy thisStrategy = (FixedOutputStrategy) strategy;
				return generateSchema(thisStrategy.getEventProperties());
			}
			else if (strategy instanceof CustomOutputStrategy)
			{
				CustomOutputStrategy thisStrategy = (CustomOutputStrategy) strategy;
				return generateSchema(thisStrategy.getEventProperties());
			}
			else if (strategy instanceof ListOutputStrategy)
			{
				ListOutputStrategy thisStrategy = (ListOutputStrategy) strategy;
				return makeList(outputSchema.getEventProperties(), thisStrategy.getPropertyName());
			}
		}
		// TODO exceptions
		return null;
	}
	
	private EventSchema makeList(List<EventProperty> schemaProperties, String propertyName)
	{
		EventPropertyList list = new EventPropertyList();
		list.setEventProperties(schemaProperties);
		list.setRuntimeName(propertyName);
		list.setRdfId(new URIKey(URI.create(schemaProperties.get(0).getRdfId()+"-list")));
		EventSchema schema = new EventSchema();
		schema.setEventProperties(Utils.createList(list));
		return schema;
	}
	
	private List<EventProperty> rename(
			List<EventProperty> existingProperties,
			List<EventProperty> appendProperties) {
		
		List<EventProperty> newEventProperties = new ArrayList<EventProperty>();
		for(EventProperty p : appendProperties)
		{
			int i = 1;
			EventProperty newProperty = p;
			while(isAlreadyDefined(existingProperties, newProperty))
			{
				if (newProperty instanceof EventPropertyPrimitive) 
					{
						EventPropertyPrimitive primitive = (EventPropertyPrimitive) newProperty;
						newProperty = new EventPropertyPrimitive(primitive.getPropertyType(), primitive.getRuntimeName() +i, primitive.getMeasurementUnit(), primitive.getSubClassOf());
						newProperty.setRdfId(new URIKey(URI.create(primitive.getRdfId().toString() +i)));
					}
				if (newProperty instanceof EventPropertyNested)
					{
						EventPropertyNested nested = (EventPropertyNested) newProperty;
						
						//TODO: hack
						List<EventProperty> nestedProperties = new ArrayList<>();
						
						for(EventProperty np : nested.getEventProperties())
						{
							if (np instanceof EventPropertyPrimitive)
							{
								EventPropertyPrimitive thisPrimitive = (EventPropertyPrimitive) np;
								EventProperty newNested = new EventPropertyPrimitive(thisPrimitive.getPropertyType(), thisPrimitive.getRuntimeName(), thisPrimitive.getMeasurementUnit(), thisPrimitive.getSubClassOf());	
								//newNested.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
								newNested.setRdfId(new URIKey(URI.create(thisPrimitive.getRdfId().toString())));
								nestedProperties.add(newNested);
							}
								
						}
						newProperty = new EventPropertyNested(nested.getRuntimeName() +i, nestedProperties);
						//newProperty = new EventPropertyNested(nested.getPropertyName() +i, nested.getEventProperties());
						//newProperty.setRdfId(new URIKey(URI.create("urn:fzi.de:sepa:" +UUID.randomUUID().toString())));
						newProperty.setRdfId(new URIKey(URI.create(nested.getRdfId().toString() +i)));
					}
				i++;
			}
			newEventProperties.add(newProperty);
		}
		updateOutputStrategy(newEventProperties);
		return newEventProperties;
		
	}
	
	private void updateOutputStrategy(List<EventProperty> eventProperties) {
		AppendOutputStrategy newOutputStrategy = new AppendOutputStrategy(eventProperties);
		this.outputStrategy = newOutputStrategy;
	}
	
	private boolean isAlreadyDefined(List<EventProperty> existingProperties, EventProperty appendProperty)
	{
		for(EventProperty existingAppendProperty : existingProperties)
		{
			if (appendProperty.getRuntimeName().equals(existingAppendProperty.getRuntimeName()))
				return true;
		}
		return false;
	}

	public EventSchema calculateOutputSchema(EventStream stream1, EventStream stream2, List<OutputStrategy> strategies)
	{
		for(OutputStrategy strategy : strategies)
		{
			List<EventProperty> properties = new ArrayList<>();
			if (strategy instanceof CustomOutputStrategy)
			{
				properties = stream1.getEventSchema().getEventProperties();
				properties.addAll(stream2.getEventSchema().getEventProperties());
			} 
			else if (strategy instanceof AppendOutputStrategy)
			{
				properties = stream1.getEventSchema().getEventProperties();
				//properties.addAll(rename(properties, stream2.getEventSchema().getEventProperties()));
				//properties.addAll(rename(properties, ((AppendOutputStrategy) strategy).getEventProperties()));
				properties.addAll(((AppendOutputStrategy) strategy).getEventProperties());
			}
			return generateSchema(properties);
		}
		//TODO exceptions
		return null;
	}
	
	private EventSchema generateSchema(List<EventProperty> properties)
	{
		EventSchema result = new EventSchema();
		for(EventProperty p : properties)
			result.addEventProperty(p);
		return result;
	}

	public OutputStrategy getOutputStrategy() {
		return outputStrategy;
	}

	public boolean isPropertyUpdated() {
		return propertyUpdated;
	}

	public void setPropertyUpdated(boolean propertyUpdated) {
		this.propertyUpdated = propertyUpdated;
	}
	
	
	
}