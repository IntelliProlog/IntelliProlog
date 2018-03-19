package ch.eif.intelliprolog.module;

import ch.eif.intelliprolog.PrologIcons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PrologModuleType extends ModuleType<PrologModuleBuilder> {

    private static final String ID = "PROLOG_MODULE_TYPE";

    public PrologModuleType() {
        super(ID);
    }

    public static PrologModuleType getInstance() {
        return (PrologModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public PrologModuleBuilder createModuleBuilder() {
        return new PrologModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Prolog";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "GNU Prolog Module Type";
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return PrologIcons.FILE;
    }
}
