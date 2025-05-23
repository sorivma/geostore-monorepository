package sorivma.geometadataservice.application.publisher.textGenerator.impl

import org.springframework.stereotype.Component
import sorivma.geometadataservice.application.publisher.textGenerator.AnyTextGenerator
import sorivma.geometadataservice.domain.model.GeoMetadata

@Component
class AnyTextGeneratorImpl : AnyTextGenerator {
    override fun fromMetadata(meta: GeoMetadata): String {
        val fragments = extractTextFromMap(meta.properties)

        return fragments
            .asSequence()
            .flatMap { tokenize(it) }
            .filter { it.length > 2 }
            .distinct()
            .sorted()
            .joinToString(" ")
    }

    private fun extractTextFromMap(map: Map<String, Any>?): List<String> {
        if (map == null) return emptyList()
        return map.entries
            .flatMap { (_, value) -> extractTextFromAny(value) }
    }

    private fun extractTextFromAny(value: Any?): List<String> = when (value) {
        is String -> listOf(value)
        is Number -> listOf(value.toString())
        is Boolean -> listOf(value.toString())
        is List<*> -> value.flatMap { extractTextFromAny(it) }
        is Map<*, *> -> extractTextFromMap(value as? Map<String, Any>)
        else -> emptyList()
    }

    private fun tokenize(text: String): List<String> {
        return text
            .lowercase()
            .replace(Regex("[^\\p{L}\\p{N}\\s]"), "")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
    }
}