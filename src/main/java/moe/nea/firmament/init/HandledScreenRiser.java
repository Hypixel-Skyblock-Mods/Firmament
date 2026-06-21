
package moe.nea.firmament.init;

import moe.nea.firmament.mixins.ScreenInputEvents;
import moe.nea.firmament.mixins.customgui.PatchGenericScreen;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Consumer;

public class HandledScreenRiser extends RiserUtils {
	Intermediary.InterClass Screen = Intermediary.<Screen>intermediaryClass();
	Intermediary.InterClass KeyEvent = Intermediary.<KeyEvent>intermediaryClass();
	Intermediary.InterClass CharInput = Intermediary.<CharacterEvent>intermediaryClass();
	Intermediary.InterClass HandledScreen = Intermediary.<AbstractContainerScreen>intermediaryClass();
	Intermediary.InterClass AbstractContainerEventHandler = Intermediary.<AbstractContainerEventHandler>intermediaryClass();
	Intermediary.InterClass MouseButtonEvent = Intermediary.<MouseButtonEvent>intermediaryClass();
	Intermediary.InterMethod mouseScrolled = Intermediary.intermediaryMethod(
		GuiEventListener::mouseScrolled,
		Intermediary.ofClass(boolean.class),
		Intermediary.ofClass(double.class),
		Intermediary.ofClass(double.class),
		Intermediary.ofClass(double.class),
		Intermediary.ofClass(double.class)
	);
	Intermediary.InterMethod mouseClickedScreen = Intermediary.intermediaryMethod(
		GuiEventListener::mouseClicked,
		Intermediary.ofClass(boolean.class),
		MouseButtonEvent,
		Intermediary.ofClass(boolean.class)
	);
	Intermediary.InterMethod mouseReleased = Intermediary.intermediaryMethod(
		GuiEventListener::mouseReleased,
		Intermediary.ofClass(boolean.class),
		MouseButtonEvent
	);
	Intermediary.InterMethod mouseDragged = Intermediary.intermediaryMethod(
		GuiEventListener::mouseDragged,
		Intermediary.ofClass(boolean.class),
		MouseButtonEvent,
		Intermediary.ofClass(double.class),
		Intermediary.ofClass(double.class)
	);
	Intermediary.InterMethod keyReleased = Intermediary.intermediaryMethod(
		GuiEventListener::keyReleased,
		Intermediary.ofClass(boolean.class),
		KeyEvent
	);
	Intermediary.InterMethod charTyped = Intermediary.intermediaryMethod(
		GuiEventListener::charTyped,
		Intermediary.ofClass(boolean.class),
		CharInput
	);

	@Override
	public void addTinkerers() {
		addTransformation(HandledScreen, this::addMouseScrollSuper, true);
		addTransformation(Screen, this::addMouseScroll, true);
		addTransformation(Screen, this::addKeyReleased, true);
		addTransformation(Screen, this::addCharTyped, true);
		addTransformation(Screen, this::addMouseReleased, true);
		addTransformation(Screen, this::addMouseDragged, true);
		addTransformation(Screen, this::addMouseClicked, true);
	}

