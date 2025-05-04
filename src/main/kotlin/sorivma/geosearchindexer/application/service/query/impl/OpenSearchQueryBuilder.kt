package sorivma.geosearchindexer.application.service.query.impl

import org.opensearch.client.json.JsonData
import org.opensearch.client.opensearch._types.FieldValue
import org.opensearch.client.opensearch._types.SortOptions
import org.opensearch.client.opensearch._types.SortOrder
import org.opensearch.client.opensearch._types.query_dsl.Query
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType
import org.springframework.stereotype.Component
import sorivma.geosearchindexer.api.dto.SearchQueryRequest
import sorivma.geosearchindexer.application.service.query.QueryBuilder

@Component
class OpenSearchQueryBuilder : QueryBuilder<SearchQueryRequest> {

    override fun buildQuery(request: SearchQueryRequest): Query {
        val must = mutableListOf<Query>()

        must += Query.of { it ->
            it.terms { t ->
                t.field("objectId")
                    .terms { ts ->
                        ts.value(request.objectIds.map { FieldValue.of(it.toString()) })
                    }
            }
        }

        if (request.query.isNotBlank()) {
            must += Query.of {
                it.multiMatch { mm ->
                    mm.query(request.query)
                        .fields("anyText^3")
                        .fuzziness("AUTO")
                        .type(TextQueryType.BestFields)
                }
            }
        }

        request.filters?.dcType?.let { dcType ->
            must += Query.of {
                it.term { t ->
                    t.field("dcType")
                        .value(FieldValue.of(dcType))
                }
            }
        }

        request.filters?.region?.let { region ->
            must += Query.of {
                it.term { t ->
                    t.field("region")
                        .value(FieldValue.of(region))
                }
            }
        }

        request.filters?.topicCategory?.takeIf { it.isNotEmpty() }?.let { categories ->
            must += Query.of {
                it.terms { t ->
                    t.field("topicCategory")
                        .terms { ts -> ts.value(categories.map(FieldValue::of)) }
                }
            }
        }

        request.temporal?.let { tf ->
            val range = RangeQuery.Builder().field("temporalExtent.start")
            tf.start?.let { range.gte(JsonData.of(it.toString())) }
            tf.end?.let { range.lte(JsonData.of(it.toString())) }
            must += Query.of { it.range(range.build()) }
        }

        return Query.of { q -> q.bool { b -> b.must(must) } }
    }

    override fun buildSort(request: SearchQueryRequest): List<SortOptions> {
        return listOf(
            SortOptions.of {
                it.field { f ->
                    f.field(request.sort.field)
                        .order(SortOrder.valueOf(request.sort.order.name))
                }
            }
        )
    }
}