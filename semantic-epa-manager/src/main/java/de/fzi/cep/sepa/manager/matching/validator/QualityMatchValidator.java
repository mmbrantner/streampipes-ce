package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventQuality;

public class QualityMatchValidator implements Validator<List<EventQuality>> {

	@Override
	public boolean validate(List<EventQuality> left, List<EventQuality> right) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean validate(List<EventQuality> firstLeft,
			List<EventQuality> secondLeft, List<EventQuality> right) {
		return validate(firstLeft, right) && validate(secondLeft, right);
	}

	@Override
	public boolean validate(List<EventQuality> firstLeft,
			List<EventQuality> secondLeft, List<EventQuality> firstRight,
			List<EventQuality> secondRight) {
		// TODO Auto-generated method stub
		return false;
	}

}