/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.connect.adapter.generic.protocol.stream;

import org.apache.commons.io.IOUtils;
import org.streampipes.connect.SendToPipeline;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.AdapterType;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MqttProtocol extends BrokerProtocol {

  public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/mqtt";

  private Thread thread;
  private MqttConsumer mqttConsumer;

  public MqttProtocol() {
  }

  public MqttProtocol(Parser parser, Format format, String brokerUrl, String topic) {
    super(parser, format, brokerUrl, topic);
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
    String brokerUrl = extractor.singleValue("broker_url");
    String topic = extractor.singleValue("topic");

    return new MqttProtocol(parser, format, brokerUrl, topic);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID, "MQTT", "Consumes messages from a broker using " +
            "the MQTT protocol")
            .iconUrl("mqtt.png")
            .category(AdapterType.Generic, AdapterType.Manufacturing)
            .sourceType(AdapterSourceType.STREAM)
            .requiredTextParameter(Labels.from("broker_url", "Broker URL",
                    "Example: tcp://test-server.com:1883 (Protocol required. Port required)"))
            .requiredTextParameter(Labels.from("topic", "Topic","Example: test/topic"))
            .build();
  }

  @Override
  protected List<byte[]> getNByteElements(int n) throws ParseException {
    List<byte[]> elements = new ArrayList<>();
    int i = 0;

    InternalEventProcessor<byte[]> eventProcessor = elements::add;

    MqttConsumer consumer = new MqttConsumer(this.brokerUrl, this.topic, eventProcessor);

    Thread thread = new Thread(consumer);
    thread.start();

    while (consumer.getMessageCount() < n) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return elements;
  }

  @Override
  public void run(AdapterPipeline adapterPipeline) {
    SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
    this.mqttConsumer = new MqttConsumer(this.brokerUrl, this.topic, new MqttProtocol.EventProcessor(stk));

    thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void stop() {
    this.mqttConsumer.close();
  }

  @Override
  public String getId() {
    return ID;
  }

  private class EventProcessor implements InternalEventProcessor<byte[]> {
    private SendToPipeline stk;

    public EventProcessor(SendToPipeline stk) {
      this.stk = stk;
    }

    @Override
    public void onEvent(byte[] payload) {
      try {
        parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), stk);
      } catch (IOException e) {
        e.printStackTrace();
        //logger.error("Adapter " + ID + " could not read value!",e);
      } catch (ParseException e) {
        e.printStackTrace();
    }
    }
  }
}
