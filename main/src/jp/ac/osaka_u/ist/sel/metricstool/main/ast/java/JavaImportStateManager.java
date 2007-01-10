package jp.ac.osaka_u.ist.sel.metricstool.main.ast.java;

import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.EnterExitStateManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.StateChangeEvent.StateChangeEventType;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.token.AstToken;

public class JavaImportStateManager extends EnterExitStateManager{
    public static enum IMPORT_STATE_CHANGE implements StateChangeEventType{
        ENTER_IMPORT,
        EXIT_IMPORT
    }
    
    @Override
    public  StateChangeEventType getEnterEventType() {
        return IMPORT_STATE_CHANGE.ENTER_IMPORT;
    }

    @Override
    public  StateChangeEventType getExitEventType() {
        return IMPORT_STATE_CHANGE.EXIT_IMPORT;
    }

    @Override
    protected boolean isStateChangeTriggerToken(AstToken token) {
        return token.equals(JavaAstToken.IMPORT);
    }
}