package de.fzi.cep.sepa.rest.impl;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.IPipelineElement;
import de.fzi.cep.sepa.storage.filter.Filter;

@Path("/v2/users/{username}/sepas")
public class SemanticEventProcessingAgent extends AbstractRestInterface implements IPipelineElement {

	@GET
	@Path("/available")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getAvailable(@PathParam("username") String username) {
		List<SepaDescription> sepas = Filter.byUri(requestor.getAllSEPAs(), userService.getAvailableSepaUris(username));
		return ok(sepas);
	}
	
	@GET
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getFavorites(@PathParam("username") String username) {
		List<SepaDescription> sepas = Filter.byUri(requestor.getAllSEPAs(), userService.getFavoriteSepaUris(username));
		return ok(sepas);
	}

	@GET
	@Path("/own")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getOwn(@PathParam("username") String username) {
		List<SepaDescription> sepas = Filter.byUri(requestor.getAllSEPAs(), userService.getOwnSepaUris(username));
		List<SepaInvocation> si = sepas.stream().map(s -> new SepaInvocation(new SepaInvocation(s))).collect(Collectors.toList());

		return ok(si);
	}

	@POST
	@Path("/favorites")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
		userService.addSepaAsFavorite(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}

	@DELETE
	@Path("/favorites/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		userService.removeSepaFromFavorites(username, decode(elementUri));
		return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
	}
	
	@DELETE
	@Path("/own/{elementUri}")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		try {
			userService.deleteOwnSepa(username, elementUri);
			requestor.deleteSEPA(requestor.getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return constructErrorMessage(Notifications.create(NotificationType.STORAGE_ERROR, e.getMessage()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("/{elementUri}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String getAsJsonLd(@PathParam("elementUri") String elementUri) {
		try {
			return toJsonLd(requestor.getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return toJson(constructErrorMessage(Notifications.create(NotificationType.UNKNOWN_ERROR, e.getMessage())));
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println(new SemanticEventProcessingAgent().getOwn("riemer@fzi.de"));
	}

	@Path("/{elementUri}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
		// TODO Access rights
		try {
			return ok(requestor.getSEPAById(elementUri));
		} catch (URISyntaxException e) {
			return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR, e.getMessage()));
		}
	}

}