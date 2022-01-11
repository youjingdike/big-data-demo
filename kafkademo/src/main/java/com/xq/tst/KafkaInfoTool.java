/*
package com.xq.tst;

import kafka.api.GroupCoordinatorRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.ErrorMapping;
import kafka.common.OffsetMetadataAndError;
import kafka.common.TopicAndPartition;
import kafka.javaapi.GroupCoordinatorResponse;
import kafka.javaapi.OffsetFetchRequest;
import kafka.javaapi.OffsetFetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.network.BlockingChannel;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * @author : tyhy_xingqian
 * @Date  : Create in 14:35 2019/7/15
 * @Version : 1.0
 *//*

public class KafkaInfoTool {
  private Logger logger = LoggerFactory.getLogger(KafkaInfoTool.class);
  final int timeOut = 100000;
  final int bufferSize = 128 * 1024;
  private static KafkaInfoTool KAFKAINFOTOOL = new KafkaInfoTool();
  private KafkaInfoTool() {
  }

  public static KafkaInfoTool getInstance() {
    return KAFKAINFOTOOL;
  }

*/
/**
 * 获取topic最后的偏移量(与分组无关)
 * @param brokerList
 * @param topics
 * @param clientId
 * @return
 *//*

public Map<TopicAndPartition, Long> getOffsetWithTime(String brokerList, List<String> topics,
                                                  String clientId,long time) {

    Map<TopicAndPartition, Long> topicAndPartitionLongMap = new HashMap<>(32);

    Map<TopicAndPartition, BrokerEndPoint > topicAndPartitionBrokerMap =
        KafkaInfoTool.getInstance().findLeader(brokerList, topics);
    for (Map.Entry<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerEntry : topicAndPartitionBrokerMap
        .entrySet()) {
      // get leader broker
      BrokerEndPoint  leaderBroker = topicAndPartitionBrokerEntry.getValue();
      if (leaderBroker!=null) {

        SimpleConsumer simpleConsumer = new SimpleConsumer(leaderBroker.host(), leaderBroker.port(),
                timeOut, bufferSize, clientId);

        long readOffset = getTopicAndPartitionOffset(simpleConsumer,
            topicAndPartitionBrokerEntry.getKey(), clientId,time);

        topicAndPartitionLongMap.put(topicAndPartitionBrokerEntry.getKey(), readOffset);
        if (simpleConsumer!=null) {
            simpleConsumer.close();
        }
      } else {
        logger.error("topic:{},partition:{},do not have leader.",topicAndPartitionBrokerEntry.getKey().topic(),topicAndPartitionBrokerEntry.getKey().partition());
      }
    }
    return topicAndPartitionLongMap;

  }

  */
/**
 * 获取topic最后的偏移量(与分组无关)
 * @param brokerList
 * @param topics
 * @param clientId
 * @return
 *//*

public Map<TopicAndPartition, Long> getLastOffset(String brokerList, List<String> topics,
                                                  String clientId) {

    Map<TopicAndPartition, Long> topicAndPartitionLongMap = new HashMap<>(32);

    Map<TopicAndPartition, BrokerEndPoint > topicAndPartitionBrokerMap =
        KafkaInfoTool.getInstance().findLeader(brokerList, topics);
    for (Map.Entry<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerEntry : topicAndPartitionBrokerMap
        .entrySet()) {
      // get leader broker
      BrokerEndPoint  leaderBroker = topicAndPartitionBrokerEntry.getValue();
      if (leaderBroker!=null) {

        SimpleConsumer simpleConsumer = new SimpleConsumer(leaderBroker.host(), leaderBroker.port(),
                timeOut, bufferSize, clientId);

        long readOffset = getTopicAndPartitionOffset(simpleConsumer,
            topicAndPartitionBrokerEntry.getKey(), clientId,kafka.api.OffsetRequest.LatestTime());

        topicAndPartitionLongMap.put(topicAndPartitionBrokerEntry.getKey(), readOffset);
        if (simpleConsumer!=null) {
            simpleConsumer.close();
        }
      } else {
        logger.error("topic:{},partition:{},do not have leader.",topicAndPartitionBrokerEntry.getKey().topic(),topicAndPartitionBrokerEntry.getKey().partition());
      }
    }
    return topicAndPartitionLongMap;

  }

  */
