package ch.uzh.ifi.seal.bencher.analysis.weight

import ch.uzh.ifi.seal.bencher.Constants
import ch.uzh.ifi.seal.bencher.PlainMethod
import org.apache.commons.csv.CSVFormat
import org.funktionale.either.Either
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class CSVMethodWeighter(
        private val file: InputStream,
        val hasHeader: Boolean = false,
        val hasParams: Boolean = true,
        val del: Char = ';',
        val charset: String = Constants.defaultCharset
) : MethodWeighter {

    private var read: Boolean = false
    private lateinit var weights: MethodWeights

    override fun weights(): Either<String, MethodWeights> {
        if (!read) {
            val eWeights = read()
            if (eWeights.isRight()) {
                weights = eWeights.right().get()
                read = true
            }
            return eWeights
        }

        return Either.right(weights)
    }

    private fun read(): Either<String, MethodWeights> {
        val r = BufferedReader(InputStreamReader(file, charset))
        val format = CSVFormat.DEFAULT.withDelimiter(del)
        val headerFormat = if (hasHeader) {
            format.withHeader()
        } else if (hasParams) {
            format.withHeader(CSVMethodWeightConstants.clazz, CSVMethodWeightConstants.method, CSVMethodWeightConstants.params, CSVMethodWeightConstants.value)
        } else {
            format.withHeader(CSVMethodWeightConstants.clazz, CSVMethodWeightConstants.method, CSVMethodWeightConstants.value)
        }

        try {
            val p = headerFormat.parse(r)
            val methodPrios = p.records.mapNotNull rec@{ rec ->
                val c = rec.get(CSVMethodWeightConstants.clazz) ?: return@rec null
                val m = rec.get(CSVMethodWeightConstants.method) ?: return@rec null
                val vStr = rec.get(CSVMethodWeightConstants.value) ?: return@rec null
                val params = if (hasParams) {
                    params(rec.get(CSVMethodWeightConstants.params))
                } else {
                    listOf()
                }


                Pair(
                        PlainMethod(
                                clazz = c,
                                name = m,
                                params = params
                        ),
                        vStr.toDouble()
                )
            }.toMap()

            return Either.right(methodPrios)
        } catch (e: IOException) {
            return Either.left("Could not parse CSV file: ${e.message}")
        } catch (e: NumberFormatException) {
            return Either.left("Could not parse value into double: ${e.message}")
        }
    }

    private fun params(s: String?): List<String> =
            if (s == null) {
                listOf()
            } else {
                s.split(",")
            }
}
