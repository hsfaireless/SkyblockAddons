package codes.biscuit.skyblockaddons.asm;

import codes.biscuit.skyblockaddons.asm.utils.TransformerClass;
import codes.biscuit.skyblockaddons.asm.utils.TransformerMethod;
import codes.biscuit.skyblockaddons.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RenderItemTransformer implements ITransformer {

    /**
     * {@link net.minecraft.client.renderer.entity.RenderItem}
     */
    @Override
    public String[] getClassName() {
        return new String[]{TransformerClass.RenderItem.getTransformerName()};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {


            // Objective:
            //
            // Find:
            //   this.renderItem(stack, model);
            //
            // Add after:
            //   RenderItemHook.renderArrowPoisonEffect(model, stack);

            if (TransformerMethod.renderItem.matches(methodNode)) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractNode = iterator.next();

                    if (abstractNode instanceof MethodInsnNode && (abstractNode.getOpcode() == Opcodes.INVOKESPECIAL || abstractNode.getOpcode() == Opcodes.INVOKEVIRTUAL)) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)abstractNode;

                        if (TransformerMethod.renderModel_IBakedModel_ItemStack.matches(methodInsnNode)) {

                            methodNode.instructions.insert(abstractNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "codes/biscuit/skyblockaddons/asm/hooks/RenderItemHook",
                                    "renderArrowPoisonEffect", "("+TransformerClass.IBakedModel.getName()+TransformerClass.ItemStack.getName()+")V", false));
                            methodNode.instructions.insert(abstractNode, new VarInsnNode(Opcodes.ALOAD, 1));
                            methodNode.instructions.insert(abstractNode, new VarInsnNode(Opcodes.ALOAD, 2));
                            break;

                        }
                    }
                }
            }
        }
    }
}