	private void addMouseScrollSuper(ClassNode classNode) { // TODO: what a shoddy manip
		var mouseScrolled = classNode.methods
			.stream()
			.filter(it -> Objects.equals(it.name, this.mouseScrolled.mapped()))
			.findAny()
			.orElseThrow();
		AbstractInsnNode lastReturn = null;
		for (AbstractInsnNode instruction : mouseScrolled.instructions) {
			if (instruction.getOpcode() == Opcodes.IRETURN)
				lastReturn = instruction;
			if (instruction.getOpcode() == Opcodes.INVOKESPECIAL
				&& instruction instanceof MethodInsnNode methodInsnNode
				&& methodInsnNode.name.equals(mouseScrolled.name)) {
				return;
			}
		}
		var loadInsns = new InsnList();
		loadInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
		// DLOAD 1-4, load the 4 argument doubles. Note that since doubles are two entries wide we skip 2 each time.
		loadInsns.add(new VarInsnNode(Opcodes.DLOAD, 1));
		loadInsns.add(new VarInsnNode(Opcodes.DLOAD, 3));
		loadInsns.add(new VarInsnNode(Opcodes.DLOAD, 5));
		loadInsns.add(new VarInsnNode(Opcodes.DLOAD, 7));

		// INVOKESPECIAL call super method
		loadInsns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Screen.mapped().getInternalName(),
			mouseScrolled.name, mouseScrolled.desc));

		mouseScrolled.instructions.insertBefore(lastReturn, loadInsns);
	}


	/// @see PatchGenericScreen#mouseScrolled_firmament
	void addMouseScroll(ClassNode classNode) {
		addSuperInjector(
			classNode, mouseScrolled.mapped(), mouseScrolled.mappedDesc(), Screen, AbstractContainerEventHandler, "mouseScrolled_firmament",
			insns -> {
				// ALOAD 0, load this
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// DLOAD 1-4, load the 4 argument doubles. Note that since doubles are two entries wide we skip 2 each time.
				insns.add(new VarInsnNode(Opcodes.DLOAD, 1));
				insns.add(new VarInsnNode(Opcodes.DLOAD, 3));
				insns.add(new VarInsnNode(Opcodes.DLOAD, 5));
				insns.add(new VarInsnNode(Opcodes.DLOAD, 7));
			});
	}

	/// @see PatchGenericScreen#keyReleased_firmament
	void addKeyReleased(ClassNode classNode) {
		addSuperInjector(
			classNode, keyReleased.mapped(), keyReleased.mappedDesc(), Screen, AbstractContainerEventHandler, "keyReleased_firmament",
			insns -> {
				// ALOAD 0, load this
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// ALOAD 1, load args
				insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			});
	}

	/// @see PatchGenericScreen#mouseReleased_firmament
	void addMouseReleased(ClassNode classNode) {
		addSuperInjector(
			classNode, mouseReleased.mapped(), mouseReleased.mappedDesc(), Screen, AbstractContainerEventHandler, "mouseReleased_firmament",
			insns -> {
				// ALOAD 0, load this
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// ALOAD 1, load args
				insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			});
	}

	/// @see PatchGenericScreen#mouseDragged_firmament
	void addMouseDragged(ClassNode classNode) {
		addSuperInjector(
			classNode, mouseDragged.mapped(), mouseDragged.mappedDesc(), Screen, AbstractContainerEventHandler, "mouseDragged_firmament",
			insns -> {
				// ALOAD 0, load this
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// ALOAD 1 load event
				insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
				// DLOAD 2, 4, load args (doubles)
				insns.add(new VarInsnNode(Opcodes.DLOAD, 2));
				insns.add(new VarInsnNode(Opcodes.DLOAD, 4));
			});
	}

	/// @see PatchGenericScreen#mouseClicked_firmament_customGui
	/// @see ScreenInputEvents#onMouseClicked_firmament_generic
	void addMouseClicked(ClassNode classNode) {
		Consumer<InsnList> loadInsns = insns -> {
			// load this
			insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
			// load mouse event
			insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			// load doubled
			insns.add(new VarInsnNode(Opcodes.ILOAD, 2));
		};
		addSuperInjector(
			classNode, mouseClickedScreen.mapped(), mouseClickedScreen.mappedDesc(),
			Screen, AbstractContainerEventHandler, "onMouseClicked_firmament_generic", loadInsns);
		addSuperInjector(
			classNode, mouseClickedScreen.mapped(), mouseClickedScreen.mappedDesc(),
			Screen, AbstractContainerEventHandler, "mouseClicked_firmament_customGui", loadInsns);
	}

	/// @see PatchGenericScreen#charTyped_firmament
	void addCharTyped(ClassNode classNode) {
		addSuperInjector(
			classNode, charTyped.mapped(), charTyped.mappedDesc(),
			Screen, AbstractContainerEventHandler, "charTyped_firmament",
			insns -> {
				// ALOAD 0, load this
				insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
				// ALOAD 1, load args
				insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
			});
	}

	void addSuperInjector(
		ClassNode classNode,
		String name,
		Type desc,
		Intermediary.InterClass currentClass,
		Intermediary.InterClass parentClass,
		String firmamentName,
		Consumer<InsnList> loadArgs
	) {
		var keyReleasedNode = findMethod(classNode, name, desc);
		if (keyReleasedNode == null) {
			keyReleasedNode = new MethodNode(
				Modifier.PUBLIC,
				name,
				desc.getDescriptor(),
				null,
				new String[0]
			);
			var insns = keyReleasedNode.instructions;
			loadArgs.accept(insns);
			// INVOKESPECIAL call super method
			insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, parentClass.mapped().getInternalName(),
				name, desc.getDescriptor()));
			// IRETURN return int on stack (booleans are int at runtime)
			insns.add(new InsnNode(Opcodes.IRETURN));
			classNode.methods.add(keyReleasedNode);
		}
		insertTrueHandler(keyReleasedNode, loadArgs, insns -> {
			// INVOKEVIRTUAL call custom handler
			insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
				currentClass.mapped().getInternalName(),
				firmamentName,
				desc.getDescriptor()));
		});

	}

	/**
	 * Insert a handler that roughly inserts the following code at the beginning of the instruction list:
	 * <code><pre>
	 * if (insertInvoke(insertLoads)) return true
	 * </pre></code>
	 *
	 * @param node         The method node to prepend the instructions to
	 * @param insertLoads  insert all the loads, including the {@code this} parameter
	 * @param insertInvoke insert the invokevirtual/invokestatic call
	 */
	void insertTrueHandler(MethodNode node,
	                       Consumer<InsnList> insertLoads,
	                       Consumer<InsnList> insertInvoke) {

		var insns = new InsnList();
		insertLoads.accept(insns);
		insertInvoke.accept(insns);
		// Create jump target (but not insert it yet)
		var jumpIfFalse = new LabelNode();
		// IFEQ (if returned boolean == 0), jump to jumpIfFalse
		insns.add(new JumpInsnNode(Opcodes.IFEQ, jumpIfFalse));
		// LDC 1 (as int, which is what booleans are at runtime)
		insns.add(new LdcInsnNode(1));
		// IRETURN return int on stack (booleans are int at runtime)
		insns.add(new InsnNode(Opcodes.IRETURN));
		insns.add(jumpIfFalse);
		node.instructions.insert(insns);
	}

}
