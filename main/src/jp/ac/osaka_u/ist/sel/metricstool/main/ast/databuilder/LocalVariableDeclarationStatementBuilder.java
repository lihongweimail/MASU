package jp.ac.osaka_u.ist.sel.metricstool.main.ast.databuilder;


import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.LocalVariableStateManager;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.StateChangeEvent;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.StateChangeEvent.StateChangeEventType;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.statemanager.VariableDefinitionStateManager.VARIABLE_STATE;
import jp.ac.osaka_u.ist.sel.metricstool.main.ast.visitor.AstVisitEvent;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.ExpressionInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.LocalSpaceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedExpressionInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLocalSpaceInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedLocalVariableUsageInfo;
import jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved.UnresolvedVariableDeclarationStatementInfo;


public class LocalVariableDeclarationStatementBuilder extends
        CompoundDataBuilder<UnresolvedVariableDeclarationStatementInfo> {

    public LocalVariableDeclarationStatementBuilder(final LocalVariableBuilder variableBuilder,
            final BuildDataManager buildDataManager) {

        if (null == variableBuilder) {
            throw new IllegalArgumentException("variableBuilder is null.");
        }

        this.variableBuilder = variableBuilder;

        this.buildDataManager = buildDataManager;
        
        this.addStateManager(new LocalVariableStateManager());
    }

    @Override
    public void stateChangend(StateChangeEvent<AstVisitEvent> event) {
        final StateChangeEventType eventType = event.getType();
        if (eventType.equals(VARIABLE_STATE.EXIT_VARIABLE_DEF)) {
            final AstVisitEvent trigger = event.getTrigger();
            this.buildVariableDeclarationStatement(this.variableBuilder.getLastDeclarationUsage(),
                    this.variableBuilder.getLastBuiltExpression(), trigger.getStartLine(), trigger
                            .getStartColumn(), trigger.getEndLine(), trigger.getEndColumn());
        }
    }

    private void buildVariableDeclarationStatement(
            final UnresolvedLocalVariableUsageInfo declarationUsage,
            final UnresolvedExpressionInfo<? extends ExpressionInfo> initializerExpression,
            final int fromLine, final int fromColumn, final int toLine, final int toColumn) {
        final UnresolvedVariableDeclarationStatementInfo declarationStatement = new UnresolvedVariableDeclarationStatementInfo(
                declarationUsage, initializerExpression);
        declarationStatement.setFromLine(fromLine);
        declarationStatement.setFromColumn(fromColumn);
        declarationStatement.setToLine(toLine);
        declarationStatement.setToColumn(toColumn);

        final UnresolvedLocalSpaceInfo<? extends LocalSpaceInfo> currentLocal = this.buildDataManager
                .getCurrentLocalSpace();
        if (null != currentLocal) {
            currentLocal.addStatement(declarationStatement);
        }

    }

    private final LocalVariableBuilder variableBuilder;

    private final BuildDataManager buildDataManager;
}
