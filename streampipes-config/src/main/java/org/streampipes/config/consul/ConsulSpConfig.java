package org.streampipes.config.consul;

import com.google.common.base.Optional;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import org.streampipes.config.SpConfig;
import org.streampipes.config.SpConfigChangeCallback;

import java.util.HashMap;
import java.util.Map;

public class ConsulSpConfig extends SpConfig implements Runnable {
//    final static Logger logger = LogUtil.getInstance(ConsulSpConfig.class);

//public class ConsulSpConfig extends SpConfig {
    private static final String CONSUL_ENV_LOCATION = "CONSUL_LOCATION";
    private static final String SERVICE_ROUTE_PREFIX = "sp/v1/";
    private String serviceName;
    private  KeyValueClient kvClient;


    // TODO Implement mechanism to update the client when some configutation parameters change in Consul
    private SpConfigChangeCallback callback;
    private Map<String, Object> configProps;

    public ConsulSpConfig(String serviceName) {
        super(serviceName);
        //TDOO use consul adress from an environment variable
        Map<String, String> env = System.getenv();
        Consul consul;
        if (env.containsKey(CONSUL_ENV_LOCATION)) {
            consul = Consul.builder().withUrl(CONSUL_ENV_LOCATION).build(); // connect to Consul on localhost
        } else {
            consul = Consul.builder().build();
        }

//        Consul consul = Consul.builder().build(); // connect to Consul on localhost
        kvClient = consul.keyValueClient();

        this.serviceName = serviceName;
    }

    public ConsulSpConfig(String serviceName, SpConfigChangeCallback callback) {
        this(serviceName);
        this.callback = callback;
        this.configProps = new HashMap<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        Consul consulThread = Consul.builder().build(); // connect to Consul on localhost
        KeyValueClient kvClientThread = consulThread.keyValueClient();
        while (true) {

            configProps.keySet().stream().forEach((s) -> {
                Optional<Value> te = kvClientThread.getValue(addSn(s));
                if (!te.get().getValueAsString().get().equals(configProps.get(s))) {
                    callback.onChange();
                    configProps.put(s, te.get().getValueAsString().get());
                }
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void register(String key, boolean defaultValue, String description) {
        register(key, Boolean.toString(defaultValue), description);
    }

    @Override
    public void register(String key, int defaultValue, String description) {
        register(key, Integer.toString(defaultValue), description);
    }

    @Override
    public void register(String key, double defaultValue, String description) {
        register(key, Double.toString(defaultValue), description);

    }

    @Override
    public void register(String key, String defaultValue, String description) {

        Optional<String> i = kvClient.getValueAsString(addSn(key));
        // TODO this check does not work
        if (!i.isPresent()) {
            kvClient.putValue(addSn(key), defaultValue);
            kvClient.putValue(addSn(key) + "_description", description);
        }

        if (configProps != null) {
            configProps.put(key, getString(key));
        }
    }

    @Override
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    @Override
    public int getInteger(String key) {
        return Integer.parseInt(getString(key));
    }

    @Override
    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    @Override
    public String getString(String key) {
        Optional<String> os = kvClient.getValueAsString(addSn(key));

        String s = os.get();
        return s;

//        return kvClient.getValueAsString(addSn(key)).get();
    }

    @Override
    public void setBoolean(String key, Boolean value) {
        setString(key, value.toString());
    }

    @Override
    public void setInteger(String key, int value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setDouble(String key, double value) {
        setString(key, String.valueOf(value));
    }

    @Override
    public void setString(String key, String value) {
        kvClient.putValue(addSn(key), value);
    }

    private String addSn(String key) {
       return SERVICE_ROUTE_PREFIX + serviceName + "/" + key;
    }


}