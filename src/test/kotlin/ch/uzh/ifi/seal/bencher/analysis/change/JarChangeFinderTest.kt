package ch.uzh.ifi.seal.bencher.analysis.change

import ch.uzh.ifi.seal.bencher.Class
import ch.uzh.ifi.seal.bencher.analysis.JarTestHelper
import ch.uzh.ifi.seal.bencher.fileResource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JarChangeFinderTest {

    private fun nonJMHGenerated(s: String): Boolean =
            !s.contains("generated")

    private fun nonJMHGenerated(c: Change): Boolean = when (c) {
        is MethodChange -> nonJMHGenerated(c.method.clazz)
        is ClassHeaderChange -> nonJMHGenerated(c.clazz.name)
        is ClassFieldChange -> nonJMHGenerated(c.clazz.name)
        is ClassMethodChange -> nonJMHGenerated(c.clazz.name)
        is DeletionChange -> nonJMHGenerated(c.type)
        is AdditionChange -> nonJMHGenerated(c.type)
    }


    private fun filterJMHGeneratedChanges(cs: Iterable<Change>) =
            cs.filter { nonJMHGenerated(it) }

    @Test
    fun noChanges() {
        val f = JarChangeFinder(pkgPrefix = pkgPrefix)
        val eChanges = f.changes(j1.absoluteFile, j1.absoluteFile)
        if (eChanges.isLeft()) {
            Assertions.fail<String>("Could not get changes: ${eChanges.left().get()}")
        }
        val changes = eChanges.right().get()
        Assertions.assertTrue(changes.isEmpty())
    }

    @Test
    fun changes() {
        val f = JarChangeFinder(pkgPrefix = pkgPrefix)
        val eChanges = f.changes(j1.absoluteFile, j2.absoluteFile)
        if (eChanges.isLeft()) {
            Assertions.fail<String>("Could not get changes: ${eChanges.left().get()}")
        }
        val allChanges = eChanges.right().get()

        // filter JMH-generated changes
        val changes = filterJMHGeneratedChanges(allChanges)

        Assertions.assertEquals(10, changes.size)

        // MethodChange(method=Benchmark(clazz=org.sample.BenchParameterized, name=bench1, params=[], jmhParams=[(str, 1), (str, 2), (str, 3)]))
        val containsB1Change = changes.contains(MethodChange(method = JarTestHelper.BenchParameterized.bench1))
        Assertions.assertTrue(containsB1Change, "No bench1 change")

        // MethodChange(method=PlainMethod(clazz=org.sample.core.CoreA, name=m, params=[]))
        val containsCoreAmChange = changes.contains(MethodChange(method = JarTestHelper.CoreA.m))
        Assertions.assertTrue(containsCoreAmChange, "No CoreA.m change")

        // MethodChange(method=PlainMethod(clazz=org.sample.core.CoreA, name=<init>, params=[java.lang.String, org.sample.core.CoreI]))
        val containsCoreAinitChange = changes.contains(MethodChange(method = JarTestHelper.CoreA.constructor))
        Assertions.assertTrue(containsCoreAinitChange, "No CoreA.<init> change")

        // AdditionChange(type=ClassFieldChange(clazz=Class(file=, name=org.sample.core.CoreA), field=additionalString))
        val addChange = AdditionChange(
                type = ClassFieldChange(
                        clazz = Class(name = JarTestHelper.CoreA.fqn),
                        field = "additionalString"
                )
        )
        val containsCoreAadditionalStringChange = changes.contains(addChange)
        Assertions.assertTrue(containsCoreAadditionalStringChange, "No CoreA.additionalString change")

        // MethodChange(method=PlainMethod(clazz=org.sample.core.CoreC, name=m, params=[]))
        val containsCoreCmChange = changes.contains(MethodChange(method = JarTestHelper.CoreC.m))
        Assertions.assertTrue(containsCoreCmChange, "No CoreC.m change")


        // AdditionChange(type=ClassHeaderChange(clazz=Class(name=org.sample.core.CoreE)))
        val addChangeCoreE = AdditionChange(type = ClassHeaderChange(clazz = Class(name=JarTestHelper.CoreE.fqn)))
        val containsNewCoreE = changes.contains(addChangeCoreE)
        Assertions.assertTrue(containsNewCoreE, "No CoreE addition change")

        // AdditionChange(type=ClassHeaderChange(clazz=Class(name=org.sample.NestedBenchmark$Bench1)))
        val addChangeB1 = AdditionChange(type = ClassHeaderChange(clazz = Class(name = JarTestHelper.NestedBenchmark.Bench1.fqn)))
        val containsNewB1 = changes.contains(addChangeB1)
        Assertions.assertTrue(containsNewB1, "No NestedBenchmark.Bench1 addition change")

        // AdditionChange(type=ClassHeaderChange(clazz=Class(name=org.sample.NestedBenchmark$Bench3$Bench32)))
        val addChangeB32 = AdditionChange(type = ClassHeaderChange(clazz = Class(name = JarTestHelper.NestedBenchmark.Bench3.Bench32.fqn)))
        val containsNewB32 = changes.contains(addChangeB32)
        Assertions.assertTrue(containsNewB32, "No NestedBenchmark.Bench3.Bench32 addition change")

        // AdditionChange(type=ClassHeaderChange(clazz=Class(name=org.sample.NestedBenchmark$Bench3)))
        val addChangeB3 = AdditionChange(type = ClassHeaderChange(clazz = Class(name = JarTestHelper.NestedBenchmark.Bench3.fqn)))
        val containsNewB3 = changes.contains(addChangeB3)
        Assertions.assertTrue(containsNewB3, "No NestedBenchmark.Bench3 addition change")


        // AdditionChange(type=ClassHeaderChange(clazz=Class(name=org.sample.NestedBenchmark)))
        val addChangeNB = AdditionChange(type = ClassHeaderChange(clazz = Class(name = JarTestHelper.NestedBenchmark.fqn)))
        val containsNewNB = changes.contains(addChangeNB)
        Assertions.assertTrue(containsNewNB, "No NestedBenchmark addition change")
    }

    companion object {
        val pkgPrefix = "org.sample"
        val j1 = JarTestHelper.jar4BenchsJmh121.fileResource()
        val j2 = JarTestHelper.jar4BenchsJmh121v2.fileResource()
    }

}
