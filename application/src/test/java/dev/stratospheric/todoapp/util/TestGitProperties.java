package dev.stratospheric.todoapp.util;

import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class TestGitProperties {

  @Bean
  public GitProperties gitProperties() {
    Properties gitProperties = new Properties();

    gitProperties.put("branch", "main");
    gitProperties.put("commit.time","2023-02-15T22:41:38+0100");
    gitProperties.put("commit.id", "ce0c3165f50022f9e98ad0b9646bb400dacb6aaa");
    gitProperties.put("commit.id.abbrev", "ce0c316");

    return new GitProperties(gitProperties);
  }
}
