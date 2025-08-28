package com.infobank.multiagentplatform.app.config;

import com.infobank.multiagentplatform.domain.agent.type.enumtype.AgentStatus;
import com.infobank.multiagentplatform.domain.agent.type.enumtype.ProtocolType;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;

/**
 * R2DBC Configuration for PostgreSQL enum types support
 */
@Slf4j
@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    private final ConnectionFactory connectionFactory;

    public R2dbcConfiguration(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    @NonNull
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = Arrays.asList(
                // ProtocolType converters
                new ProtocolTypeToStringConverter(),
                new StringToProtocolTypeConverter(),
                // AgentStatus converters  
                new AgentStatusToStringConverter(),
                new StringToAgentStatusConverter()
        );

        log.info("R2DBC Custom Converters registered: {}", converters.size());
        return new R2dbcCustomConversions(getStoreConversions(), converters);
    }

    /**
     * Convert ProtocolType enum to String for database storage
     */
    @WritingConverter
    static class ProtocolTypeToStringConverter implements Converter<ProtocolType, String> {
        @Override
        public String convert(@NonNull ProtocolType source) {
            String result = source.name();
            log.info("Converting ProtocolType to String: {} -> {}", source, result);
            return result;
        }
    }

    /**
     * Convert String from database to ProtocolType enum
     */
    @ReadingConverter
    static class StringToProtocolTypeConverter implements Converter<String, ProtocolType> {
        @Override
        public ProtocolType convert(@NonNull String source) {
            ProtocolType result = ProtocolType.valueOf(source.toUpperCase());
            log.debug("Converting String to ProtocolType: {} -> {}", source, result);
            return result;
        }
    }

    /**
     * Convert AgentStatus enum to String for database storage
     */
    @WritingConverter
    static class AgentStatusToStringConverter implements Converter<AgentStatus, String> {
        @Override
        public String convert(@NonNull AgentStatus source) {
            String result = source.name();
            log.debug("Converting AgentStatus to String: {} -> {}", source, result);
            return result;
        }
    }

    /**
     * Convert String from database to AgentStatus enum
     */
    @ReadingConverter
    static class StringToAgentStatusConverter implements Converter<String, AgentStatus> {
        @Override
        public AgentStatus convert(@NonNull String source) {
            AgentStatus result = AgentStatus.valueOf(source.toUpperCase());
            log.debug("Converting String to AgentStatus: {} -> {}", source, result);
            return result;
        }
    }
}
