# Kafka Tutorial - In-Depth Guide

## Overview
Apache Kafka is a distributed streaming platform that allows applications to publish and subscribe to streams of records. In this project, Kafka is used to publish exchange rate updates to other systems in real-time.

## Architecture in Mobilele Project

```
[ExRatesPublisher] -> [ExRateServiceImpl] -> [KafkaPublicationServiceImpl] -> [Kafka Topic: ex-rates]
                                                                                      |
                                                                               [External Consumers]
```

## Configuration

### 1. Dependencies (build.gradle)
```gradle
implementation 'org.springframework.kafka:spring-kafka'
```

### 2. Application Configuration (application.yaml)
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # Kafka server address
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### 3. Kafka Topic Configuration (KafkaConfig.java)
```java
@Configuration
public class KafkaConfig {
  public static final String EX_RATES_TOPIC = "ex-rates";

  @Bean
  public NewTopic exRatesTopic() {
    return TopicBuilder.name(EX_RATES_TOPIC)
        .partitions(2)        // Number of partitions for parallel processing
        .compact()            // Log compaction enabled
        .build();
  }
}
```

**Key Points:**
- Topic name: `ex-rates`
- 2 partitions for better throughput
- Compacted topic - keeps only latest value per key

## Core Components

### 1. Service Interface (KafkaPublicationService.java)
```java
public interface KafkaPublicationService {
  void publishExRate(ExRateDTO exRateDTO);
}
```

### 2. Implementation (KafkaPublicationServiceImpl.java)
```java
@Service
public class KafkaPublicationServiceImpl implements KafkaPublicationService {
  
  private final KafkaTemplate<String, ExRateDTO> kafkaTemplate;

  @Override
  public void publishExRate(ExRateDTO exRateDTO) {
    kafkaTemplate
        .send(EX_RATES_TOPIC, exRateDTO.currency(), exRateDTO)
        .whenComplete((res, ex) -> {
          if (ex == null) {
            RecordMetadata metadata = res.getRecordMetadata();
            LOGGER.info("Successfully sent key {} to topic/partition/offset={}/{}/{}",
                exRateDTO.currency(),
                metadata.topic(),
                metadata.partition(), 
                metadata.offset()
            );
          } else {
            LOGGER.error("Error producing message with key {}", 
                exRateDTO.currency(), ex);
          }
        });
  }
}
```

**Key Features:**
- **Asynchronous sending** - `send()` returns CompletableFuture
- **Key-based partitioning** - currency as key ensures same currency goes to same partition
- **Callback handling** - `whenComplete()` for success/error handling
- **Detailed logging** - tracks partition and offset information

### 3. Integration with Business Logic (ExRateServiceImpl.java)
```java
@Override
public void publishExRates() {
  List<ExRateDTO> exRates = exRateRepository
      .findAll()
      .stream()
      .sorted(Comparator.comparing(ExRateEntity::getCurrency))
      .map(this::map)
      .toList();

  exRates.forEach(kafkaPublicationService::publishExRate);
}
```

### 4. Startup Publisher (ExRatesPublisher.java)
```java
@Order(1)
@Component
public class ExRatesPublisher implements CommandLineRunner {
  
  private final ExRateService exRateService;

  @Override
  public void run(String... args) throws Exception {
    exRateService.publishExRates();  // Publish all rates on startup
  }
}
```

## Message Structure

### Key: Currency Code (String)
```
"USD", "EUR", "BGN"
```

### Value: ExRateDTO (JSON)
```json
{
  "currency": "EUR",
  "rate": 0.91
}
```

## Data Flow

1. **Application Startup:**
   ```
   ExRatesPublisher (CommandLineRunner) 
   -> ExRateServiceImpl.publishExRates()
   -> KafkaPublicationServiceImpl.publishExRate()
   -> KafkaTemplate.send()
   -> Kafka Topic
   ```

2. **Message Processing:**
   - Each currency rate becomes separate Kafka message
   - Currency code used as partition key
   - Same currency always goes to same partition
   - Enables ordering guarantees per currency

3. **Partitioning Strategy:**
   ```
   Currency "USD" -> hash(USD) % 2 -> Partition 0 or 1
   Currency "EUR" -> hash(EUR) % 2 -> Partition 0 or 1
   ```

## Testing

### Unit Tests (ExRateServiceImplTest.java)
```java
@Mock
private KafkaPublicationService mockKafkaPublicationService;

// Mock is injected to avoid actual Kafka calls during testing
```

### Integration Testing
- Real Kafka integration would require:
  - Test containers with Kafka
  - Consumer verification
  - Message ordering tests

## Production Considerations

### 1. Error Handling
- **Network failures:** Retry mechanism in KafkaTemplate
- **Serialization errors:** Handled by Spring Kafka
- **Topic availability:** Auto-topic creation if enabled

### 2. Performance
- **Batch sending:** Can be configured for better throughput
- **Compression:** GZIP/Snappy for large messages
- **Acks configuration:** Trade-off between speed and durability

### 3. Monitoring
- **Producer metrics:** Success/failure rates
- **Lag monitoring:** Consumer lag tracking
- **Topic health:** Partition leadership, replica status

## Common Patterns

### 1. Fire-and-Forget
```java
kafkaTemplate.send(topic, key, value);  // No callback
```

### 2. Synchronous Send
```java
SendResult<String, ExRateDTO> result = kafkaTemplate.send(topic, key, value).get();
```

### 3. Transactional Sends
```java
@Transactional
public void publishWithTransaction() {
  kafkaTemplate.executeInTransaction(operations -> {
    operations.send(topic, key1, value1);
    operations.send(topic, key2, value2);
    return null;
  });
}
```

## Best Practices

### 1. Key Design
- Use meaningful keys for partitioning
- Ensure even distribution across partitions
- Consider key-based compaction needs

### 2. Error Handling
- Always handle send callbacks
- Implement retry logic for critical messages
- Log failures with sufficient context

### 3. Serialization
- Use schema registry for production
- Version your message formats
- Handle backward/forward compatibility

### 4. Configuration
- Tune batch.size and linger.ms for throughput
- Set appropriate acks level (0, 1, all)
- Configure retries and timeout values

## Example Consumer (Not in this project)

```java
@Component
public class ExRateConsumer {
  
  @KafkaListener(topics = "ex-rates")
  public void handleExRate(ExRateDTO exRate, 
                          @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset) {
    
    log.info("Received exchange rate: {} from partition {} at offset {}", 
             exRate, partition, offset);
    
    // Process the exchange rate update
    updateLocalCache(exRate);
  }
}
```

## Troubleshooting

### Common Issues:
1. **Connection refused:** Check if Kafka is running on localhost:9092
2. **Serialization errors:** Verify JsonSerializer configuration
3. **Topic not found:** Ensure auto-create is enabled or topic exists
4. **Partition assignment:** Check key distribution and partition count

### Debug Commands:
```bash
# List topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe topic
kafka-topics --bootstrap-server localhost:9092 --describe --topic ex-rates

# Consume messages
kafka-console-consumer --bootstrap-server localhost:9092 --topic ex-rates --from-beginning

# Check consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

## Conclusion

This Kafka implementation provides:
- **Reliable messaging** for exchange rate updates
- **Scalable architecture** with partitioning
- **Loose coupling** between exchange rate service and consumers
- **Real-time data distribution** to external systems

The producer-only pattern is common in microservices where one service publishes domain events for others to consume.