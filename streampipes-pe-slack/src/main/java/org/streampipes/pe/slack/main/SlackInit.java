package org.streampipes.pe.slack.main;

import org.streampipes.pe.slack.sec.SlackNotificationController;
import org.streampipes.pe.slack.sep.SlackProducer;
import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;

public class SlackInit extends ContainerModelSubmitter {
    @Override
    public void init() {
        DeclarersSingleton.getInstance().setRoute("slack");
        DeclarersSingleton.getInstance()
                .add(new SlackNotificationController())
                .add(new SlackProducer());
    }
}