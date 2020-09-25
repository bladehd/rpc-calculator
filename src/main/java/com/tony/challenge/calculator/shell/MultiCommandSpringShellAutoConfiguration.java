package com.tony.challenge.calculator.shell;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.Shell;
import org.springframework.shell.SpringShellAutoConfiguration;
import org.springframework.shell.result.ResultHandlerConfig;

/**
 * @author tony.chen
 */
@Configuration
@Import({ResultHandlerConfig.class})
public class MultiCommandSpringShellAutoConfiguration extends SpringShellAutoConfiguration {

    @Override
    @Bean
    public Shell shell(@Qualifier("main") ResultHandler resultHandler) {
        return new MultiCommandShell(resultHandler);
    }
}
