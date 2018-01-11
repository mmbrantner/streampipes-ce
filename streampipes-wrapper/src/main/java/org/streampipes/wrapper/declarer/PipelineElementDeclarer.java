package org.streampipes.wrapper.declarer;

import org.streampipes.model.Response;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.sdk.extractor.AbstractParameterExtractor;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class PipelineElementDeclarer<B extends BindingParams, EPR extends
        PipelineElementRuntime, I
        extends InvocableStreamPipesEntity, EX extends AbstractParameterExtractor<I>> {

  protected EPR epRuntime;
  protected String elementId;

  public Response invokeEPRuntime(I graph) {

    try {
      elementId = graph.getElementId();
      epRuntime = getRuntime(graph);
      epRuntime.bindRuntime();
      return new Response(graph.getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(graph.getElementId(), false, e.getMessage());
    }

  }

  public Response detachRuntime(String pipelineId) {
    try {
      epRuntime.discardRuntime();
      return new Response(elementId, true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(elementId, false, e.getMessage());
    }
  }

  protected abstract EX getExtractor(I graph);

  public abstract EPR getRuntime(I graph);




}
