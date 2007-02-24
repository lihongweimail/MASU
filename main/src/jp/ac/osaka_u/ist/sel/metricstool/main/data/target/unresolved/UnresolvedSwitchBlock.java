package jp.ac.osaka_u.ist.sel.metricstool.main.data.target.unresolved;


import jp.ac.osaka_u.ist.sel.metricstool.main.security.MetricsToolSecurityManager;


/**
 * switch �u���b�N��\���N���X
 * 
 * @author y-higo
 * 
 */
public class UnresolvedSwitchBlock extends UnresolvedBlock {

    /**
     * switch �u���b�N����������
     * 
     */
    public UnresolvedSwitchBlock() {
        MetricsToolSecurityManager.getInstance().checkAccess();
    }

    /**
     * ����switch �u���b�N�� case �G���g����ǉ�����
     * 
     * @param innerBlock �ǉ����� case �G���g��
     */
    @Override
    public final void addInnerBlock(final UnresolvedBlock innerBlock) {

        MetricsToolSecurityManager.getInstance().checkAccess();
        if (null == innerBlock) {
            throw new NullPointerException();
        }

        if (!(innerBlock instanceof UnresolvedCaseEntry)) {
            throw new IllegalArgumentException(
                    "Inner block of switch statement must be case or default entry!");
        }

        super.addInnerBlock(innerBlock);
    }
}