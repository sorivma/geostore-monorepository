package sorivma.geosearchindexer.infrastructure.config.opensearch

import jakarta.annotation.PostConstruct
import org.opensearch.client.opensearch.OpenSearchClient
import org.opensearch.client.opensearch._types.analysis.TokenFilter
import org.opensearch.client.opensearch._types.mapping.TypeMapping
import org.opensearch.client.opensearch.indices.IndexSettings
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sorivma.geosearchindexer.infrastructure.config.opensearch.properties.OpenSearchProperties

@Component
class OpensearchIndexInitializer(
    private val client: OpenSearchClient,
    private val props: OpenSearchProperties
) {

    private val log = LoggerFactory.getLogger(OpensearchIndexInitializer::class.java)

    @PostConstruct
    fun initIndex() {
        if (!props.initialize) {
            log.info("Index initialization skipped (opensearch.initialize = false)")
            return
        }

        log.info("Checking if index '${props.index}' exists in OpenSearch...")
        val exists = client.indices().exists { it.index(props.index) }.value()

        if (exists) {
            log.info("Index '${props.index}' already exists, skipping creation.")
            return
        }

        log.info("Creating index '${props.index}'...")

        val mapping = buildDefaultMapping()
        val settings = buildIndexSetting()

        client.indices().create { req ->
            req.index(props.index)
                .settings(settings)
                .mappings(mapping)
        }

        log.info("Index '${props.index}' successfully created.")
    }

    private fun buildDefaultMapping(): TypeMapping {
        return TypeMapping.Builder()
            .properties("anyText") {
                it.text { t ->
                    t.analyzer("autocomplete_analyzer")
                        .searchAnalyzer("autocomplete_search_analyzer")
                }
            }
            .properties("objectId") { it.keyword { k -> k } }
            .properties("dcType") { it.keyword { k -> k } }
            .properties("region") { it.keyword { k -> k } }
            .properties("topicCategory") { it.keyword { k -> k } }
            .properties("temporalExtent") {
                it.`object` { obj -> obj }
            }
            .properties("indexedAt") { it.date { date -> date } }
            .build()
    }

    private fun buildIndexSetting(): IndexSettings {
        return IndexSettings.Builder()
            .analysis { analysis ->
                analysis.analyzer("autocomplete_analyzer") {
                    it.custom { custom ->
                        custom.tokenizer("standard")
                            .filter("lowercase", "asciifolding", "edge_ngram_filter")
                    }
                }
                    .analyzer("autocomplete_search_analyzer") {
                        it.custom { custom ->
                            custom
                                .tokenizer("standard")
                                .filter("lowercase", "asciifolding")
                        }
                    }
                    .filter("edge_ngram_filter") {
                        it.definition { definition ->
                            definition.edgeNgram { edge ->
                                edge
                                    .minGram(1)
                                    .maxGram(20)
                            }
                        }
                    }
            }.build()
    }
}