/**
   * 获取topic最早的偏移量(与分组无关)
   * @param brokerList
   * @param topics
   * @param clientId
   * @return
   *//*

  public Map<TopicAndPartition, Long> getEarliestOffset(String brokerList, List<String> topics,
      String clientId) {

    Map<TopicAndPartition, Long> topicAndPartitionLongMap = new HashMap<>(32);

    Map<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerMap =
        KafkaInfoTool.getInstance().findLeader(brokerList, topics);

    for (Map.Entry<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerEntry : topicAndPartitionBrokerMap
        .entrySet()) {
      // get leader broker
    	BrokerEndPoint leaderBroker = topicAndPartitionBrokerEntry.getValue();

      SimpleConsumer simpleConsumer = new SimpleConsumer(leaderBroker.host(), leaderBroker.port(),
              timeOut, bufferSize, clientId);

      long readOffset = getTopicAndPartitionOffset(simpleConsumer,
          topicAndPartitionBrokerEntry.getKey(), clientId,kafka.api.OffsetRequest.EarliestTime());

      topicAndPartitionLongMap.put(topicAndPartitionBrokerEntry.getKey(), readOffset);
      if (simpleConsumer!=null) {
    	  simpleConsumer.close();
      }
    }
    
    return topicAndPartitionLongMap;

  }

  */
/**
   * 获取某个消费者组的偏移量
   * @param brokerList
   * @param topics
   * @param clientId
   * @return
   *//*

  public Map<TopicAndPartition, Long> getConsumerGroupOffset(String group,String brokerList, List<String> topics,
    String clientId) {
    Map<TopicAndPartition, Long> resultMap = new HashMap(16);
    int correlationId = 0;
    BlockingChannel channel = null;
    Map<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerMap = KafkaInfoTool.getInstance().findLeader(brokerList,topics);
    if (topicAndPartitionBrokerMap==null || topicAndPartitionBrokerMap.isEmpty()) {
      return resultMap;
    }

    for (BrokerEndPoint value : topicAndPartitionBrokerMap.values()) {
      channel = new BlockingChannel(value.host(), value.port(),
              BlockingChannel.UseDefaultBufferSize(),
              BlockingChannel.UseDefaultBufferSize(),
              5000);
      channel.connect();
      break;
    }
    if (channel != null) {
      try {
        channel.send(new GroupCoordinatorRequest(group,kafka.api.OffsetRequest.CurrentVersion(),correlationId,clientId));
        GroupCoordinatorResponse groupCoordinatorResponse = GroupCoordinatorResponse.readFrom(channel.receive().payload());
        List<TopicAndPartition> partitions = new ArrayList<>(topicAndPartitionBrokerMap.keySet());
        if (groupCoordinatorResponse.errorCode() == ErrorMapping.NoError()) {
          BrokerEndPoint coordinator = groupCoordinatorResponse.coordinator();
          channel.disconnect();
          channel = new BlockingChannel(coordinator.host(), coordinator.port(),
                  BlockingChannel.UseDefaultBufferSize(),
                  BlockingChannel.UseDefaultBufferSize(),
                  5000);
          channel.connect();
          OffsetFetchRequest fetchRequest = new OffsetFetchRequest(
                  group,
                  partitions,
                  (short)1,//注意：version 1 and above fetch from Kafka, version 0 fetches from ZooKeeper
                  correlationId,
                  clientId);
          channel.send(fetchRequest.underlying());
          OffsetFetchResponse fetchResponse = OffsetFetchResponse.readFrom(channel.receive().payload());

          for (Map.Entry<TopicAndPartition, BrokerEndPoint> entry : topicAndPartitionBrokerMap.entrySet()) {

            TopicAndPartition topicAndPartition = entry.getKey();

            OffsetMetadataAndError result = fetchResponse.offsets().get(topicAndPartition);
            short offsetFetchErrorCode = result.error();
            logger.info("offsetFetchErrorCode：{}",offsetFetchErrorCode);
            if (offsetFetchErrorCode == ErrorMapping.NotCoordinatorForConsumerCode()) {
              logger.error("topic:{},partition:{},NotCoordinatorForConsumerCode",topicAndPartition.topic(), topicAndPartition.partition());
            } else {
              resultMap.put(topicAndPartition, result.offset());
            }

          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage(),e);
      } finally {
        channel.disconnect();
      }
    }

    return resultMap;
  }


  */
