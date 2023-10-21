package com.qosocial.qosocialapi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    String secretName = "v1mySQLConnection";
    Region region = Region.of("us-east-1");

    @Bean
    public DataSource dataSource() throws JsonProcessingException {
        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            throw e;
        }

        String secret = getSecretValueResponse.secretString();


        // Parse the secret JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode secretJson = objectMapper.readTree(secret);

        // Extract the values from the JSON
        String username = secretJson.get("username").asText();
        String password = secretJson.get("password").asText();
        String host = secretJson.get("host").asText();
        String port = secretJson.get("port").asText();
        String dbname = secretJson.get("dbname").asText();


        // Construct the JDBC URL
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, dbname);

        // Create and return the data source
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;

    }
}

