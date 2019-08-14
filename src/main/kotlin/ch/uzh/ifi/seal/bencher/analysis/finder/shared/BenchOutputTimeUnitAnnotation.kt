package ch.uzh.ifi.seal.bencher.analysis.finder.shared

import org.funktionale.option.Option
import java.util.concurrent.TimeUnit

class BenchOutputTimeUnitAnnotation {

    private var timeUnit = defaultTimeUnit
    fun timeUnit(): Option<TimeUnit> = timeUnit

    fun setValueEnum(name: String?, descriptor: String, value: String) {
        timeUnit = if (descriptor == bcTimeUnit) {
            try {
                Option.Some(TimeUnit.valueOf(value))
            } catch (e: IllegalArgumentException) {
                defaultTimeUnit
            }
        } else {
            defaultTimeUnit
        }
    }

    companion object {
        private val defaultTimeUnit = Option.empty<TimeUnit>()

        private const val valTimeUnit = "value"

        const val bcTimeUnit = "Ljava/util/concurrent/TimeUnit;"
    }
}