package org.cubewhy.lunarcn.loader.bootstrap

class AccessTransformer(private val classMap: Map<String, ByteArray>) : SafeTransformer {
    override fun transform(loader: ClassLoader, className: String, originalClass: ByteArray): ByteArray? {
        if (classMap.containsKey(className)) {
            return classMap[className] // replace class
        }
        return null;
    }
}