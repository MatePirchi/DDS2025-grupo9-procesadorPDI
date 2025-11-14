package ar.edu.utn.dds.k3003.manejoWorkers;

import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.nio.charset.StandardCharsets;

@Service
public class ProcesadorCola {
    @Value("${rabbitmq.queue.name}")
    private String queue_name;

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;

    private Connection connection;
    private Channel channel;
    private final ObjectMapper objectMapper;
    
    public ProcesadorCola(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setVirtualHost(username);

            connection = factory.newConnection();
            channel = connection.createChannel();

            // Declarar la cola (durable=true para que sobreviva reinicios)
            channel.queueDeclare(queue_name, true, false, false, null);

            System.out.println("Conexión a RabbitMQ establecida: " + host + ":" + port);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con RabbitMQ", e);
        }
    }
    
    public void encolarPDI(PDIDTO pdiDTO) {
        try {
            String mensajeJson = objectMapper.writeValueAsString(pdiDTO);
            
            // Publicar mensaje (deliveryMode=2 para persistencia)
            channel.basicPublish("", queue_name,
                com.rabbitmq.client.MessageProperties.PERSISTENT_TEXT_PLAIN,
                mensajeJson.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("PDI encolado en RabbitMQ: " + pdiDTO.id());
        } catch (Exception e) {
            throw new RuntimeException("Error al encolar PDI en RabbitMQ", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            System.out.println("Conexión a RabbitMQ cerrada");
        } catch (Exception e) {
            System.err.println("Error al cerrar conexión RabbitMQ: " + e.getMessage());
        }
    }
}
