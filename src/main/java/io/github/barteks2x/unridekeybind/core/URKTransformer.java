/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.barteks2x.unridekeybind.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class URKTransformer implements IClassTransformer {

    @Override public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.client.entity.EntityPlayerSP".equals(transformedName)) {
            return transformPlayer(basicClass);
        }
        return basicClass;
    }

    private byte[] transformPlayer(byte[] bytes) {
        final String onUpdate = Mappings.getNameFromSrg("func_70071_h_");
        ClassReader cr = new ClassReader(bytes);

        ClassWriter cw = new ClassWriter(cr, 0);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
            @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (!name.equals(onUpdate)) {
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
                return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                    @Override public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                        if (!owner.equals("net/minecraft/util/MovementInput") || !name.equals(Mappings.getNameFromSrg("field_78899_d"))) {
                            super.visitFieldInsn(opcode, owner, name, desc);
                            return;
                        }
                        // to get "sneak" field, the stack contains the MovementInput instance, pop it
                        super.visitInsn(Opcodes.POP);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                                "io/github/barteks2x/unridekeybind/core/CoreHooks", "shouldDismount", "()Z", false);
                    }
                };
            }
        };

        cr.accept(cv, 0);

        return cw.toByteArray();
    }
}
