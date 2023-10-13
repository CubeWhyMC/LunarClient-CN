package org.cubewhy.lunarcn.loader.utils

import org.apache.logging.log4j.core.config.plugins.ResolverUtil
import org.cubewhy.lunarcn.loader.api.Hook
import org.cubewhy.lunarcn.loader.api.SubscribeHook
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.reflect.Modifier

object ClassUtils {
    fun <T : Any, A : Annotation> searchClassesByAnnotation(annotation: Class<A>, targetClass: Class<T>, pkg: String): MutableList<T> {
        val classes: List<Class<out T>> = resolvePackage(pkg, targetClass)
        val list = mutableListOf<T>()
        classes.forEach {
            if (it.getAnnotation(annotation) != null) {
                list.add(it.newInstance())
            }
        }
        return list
    }

    private val cachedClasses = mutableMapOf<String, Boolean>()

    /**
     * Allows you to check for existing classes with the [className]
     */
    fun hasClass(className: String): Boolean {
        return if (cachedClasses.containsKey(className)) {
            cachedClasses[className]!!
        } else try {
            Class.forName(className)
            cachedClasses[className] = true

            true
        } catch (e: ClassNotFoundException) {
            cachedClasses[className] = false

            false
        }
    }

    fun getObjectInstance(clazz: Class<*>): Any {
        clazz.declaredFields.forEach {
            if (it.name.equals("INSTANCE")) {
                return it.get(null)
            }
        }
        throw IllegalAccessException("This class not a kotlin object")
    }

    /**
     * scan classes with specified superclass like what Reflections do but with log4j [ResolverUtil]
     * @author liulihaocai
     */
    fun <T : Any> resolvePackage(packagePath: String, klass: Class<T>): List<Class<out T>> {
        // use resolver in log4j to scan classes in target package
        val resolver = ResolverUtil()

        // set class loader
        resolver.classLoader = klass.classLoader

        // set package to scan
        resolver.findInPackage(object : ResolverUtil.ClassTest() {
            override fun matches(type: Class<*>): Boolean {
                return true
            }
        }, packagePath)

        // use a list to cache classes
        val list = mutableListOf<Class<out T>>()

        for (resolved in resolver.classes) {
            resolved.declaredMethods.find {
                Modifier.isNative(it.modifiers)
            }?.let {
                val klass1 = it.declaringClass.typeName + "." + it.name
                throw UnsatisfiedLinkError(klass1 + "\n\tat ${klass1}(Native Method)") // we don't want native methods
            }
            // check if class is assignable from target class
            if (klass.isAssignableFrom(resolved) && !resolved.isInterface && !Modifier.isAbstract(resolved.modifiers)) {
                // add to list
                list.add(resolved as Class<out T>)
            }
        }

        return list
    }
    fun toClassNode(bytes: ByteArray): ClassNode {
        val classReader = ClassReader(bytes)
        val classNode = ClassNode()
        classReader.accept(classNode, 0)

        return classNode
    }

    /**
     * Write class node to bytes
     *
     * @param classNode ClassNode of class
     */
    fun toBytes(classNode: ClassNode): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        classNode.accept(classWriter)

        return classWriter.toByteArray()
    }
}
