package jp.ac.osaka_u.ist.sel.metricstool.main.data.target;

import java.util.Set;

/**
 * �����̏���\���N���X
 * 
 * @author t-miyake
 *
 */
public class ExpressionStatementInfo extends SingleStatementInfo {

    /**
     * ���ƈʒu����^���ď�����
     * 
     * @param expression �������\�����鎮
     * @param fromLine �J�n�s
     * @param fromColumn �J�n��
     * @param toLine �I���s
     * @param toColumn �I����
     */
    public ExpressionStatementInfo(final ExpressionInfo expression, final int fromLine, final int fromColumn, final int toLine, final int toColumn) {
        super(fromLine, fromColumn, toLine, toColumn);

        if(null == expression) {
            throw new IllegalArgumentException("expression is null");
        }
        
        this.expression = expression;
    }

    
    /**
     * �������\�����鎮��Ԃ�
     * 
     * @return �������\�����鎮
     */
    public final ExpressionInfo getExpression() {
        return this.expression;
    }
    
    /* (non-Javadoc)
     * @see jp.ac.osaka_u.ist.sel.metricstool.main.data.target.StatementInfo#getVariableUsages()
     */
    @Override
    public Set<VariableUsageInfo<?>> getVariableUsages() {
        return this.getExpression().getVariableUsages();
    }
    
    /**
     * �������\�����鎮��ۑ����邽�߂̕ϐ�
     */
    private final ExpressionInfo expression;

}