/**
   * 得到所有的 TopicAndPartition
   *
   * @param brokerList
   * @param topics
   * @return topicAndPartitions
   *//*

  private Map<TopicAndPartition, BrokerEndPoint> findLeader(String brokerList, List<String> topics) {
    // create array list of TopicAndPartition
    Map<TopicAndPartition, BrokerEndPoint> topicAndPartitionBrokerMap = new HashMap<>(32);
    //为null会报错，为空会返回所有topic的信息
    if (topics == null || topics.isEmpty()) {
      return topicAndPartitionBrokerMap;
    }
    // get broker's url array
    String[] brokerUrlArray = getBorkerUrlFromBrokerList(brokerList);
    // get broker's port map
    Map<String, Integer> brokerPortMap = getPortFromBrokerList(brokerList);


    for (String broker : brokerUrlArray) {
      SimpleConsumer consumer = null;
      try {
        // new instance of simple Consumer
        consumer = new SimpleConsumer(broker, brokerPortMap.get(broker), timeOut, bufferSize,
            "leaderLookup" + System.currentTimeMillis());

        TopicMetadataRequest req = new TopicMetadataRequest(topics);

        TopicMetadataResponse resp = consumer.send(req);

        List<TopicMetadata> metaData = resp.topicsMetadata();

        for (TopicMetadata item : metaData) {
          for (PartitionMetadata part : item.partitionsMetadata()) {
            TopicAndPartition topicAndPartition =
                new TopicAndPartition(item.topic(), part.partitionId());
            topicAndPartitionBrokerMap.put(topicAndPartition, part.leader());
          }
        }
        break;
      } catch (Exception e) {
        logger.error("from broker:{}, findLeader have exception:{}",new Object[]{broker,e.getStackTrace()});
      } finally {
        if (consumer != null) {
          consumer.close();
        }
      }
    }
    return topicAndPartitionBrokerMap;
  }

  */
/**
   * get earliest offset
   * @param consumer
   * @param topicAndPartition
   * @param clientId
   * @param time
   * @return
   *//*

  private long getTopicAndPartitionOffset(SimpleConsumer consumer,
                                          TopicAndPartition topicAndPartition, String clientId, long time) {
    Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo =
        new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>(32);

    //参数time：可以指定一个时间，但是实际找到的是 “最近修改时间早于目标timestamp的最近修改的segment file的起始offset”
    requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(
            time, 1));

    OffsetRequest request = new OffsetRequest(
        requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
        clientId);

    OffsetResponse response = consumer.getOffsetsBefore(request);

    if (response.hasError()) {
      logger.error("Error fetching data Offset Data the Broker. Reason: {}",response.errorCode(topicAndPartition.topic(), topicAndPartition.partition()));
      return 0;
    }
    //注意通过timeStamp查询的时候，这里面可能会返回空
    long[] offsets = response.offsets(topicAndPartition.topic(), topicAndPartition.partition());
    return offsets[0];
  }
  
  */
/**
   * 得到所有的broker url,url之间用','隔开
   *
   * @param brokerlist
   * @return
   *//*

  private String[] getBorkerUrlFromBrokerList(String brokerlist) {
    String[] brokers = brokerlist.split(",");
    for (int i = 0; i < brokers.length; i++) {
      brokers[i] = brokers[i].split(":")[0];
    }
    return brokers;
  }

  */
/**
   * 得到broker url 与 其port 的映射关系
   *
   * @param brokerlist
   * @return
   *//*

  private Map<String, Integer> getPortFromBrokerList(String brokerlist) {
    Map<String, Integer> map = new HashMap<String, Integer>(32);
    String[] brokers = brokerlist.split(",");
    for (String item : brokers) {
      String[] itemArr = item.split(":");
      if (itemArr.length > 1) {
        map.put(itemArr[0], Integer.parseInt(itemArr[1]));
      }
    }
    return map;
  }

  public static void main(String[] args) {
    KafkaConsumer kafkaConsumer = new KafkaConsumer(null);
//    kafkaConsumer.
  }
}*/
