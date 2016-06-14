package smolok.lib.spark

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SparkConfiguration {

    @Bean(destroyMethod = 'close')
    JavaSparkContext sparkContext(@Value('${spark.master:spark://localhost:7077}') String master) {
        def sparkConfig = new SparkConf().setMaster(master).setAppName('SparkJob')
        new JavaSparkContext(sparkConfig)
    }

}