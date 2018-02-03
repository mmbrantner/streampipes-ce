//import org.apache.commons.io.FileUtils;
//import org.eclipse.rdf4j.repository.RepositoryException;
//import org.eclipse.rdf4j.rio.RDFHandlerException;
//import org.eclipse.rdf4j.rio.RDFParseException;
//import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
//import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
//import org.streampipes.model.graph.DataProcessorInvocation;
//import org.streampipes.serializers.jsonld.JsonLdTransformer;
//import org.streampipes.serializers.json.GsonSerializer;
//import org.streampipes.storage.couchdb.impl.SepaInvocationStorageImpl;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//
//
//public class TestInvocationGraphToCouchDb {
//
//	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException, RDFHandlerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
//	{
//		//SepaDescription invocationGraph = new SepaDescription(new JsonLdTransformer().fromJsonLd(FileUtils.readFileToString(new File("src/test/resources/sepa-description-graph-sample.jsonld"), "UTF-8"), SepaDescription.class));
//		DataProcessorInvocation invocationGraph = new DataProcessorInvocation(new JsonLdTransformer().fromJsonLd(FileUtils.readFileToString(new File("src/test/resources/sepa-invocation-graph-sample.jsonld"), "UTF-8"), DataProcessorInvocation.class));
//
//
//		//System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(invocationGraph)));
//		System.out.println(GsonSerializer.getGson().toJson(invocationGraph));
//		new SepaInvocationStorageImpl().storeSepaInvocation(invocationGraph);
//
//		DataProcessorInvocation invocation2 = new SepaInvocationStorageImpl().getSepaInvovation("2b1fe4a1422d4ab8b4df7bc916d7364c");
//
//	}
//}
