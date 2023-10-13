package org.cubewhy.lunarcn.loader.utils

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.ASM9

class AccessWriter(private val access: Int, classVisitor: ClassVisitor?) : ClassVisitor(ASM9, classVisitor) {
    override fun visitField(access: Int, name: String?, desc: String?, signature: String?, value: Any?): FieldVisitor {
        return super.visitField(this.access, name, desc, signature, value)
    }
}