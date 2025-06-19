package ru.shcherbanev.nlpservice.config;

import com.joestelmach.natty.Parser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NlpConfig {

    @Bean
    public StanfordCoreNLP stanfordCoreNLP() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("tokenize.language", "ru");
        props.setProperty("ner.applyFineGrained", "false");
        props.setProperty("tokenize.language", "en");
        return new StanfordCoreNLP(props);
    }

    @Bean
    public Parser parser() {
        return new Parser(TimeZone.getTimeZone("Europe/Moscow"));
    }

    @Bean
    public Executor executorPool() {
        return Executors.newFixedThreadPool(2);
    }

}
