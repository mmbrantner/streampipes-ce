## StreamPipes Maven Archetype for Flink-based Data Processors

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-processors-flink          \
			-DarchetypeVersion=0.60.2-SNAPSHOT                \
			-DgroupId=my.test.groupId \
			-DartifactId=my-test-artifact-id
			-DclassNamePrefix=MyProcessor
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

